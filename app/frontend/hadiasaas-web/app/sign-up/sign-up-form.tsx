"use client";

import { useState, useEffect } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
    useCreatePublicUserAccount,
    useGetSubscriptionPlans,
    useGetAppConfigurations,
} from "@api-client";
import type {
    SubscriptionPlan,
    SubscribeRequestBillingFrequencyEnumKey,
    AppConfiguration,
} from "@api-client";
import { axiosInstance } from "@api-client";
import {
    Elements,
    PaymentElement,
    useStripe,
    useElements,
} from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import {
    Check,
    Zap,
    AlertCircle,
    Loader2,
    CreditCard,
    Lock,
} from "lucide-react";
import { signIn, getSession } from "next-auth/react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { toast } from "sonner";
import { IconFacebook, IconGithub } from "@/assets/brand-icons";
import { setApiAccessToken } from "@/lib/apiclient-interceptors";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { PasswordInput } from "@/components/password-input";

// ── helpers ───────────────────────────────────────────────────────────────────

function currencySymbol(code?: string) {
    if (!code) return "$";
    try {
        return (0)
            .toLocaleString("en", {
                style: "currency",
                currency: code,
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
            })
            .replace(/\d/g, "")
            .trim();
    } catch {
        return code;
    }
}

function yearlyDiscount(plan: SubscriptionPlan): number | null {
    const monthly = plan.monthlyPrice;
    const yearly = plan.yearlyPrice;
    if (!monthly || !yearly || monthly === 0 || yearly >= monthly) return null;
    return Math.round(((monthly - yearly) / monthly) * 100);
}

// ── types ─────────────────────────────────────────────────────────────────────

// Flow 1 (landing → sign-up?planId=X): account → checkout
// Flow 2 (direct /sign-up):            account → plan → checkout
type Step = "account" | "plan" | "checkout";

export interface SignUpFormProps {
    initialPlanId?: number;
    initialBilling?: SubscribeRequestBillingFrequencyEnumKey;
}

// ── NarrowCard ────────────────────────────────────────────────────────────────

function NarrowCard({ children }: { children: React.ReactNode }) {
    return (
        <div className="mx-auto w-full max-w-sm rounded-xl border bg-card p-6 shadow-sm">
            {children}
        </div>
    );
}

// ── Step 1: Account Creation (always first in both flows) ─────────────────────

const formSchema = z
    .object({
        firstName: z.string().min(1, "Please enter your first name"),
        lastName: z.string().min(1, "Please enter your last name"),
        email: z.email({
            error: (iss) =>
                iss.input === "" ? "Please enter your email" : undefined,
        }),
        password: z
            .string()
            .min(1, "Please enter your password")
            .min(8, "Password must be at least 8 characters long")
            .regex(
                /^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$/,
                "Password must include upper, lower, number, and special character"
            ),
        confirmPassword: z.string().min(1, "Please confirm your password"),
    })
    .refine((data) => data.password === data.confirmPassword, {
        message: "Passwords don't match.",
        path: ["confirmPassword"],
    });

type FormValues = z.infer<typeof formSchema>;

interface AccountStepProps {
    /** Pre-selected plan from URL — null in flow 2 (plan chosen after account). */
    plan: SubscriptionPlan | null;
    isPlanLoading: boolean;
    billing: SubscribeRequestBillingFrequencyEnumKey;
    onSuccess: () => void;
}

