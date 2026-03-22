import type { Metadata } from "next";
import { Logo } from "@/assets/logo";
import { SignUpForm } from "./sign-up-form";
import type { SubscribeRequestBillingFrequencyEnumKey } from "@api-client";

export const metadata: Metadata = {
    title: "Sign Up",
};

const VALID_BILLING = new Set(["MONTHLY", "YEARLY", "LIFETIME"]);

export default async function SignUpPage({
    searchParams,
}: {
    searchParams: Promise<Record<string, string | string[] | undefined>>;
}) {
    const params = await searchParams;

    const planIdRaw = params.planId;
    const billingRaw = params.billing;

    const planIdStr = Array.isArray(planIdRaw) ? planIdRaw[0] : planIdRaw;
    const billingStr = Array.isArray(billingRaw) ? billingRaw[0] : billingRaw;

    const initialPlanId =
        planIdStr && /^\d+$/.test(planIdStr)
            ? parseInt(planIdStr, 10)
            : undefined;

    const initialBilling =
        billingStr && VALID_BILLING.has(billingStr)
            ? (billingStr as SubscribeRequestBillingFrequencyEnumKey)
            : undefined;

    return (
        <div className="container grid min-h-svh max-w-none items-center justify-center">
            <div className="mx-auto flex w-full max-w-5xl flex-col items-center gap-8 px-4 py-10">
                {/* Logo */}
                <div className="flex items-center gap-2">
                    <Logo />
                    <span className="text-xl font-medium">Shadcn Admin</span>
                </div>

                {/* Multi-step sign-up form */}
                <SignUpForm
                    initialPlanId={initialPlanId}
                    initialBilling={initialBilling}
                />
            </div>
        </div>
    );
}