function AccountStep({
    plan,
    isPlanLoading,
    billing,
    onSuccess,
}: AccountStepProps) {
    const router = useRouter();
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const createPublicUserAccount = useCreatePublicUserAccount();

    const form = useForm<FormValues>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            firstName: "",
            lastName: "",
            email: "",
            password: "",
            confirmPassword: "",
        },
    });

    async function onSubmit(data: FormValues) {
        setIsLoading(true);
        setError(null);

        // Track separately so the catch block knows what stage we failed at.
        let accountCreated = false;

        try {
            await createPublicUserAccount.mutateAsync({
                data: {
                    firstName: data.firstName,
                    lastName: data.lastName,
                    email: data.email,
                    password: data.password,
                },
            });
            accountCreated = true;

            const result = await signIn("credentials", {
                email: data.email,
                password: data.password,
                redirect: false,
            });

            if (result?.error) {
                // Account was created but auto-sign-in failed (e.g. email
                // verification required). Send to sign-in page.
                toast.success("Account created! Please sign in to continue.");
                router.push("/sign-in");
                return;
            }

            // Prime the API client token so the next step can call
            // authenticated endpoints immediately.
            const session = await getSession();
            setApiAccessToken(session?.accessToken);

            onSuccess();
        } catch (err) {
            if (accountCreated) {
                // Registration succeeded but sign-in threw — redirect to
                // sign-in so the user isn't stuck.
                toast.success("Account created! Please sign in to continue.");
                router.push("/sign-in");
            } else {
                // Registration itself failed — show the server message if any.
                const serverMsg =
                    (
                        err as {
                            response?: { data?: { message?: string } };
                        }
                    )?.response?.data?.message ?? null;
                setError(
                    serverMsg ?? "Unable to create account. Please try again."
                );
            }
        } finally {
            setIsLoading(false);
        }
    }

    const symbol = currencySymbol(plan?.currencyCode);
    const isFree = (plan?.monthlyPrice ?? 0) === 0;
    const displayPrice =
        billing === "YEARLY" ? plan?.yearlyPrice : plan?.monthlyPrice;

    return (
        <div className="grid gap-5">
            {/* Header */}
            <div>
                <h2 className="text-lg font-semibold tracking-tight">
                    Create an account
                </h2>
                <p className="mt-1 text-sm text-muted-foreground">
                    Already have an account?{" "}
                    <Link
                        href="/sign-in"
                        className="underline underline-offset-4 hover:text-primary"
                    >
                        Sign In
                    </Link>
                </p>
            </div>

            {/* Optional plan banner — only when plan is pre-selected from URL */}
            {(plan || isPlanLoading) && (
                <div className="flex items-center justify-between rounded-lg border bg-muted/40 px-3 py-2.5 text-sm">
                    {isPlanLoading ? (
                        <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
                    ) : plan ? (
                        <>
                            <span className="font-medium">{plan.title}</span>
                            <span className="text-muted-foreground">
                                {isFree
                                    ? "Free"
                                    : `${symbol}${displayPrice}/mo · ${billing === "YEARLY" ? "Yearly" : "Monthly"}`}
                            </span>
                        </>
                    ) : null}
                </div>
            )}

            {/* Form */}
            <Form {...form}>
                <form
                    onSubmit={form.handleSubmit(onSubmit)}
                    className="grid gap-3"
                >
                    <FormField
                        control={form.control}
                        name="firstName"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>First Name</FormLabel>
                                <FormControl>
                                    <Input placeholder="John" {...field} />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="lastName"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Last Name</FormLabel>
                                <FormControl>
                                    <Input placeholder="Doe" {...field} />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="email"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Email</FormLabel>
                                <FormControl>
                                    <Input
                                        placeholder="name@example.com"
                                        {...field}
                                    />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="password"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Password</FormLabel>
                                <FormControl>
                                    <PasswordInput
                                        placeholder="********"
                                        {...field}
                                    />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                    <FormField
                        control={form.control}
                        name="confirmPassword"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Confirm Password</FormLabel>
                                <FormControl>
                                    <PasswordInput
                                        placeholder="********"
                                        {...field}
                                    />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    {error && (
                        <p className="text-sm text-destructive">{error}</p>
                    )}

                    <Button className="mt-1" disabled={isLoading}>
                        {isLoading && (
                            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        )}
                        Create Account
                    </Button>

                    <div className="relative my-1">
                        <div className="absolute inset-0 flex items-center">
                            <span className="w-full border-t" />
                        </div>
                        <div className="relative flex justify-center text-xs uppercase">
                            <span className="bg-card px-2 text-muted-foreground">
                                Or continue with
                            </span>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-2">
                        <Button
                            variant="outline"
                            className="w-full"
                            type="button"
                            disabled
                        >
                            <IconGithub className="h-4 w-4" /> GitHub
                        </Button>
                        <Button
                            variant="outline"
                            className="w-full"
                            type="button"
                            disabled
                        >
                            <IconFacebook className="h-4 w-4" /> Facebook
                        </Button>
                    </div>
                </form>
            </Form>

            <p className="text-center text-xs text-muted-foreground">
                By creating an account, you agree to our{" "}
                <a
                    href="/terms"
                    className="underline underline-offset-4 hover:text-primary"
                >
                    Terms of Service
                </a>{" "}
                and{" "}
                <a
                    href="/privacy"
                    className="underline underline-offset-4 hover:text-primary"
                >
                    Privacy Policy
                </a>
                .
            </p>
        </div>
    );
}

// ── Step 2 (flow 2 only): Plan Selection ──────────────────────────────────────

interface PlanSelectionStepProps {
    onSelect: (
        plan: SubscriptionPlan,
        billing: SubscribeRequestBillingFrequencyEnumKey
    ) => void;
}

function PlanSelectionStep({ onSelect }: PlanSelectionStepProps) {
    const [yearly, setYearly] = useState(false);
    const { data, isPending, isError } = useGetSubscriptionPlans({ size: 50 });

    const plans = (data?.items ?? [])
        .filter((p) => p.active)
        .sort((a, b) => (a.monthlyPrice ?? 0) - (b.monthlyPrice ?? 0));

    const highlightedIndex = Math.floor(plans.length / 2);

    const hasYearlyPricing = plans.some(
        (p) =>
            p.yearlyPrice !== undefined &&
            p.yearlyPrice !== null &&
            p.yearlyPrice !== p.monthlyPrice
    );

    if (isPending) {
        return (
            <div className="flex items-center justify-center py-16">
                <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
            </div>
        );
    }

    if (isError) {
        return (
            <div className="flex flex-col items-center gap-3 py-16 text-center">
                <AlertCircle className="h-8 w-8 text-destructive" />
                <p className="text-sm text-muted-foreground">
                    Could not load plans. Please try again later.
                </p>
            </div>
        );
    }

    return (
        <div className="grid w-full gap-6">
            <div className="text-center">
                <h2 className="text-2xl font-semibold tracking-tight">
                    Choose your plan
                </h2>
                <p className="mt-1 text-sm text-muted-foreground">
                    Select the plan that works best for you.
                </p>
            </div>

            {hasYearlyPricing && (
                <div className="flex items-center justify-center gap-3">
                    <span
                        className={cn(
                            "text-sm font-medium transition-colors",
                            !yearly
                                ? "text-foreground"
                                : "text-muted-foreground"
                        )}
                    >
                        Monthly
                    </span>
                    <button
                        type="button"
                        onClick={() => setYearly((v) => !v)}
                        className={cn(
                            "relative h-6 w-11 rounded-full transition-colors duration-300 focus:outline-none focus-visible:ring-2 focus-visible:ring-ring",
                            yearly ? "bg-primary" : "bg-muted"
                        )}
                        role="switch"
                        aria-checked={yearly}
                    >
                        <span
                            className={cn(
                                "absolute top-0.5 left-0.5 h-5 w-5 rounded-full bg-white shadow transition-transform duration-300",
                                yearly ? "translate-x-5" : "translate-x-0"
                            )}
                        />
                    </button>
                    <span
                        className={cn(
                            "text-sm font-medium transition-colors",
                            yearly ? "text-foreground" : "text-muted-foreground"
                        )}
                    >
                        Yearly
                    </span>
                </div>
            )}

            <div
                className={cn(
                    "grid gap-4",
                    plans.length === 1
                        ? "mx-auto max-w-xs"
                        : plans.length === 2
                          ? "sm:grid-cols-2"
                          : "sm:grid-cols-3"
                )}
            >
                {plans.map((plan, i) => {
                    const symbol = currencySymbol(plan.currencyCode);
                    const displayPrice = yearly
                        ? plan.yearlyPrice
                        : plan.monthlyPrice;
                    const isFree = (plan.monthlyPrice ?? 0) === 0;
                    const discount = yearlyDiscount(plan);
                    const highlighted = i === highlightedIndex;

                    return (
                        <div
                            key={plan.id}
                            className={cn(
                                "relative rounded-xl border p-5 transition-all",
                                highlighted
                                    ? "border-primary shadow-md"
                                    : "border-border hover:border-primary/50"
                            )}
                        >
                            {highlighted && (
                                <div className="mb-3">
                                    <span className="inline-flex items-center gap-1 rounded-full border border-primary/30 bg-primary/10 px-2.5 py-0.5 text-xs font-semibold text-primary">
                                        <Zap className="h-3 w-3" />
                                        Most popular
                                    </span>
                                </div>
                            )}

                            <h3 className="font-semibold">{plan.title}</h3>
                            <p className="mt-1 text-xs text-muted-foreground">
                                {plan.description}
                            </p>

                            <div className="mt-4 mb-5">
                                {displayPrice !== undefined &&
                                displayPrice !== null ? (
                                    <>
                                        <div className="flex items-end gap-1">
                                            <span className="text-3xl font-extrabold tracking-tight">
                                                {isFree
                                                    ? "Free"
                                                    : `${symbol}${displayPrice}`}
                                            </span>
                                            {!isFree && (
                                                <span className="mb-1 text-xs text-muted-foreground">
                                                    /mo
                                                </span>
                                            )}
                                        </div>
                                        {yearly &&
                                            !isFree &&
                                            discount !== null && (
                                                <p className="mt-1 text-xs text-muted-foreground">
                                                    Save{" "}
                                                    <span className="font-medium text-emerald-600 dark:text-emerald-400">
                                                        {discount}%
                                                    </span>{" "}
                                                    vs monthly
                                                </p>
                                            )}
                                    </>
                                ) : (
                                    <div className="text-2xl font-extrabold tracking-tight">
                                        Custom
                                    </div>
                                )}
                            </div>

                            <Button
                                type="button"
                                className="w-full"
                                variant={highlighted ? "default" : "outline"}
                                onClick={() => {
                                    const billing: SubscribeRequestBillingFrequencyEnumKey =
                                        isFree
                                            ? "MONTHLY"
                                            : yearly && plan.yearlyPrice
                                              ? "YEARLY"
                                              : "MONTHLY";
                                    onSelect(plan, billing);
                                }}
                            >
                                {isFree ? "Get started free" : "Select plan"}
                            </Button>

                            {(plan.features ?? []).length > 0 && (
                                <ul className="mt-4 space-y-2 border-t pt-4">
                                    {plan.features!.map((f) => (
                                        <li
                                            key={f}
                                            className="flex items-start gap-2 text-xs text-muted-foreground"
                                        >
                                            <Check className="mt-0.5 h-3.5 w-3.5 shrink-0 text-emerald-500" />
                                            {f}
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                    );
                })}
            </div>
        </div>
    );
}

// ── Step 3: Checkout (always last) ────────────────────────────────────────────

// Stripe instance — initialised once (null if env var not set)
const stripePromise = process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY
    ? loadStripe(process.env.NEXT_PUBLIC_STRIPE_PUBLISHABLE_KEY)
    : null;

interface CheckoutStepProps {
    plan: SubscriptionPlan;
    billing: SubscribeRequestBillingFrequencyEnumKey;
}

// ── Inner form that lives inside <Elements> (has access to useStripe/useElements)

interface StripeCardFormProps {
    plan: SubscriptionPlan;
    billing: SubscribeRequestBillingFrequencyEnumKey;
    clientSecret: string;
    paymentIntentId: string;
}

function StripeCardForm({
    plan,
    billing,
    clientSecret,
    paymentIntentId,
}: StripeCardFormProps) {
    const router = useRouter();
    const stripe = useStripe();
    const elements = useElements();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    // clientSecret is already set on the Elements provider, so we just use it here for ref
    void clientSecret;

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        if (!stripe || !elements) return;

        setIsSubmitting(true);
        setSubmitError(null);

        try {
            // 1. Confirm the card payment with Stripe
            const { error: stripeError } = await stripe.confirmPayment({
                elements,
                redirect: "if_required",
            });

            if (stripeError) {
                setSubmitError(
                    stripeError.message ?? "Payment failed. Please try again."
                );
                return;
            }

            // 2. Finalize subscription on the backend
            await axiosInstance.post("/api/v1/subscriptions", {
                planId: plan.id,
                paymentMode: "STRIPE",
                billingFrequency: billing,
                stripePaymentIntentId: paymentIntentId,
            });

            toast.success("Subscription activated!");
            router.push("/dashboard");
            router.refresh();
        } catch {
            setSubmitError(
                "Subscription failed. Please try again or contact support."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <form onSubmit={handleSubmit} className="grid gap-4">
            <div className="rounded-lg border p-3">
                <PaymentElement options={{ layout: "tabs" }} />
            </div>

            {submitError && (
                <p className="text-sm text-destructive">{submitError}</p>
            )}

            <Button
                type="submit"
                disabled={!stripe || isSubmitting}
                className="w-full"
            >
                {isSubmitting ? (
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                ) : (
                    <Lock className="mr-2 h-4 w-4" />
                )}
                Pay and activate subscription
            </Button>

            <p className="flex items-center justify-center gap-1 text-xs text-muted-foreground">
                <Lock className="h-3 w-3" />
                Secured by Stripe
            </p>
        </form>
    );
}

// ── Non-Stripe fallback (PAYPAL, etc.)

interface GenericCheckoutFormProps {
    plan: SubscriptionPlan;
    billing: SubscribeRequestBillingFrequencyEnumKey;
    effectiveMode: string;
}

function GenericCheckoutForm({
    plan,
    billing,
    effectiveMode,
}: GenericCheckoutFormProps) {
    const router = useRouter();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [submitError, setSubmitError] = useState<string | null>(null);

    async function handleSubmit() {
        setIsSubmitting(true);
        setSubmitError(null);
        try {
            await axiosInstance.post("/api/v1/subscriptions", {
                planId: plan.id,
                paymentMode: effectiveMode,
                billingFrequency: billing,
            });
            toast.success("Subscription activated!");
            router.push("/dashboard");
            router.refresh();
        } catch {
            setSubmitError(
                "Subscription failed. Please try again or contact support."
            );
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="grid gap-4">
            {submitError && (
                <p className="text-sm text-destructive">{submitError}</p>
            )}
            <Button
                onClick={handleSubmit}
                disabled={isSubmitting}
                className="w-full"
            >
                {isSubmitting && (
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                )}
                Complete subscription
            </Button>
        </div>
    );
}

// ── Main CheckoutStep

function CheckoutStep({ plan, billing }: CheckoutStepProps) {
    const [selectedMode, setSelectedMode] = useState<string | null>(null);
    const [stripeClientSecret, setStripeClientSecret] = useState<string | null>(
        null
    );
    const [stripePaymentIntentId, setStripePaymentIntentId] = useState<
        string | null
    >(null);
    const [isCreatingIntent, setIsCreatingIntent] = useState(false);
    const [intentError, setIntentError] = useState<string | null>(null);

    const { data, isPending, isError } = useGetAppConfigurations(
        {
            filter: {
                category: { equals: "PAYMENT_MODE" },
                active: { equals: true },
            },
            pageable: { page: 0, size: 20 },
        },
        {}
    );

    const modes: AppConfiguration[] = data?.items ?? [];
    const effectiveMode =
        selectedMode ?? (modes.length === 1 ? (modes[0]?.code ?? null) : null);

    const isStripe = effectiveMode === "STRIPE";

    // When Stripe is selected and we don't have a clientSecret yet, create a PaymentIntent
    useEffect(() => {
        if (!isStripe || stripeClientSecret || isCreatingIntent) return;

        setIsCreatingIntent(true);
        setIntentError(null);

        axiosInstance
            .post<{ clientSecret: string; paymentIntentId: string }>(
                "/api/v1/subscriptions/stripe/payment-intent",
                { planId: plan.id, billingFrequency: billing }
            )
            .then(({ data: intentData }) => {
                setStripeClientSecret(intentData.clientSecret);
                setStripePaymentIntentId(intentData.paymentIntentId);
            })
            .catch(() => {
                setIntentError(
                    "Failed to initiate Stripe payment. Please try again."
                );
            })
            .finally(() => {
                setIsCreatingIntent(false);
            });
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [isStripe]);

    // Reset Stripe state when switching away from Stripe
    function handleModeChange(code: string) {
        setSelectedMode(code);
        if (code !== "STRIPE") {
            setStripeClientSecret(null);
            setStripePaymentIntentId(null);
        }
    }

    const symbol = currencySymbol(plan.currencyCode);
    const isFree = (plan.monthlyPrice ?? 0) === 0;
    const displayPrice =
        billing === "YEARLY" ? plan.yearlyPrice : plan.monthlyPrice;

    return (
        <div className="grid gap-5">
            <div>
                <h2 className="text-lg font-semibold tracking-tight">
                    Complete your subscription
                </h2>
                <p className="mt-1 text-sm text-muted-foreground">
                    Review your order and enter payment details.
                </p>
            </div>

            {/* Order summary */}
            <div className="rounded-lg border bg-muted/40 p-4">
                <p className="mb-2 text-xs font-medium tracking-wide text-muted-foreground uppercase">
                    Order Summary
                </p>
                <div className="flex items-center justify-between text-sm">
                    <span className="font-medium">{plan.title}</span>
                    <span>
                        {isFree ? "Free" : `${symbol}${displayPrice}/mo`}
                    </span>
                </div>
                <p className="mt-1 text-xs text-muted-foreground">
                    Billed{" "}
                    {billing === "YEARLY"
                        ? "yearly"
                        : billing === "LIFETIME"
                          ? "once"
                          : "monthly"}
                </p>
            </div>

            {/* Payment method selector (only shown when multiple modes) */}
            {!isPending && !isError && modes.length > 1 && (
                <div className="grid gap-2">
                    <p className="text-sm font-medium">Payment method</p>
                    {modes.map((mode) => (
                        <label
                            key={mode.code}
                            className={cn(
                                "flex cursor-pointer items-center gap-3 rounded-lg border p-3 text-sm transition-colors",
                                effectiveMode === mode.code
                                    ? "border-primary bg-primary/5"
                                    : "border-border hover:border-primary/40"
                            )}
                        >
                            <input
                                type="radio"
                                name="paymentMode"
                                value={mode.code}
                                checked={effectiveMode === mode.code}
                                onChange={() =>
                                    handleModeChange(mode.code ?? "")
                                }
                                className="accent-primary"
                            />
                            {mode.code === "STRIPE" && (
                                <CreditCard className="h-4 w-4 text-muted-foreground" />
                            )}
                            {mode.label ?? mode.code}
                        </label>
                    ))}
                </div>
            )}

            {/* Payment modes loading / error */}
            {isPending && (
                <div className="flex items-center justify-center py-4">
                    <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
                </div>
            )}
            {(isError || (!isPending && modes.length === 0)) && (
                <div className="flex items-center gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
                    <AlertCircle className="h-4 w-4 shrink-0" />
                    Payments not configured. Contact support.
                </div>
            )}

            {/* Stripe card form */}
            {!isPending && !isError && effectiveMode === "STRIPE" && (
                <div>
                    {isCreatingIntent && (
                        <div className="flex items-center justify-center py-6">
                            <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
                        </div>
                    )}
                    {intentError && (
                        <p className="text-sm text-destructive">
                            {intentError}
                        </p>
                    )}
                    {!isCreatingIntent &&
                        stripeClientSecret &&
                        stripePaymentIntentId &&
                        stripePromise && (
                            <Elements
                                stripe={stripePromise}
                                options={{
                                    clientSecret: stripeClientSecret,
                                    appearance: { theme: "stripe" },
                                }}
                            >
                                <StripeCardForm
                                    plan={plan}
                                    billing={billing}
                                    clientSecret={stripeClientSecret}
                                    paymentIntentId={stripePaymentIntentId}
                                />
                            </Elements>
                        )}
                    {!isCreatingIntent &&
                        !stripeClientSecret &&
                        !intentError && (
                            <div className="flex items-center justify-center py-6">
                                <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
                            </div>
                        )}
                </div>
            )}

            {/* Non-Stripe fallback */}
            {!isPending &&
                !isError &&
                effectiveMode &&
                effectiveMode !== "STRIPE" && (
                    <GenericCheckoutForm
                        plan={plan}
                        billing={billing}
                        effectiveMode={effectiveMode}
                    />
                )}
        </div>
    );
}

// ── Root component ────────────────────────────────────────────────────────────

export function SignUpForm({ initialPlanId, initialBilling }: SignUpFormProps) {
    // Both flows start at "account".
    // Flow 1 (/sign-up?planId=X): account → checkout
    // Flow 2 (/sign-up):          account → plan → checkout
    const [step, setStep] = useState<Step>("account");
    const [selectedPlan, setSelectedPlan] = useState<SubscriptionPlan | null>(
        null
    );
    const [billing, setBilling] =
        useState<SubscribeRequestBillingFrequencyEnumKey>(
            initialBilling ?? "MONTHLY"
        );

    // In flow 1, the plan is known from the URL. Fetch all plans and pick it.
    const { data: plansData, isPending: isLoadingPlan } =
        useGetSubscriptionPlans(
            { size: 50 },
            { query: { enabled: !!initialPlanId } }
        );

    useEffect(() => {
        if (initialPlanId && plansData?.items && !selectedPlan) {
            const found = plansData.items.find((p) => p.id === initialPlanId);
            if (found) setSelectedPlan(found);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [initialPlanId, plansData]);

    function handleAccountSuccess() {
        if (initialPlanId) {
            // Flow 1: plan already known → go straight to checkout.
            setStep("checkout");
        } else {
            // Flow 2: no plan yet → let user pick one.
            setStep("plan");
        }
    }

    if (step === "account") {
        // Flow 1: block the account form until the pre-selected plan is resolved.
        // This guarantees selectedPlan is set before handleAccountSuccess fires.
        if (initialPlanId && isLoadingPlan) {
            return (
                <NarrowCard>
                    <div className="flex items-center justify-center py-16">
                        <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                    </div>
                </NarrowCard>
            );
        }

        return (
            <NarrowCard>
                <AccountStep
                    plan={selectedPlan}
                    isPlanLoading={false}
                    billing={billing}
                    onSuccess={handleAccountSuccess}
                />
            </NarrowCard>
        );
    }

    if (step === "plan") {
        return (
            <PlanSelectionStep
                onSelect={(plan, b) => {
                    setSelectedPlan(plan);
                    setBilling(b);
                    setStep("checkout");
                }}
            />
        );
    }

    // step === "checkout"
    // selectedPlan should always be set here, but show a spinner as safety net.
    if (!selectedPlan) {
        return (
            <NarrowCard>
                <div className="flex items-center justify-center py-16">
                    <Loader2 className="h-6 w-6 animate-spin text-muted-foreground" />
                </div>
            </NarrowCard>
        );
    }

    return (
        <NarrowCard>
            <CheckoutStep plan={selectedPlan} billing={billing} />
        </NarrowCard>
    );
}
