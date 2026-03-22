"use client";

import { useState } from "react";
import { useGetSubscriptionPlans } from "hadiasaas-apiclient";
import type { SubscriptionPlan } from "hadiasaas-apiclient";
import { Check, Zap, Sparkles, AlertCircle } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";

// ── helpers ──────────────────────────────────────────────────────────────────

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

// ── sub-components ────────────────────────────────────────────────────────────

function PlanCardSkeleton() {
    return (
        <div className="rounded-2xl border border-white/[0.08] bg-zinc-900/50 p-7">
            <Skeleton className="mb-5 h-6 w-24 bg-white/[0.06]" />
            <Skeleton className="mb-2 h-5 w-32 bg-white/[0.06]" />
            <Skeleton className="mb-6 h-4 w-full bg-white/[0.04]" />
            <Skeleton className="mb-7 h-12 w-28 bg-white/[0.06]" />
            <Skeleton className="mb-7 h-9 w-full rounded-lg bg-white/[0.06]" />
            <div className="space-y-3 border-t border-white/[0.06] pt-6">
                {Array.from({ length: 4 }).map((_, i) => (
                    <Skeleton key={i} className="h-4 w-3/4 bg-white/[0.04]" />
                ))}
            </div>
        </div>
    );
}

interface PlanCardProps {
    plan: SubscriptionPlan;
    highlighted: boolean;
    yearly: boolean;
}

function PlanCard({ plan, highlighted, yearly }: PlanCardProps) {
    const symbol = currencySymbol(plan.currencyCode);
    const displayPrice = yearly ? plan.yearlyPrice : plan.monthlyPrice;
    const isFree = (plan.monthlyPrice ?? 0) === 0;
    const discount = yearlyDiscount(plan);

    const ctaLabel = isFree ? "Get started free" : "Start free trial";

    return (
        <div
            className={`relative overflow-hidden rounded-2xl border p-7 transition-all duration-300 hover:-translate-y-1 ${
                highlighted
                    ? "border-violet-300 bg-gradient-to-b from-violet-50 to-white shadow-2xl shadow-violet-200 dark:border-violet-500/40 dark:from-violet-950/80 dark:to-zinc-900/80 dark:shadow-violet-500/20"
                    : "border-gray-200 bg-white shadow-sm hover:border-gray-300 hover:shadow-md dark:border-white/[0.08] dark:bg-zinc-900/50 dark:hover:border-white/[0.15] dark:hover:shadow-black/40"
            }`}
        >
            {/* Top shimmer line for highlighted */}
            {highlighted && (
                <div className="pointer-events-none absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-violet-500/60 to-transparent" />
            )}

            {/* "Most popular" badge for highlighted */}
            {highlighted && (
                <div className="mb-5">
                    <span className="inline-flex items-center gap-1.5 rounded-full border border-violet-200 bg-violet-100 px-3 py-1 text-xs font-semibold text-violet-700 dark:border-violet-500/30 dark:bg-violet-600/25 dark:text-violet-300">
                        <Zap className="h-3 w-3" />
                        Most popular
                    </span>
                </div>
            )}

            {/* Plan name */}
            <h3 className="mb-1 text-lg font-bold text-gray-900 dark:text-white">
                {plan.title}
            </h3>

            {/* Description */}
            <p className="mb-6 text-sm text-gray-500 dark:text-zinc-400">
                {plan.description}
            </p>

            {/* Price */}
            <div className="mb-7">
                {displayPrice !== undefined && displayPrice !== null ? (
                    <>
                        <div className="flex items-end gap-1">
                            <span className="text-5xl font-extrabold tracking-tight text-gray-900 dark:text-white">
                                {isFree ? "Free" : `${symbol}${displayPrice}`}
                            </span>
                            {!isFree && (
                                <span className="mb-2 text-sm text-gray-400 dark:text-zinc-500">
                                    /mo
                                </span>
                            )}
                        </div>
                        {yearly && !isFree && discount !== null && (
                            <p className="mt-1.5 text-xs text-gray-400 dark:text-zinc-500">
                                Save{" "}
                                <span className="font-medium text-emerald-600 dark:text-emerald-400">
                                    {discount}%
                                </span>{" "}
                                vs monthly · {symbol}
                                {((plan.yearlyPrice ?? 0) * 12).toFixed(0)}/yr
                            </p>
                        )}
                    </>
                ) : (
                    <div className="text-4xl font-extrabold tracking-tight text-gray-900 dark:text-white">
                        Custom
                    </div>
                )}
            </div>

            {/* CTA */}
            <Button
                asChild
                className={`mb-7 w-full transition-all ${
                    highlighted
                        ? "bg-violet-600 text-white shadow-lg shadow-violet-500/25 hover:bg-violet-500 hover:shadow-violet-500/40"
                        : "border border-gray-200 bg-gray-50 text-gray-700 hover:bg-gray-100 dark:border-white/10 dark:bg-white/[0.05] dark:text-white dark:hover:bg-white/[0.09]"
                }`}
                variant={highlighted ? "default" : "ghost"}
            >
                <Link
                    href={`/sign-up?planId=${plan.id}&billing=${yearly && plan.yearlyPrice ? "YEARLY" : "MONTHLY"}`}
                >
                    {ctaLabel}
                </Link>
            </Button>

            {/* Feature list */}
            {(plan.features ?? []).length > 0 && (
                <div className="border-t border-gray-100 pt-6 dark:border-white/[0.06]">
                    <ul className="space-y-3">
                        {plan.features!.map((f) => (
                            <li
                                key={f}
                                className="flex items-start gap-2.5 text-sm text-gray-600 dark:text-zinc-300"
                            >
                                <Check className="mt-0.5 h-4 w-4 shrink-0 text-emerald-500 dark:text-emerald-400" />
                                {f}
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </div>
    );
}

// ── main component ────────────────────────────────────────────────────────────

export function Pricing() {
    const [yearly, setYearly] = useState(false);

    const { data, isPending, isError } = useGetSubscriptionPlans({ size: 50 });

    // Only active plans, sorted cheapest → most expensive
    const plans = (data?.items ?? [])
        .filter((p) => p.active)
        .sort((a, b) => (a.monthlyPrice ?? 0) - (b.monthlyPrice ?? 0));

    // Highlight the middle plan
    const highlightedIndex = Math.floor(plans.length / 2);

    // Show yearly toggle only if at least one plan has a distinct yearly price
    const hasYearlyPricing = plans.some(
        (p) =>
            p.yearlyPrice !== undefined &&
            p.yearlyPrice !== null &&
            p.yearlyPrice !== p.monthlyPrice
    );

    return (
        <section
            id="pricing"
            className="relative bg-white py-24 sm:py-32 dark:bg-black"
        >
            <div className="absolute inset-0 [background-image:radial-gradient(circle,var(--lp-dot)_1px,transparent_1px)] [background-size:28px_28px]" />

            <div className="relative z-10 mx-auto max-w-6xl px-4 sm:px-6">
                {/* Header */}
                <div className="mx-auto max-w-2xl text-center">
                    <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-amber-200 bg-amber-50 px-3 py-1.5 text-sm text-amber-700 dark:border-amber-500/20 dark:bg-amber-500/[0.08] dark:text-amber-300">
                        <Sparkles className="h-3.5 w-3.5" />
                        Simple, transparent pricing
                    </div>
                    <h2 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-5xl dark:text-white">
                        Start free,{" "}
                        <span className="bg-gradient-to-r from-amber-500 to-orange-500 bg-clip-text text-transparent dark:from-amber-400 dark:to-orange-400">
                            scale as you grow
                        </span>
                    </h2>
                    <p className="mt-4 text-lg text-gray-600 dark:text-zinc-400">
                        No hidden fees, no lock-in. Cancel anytime.
                    </p>
                </div>

                {/* Billing toggle — only when yearly pricing exists */}
                {hasYearlyPricing && (
                    <div className="mt-8 flex items-center justify-center gap-3">
                        <span
                            className={`text-sm font-medium transition-colors ${!yearly ? "text-white" : "text-zinc-500"}`}
                        >
                            Monthly
                        </span>
                        <button
                            onClick={() => setYearly((v) => !v)}
                            className={`relative h-6 w-11 rounded-full transition-colors duration-300 focus:outline-none focus-visible:ring-2 focus-visible:ring-violet-500 ${
                                yearly ? "bg-violet-600" : "bg-zinc-700"
                            }`}
                            role="switch"
                            aria-checked={yearly}
                        >
                            <span
                                className={`absolute top-0.5 left-0.5 h-5 w-5 rounded-full bg-white shadow transition-transform duration-300 ${
                                    yearly ? "translate-x-5" : "translate-x-0"
                                }`}
                            />
                        </button>
                        <span
                            className={`text-sm font-medium transition-colors ${yearly ? "text-white" : "text-zinc-500"}`}
                        >
                            Yearly
                            <span className="ml-1.5 rounded-full bg-emerald-500/20 px-1.5 py-0.5 text-[11px] font-semibold text-emerald-400">
                                Save more
                            </span>
                        </span>
                    </div>
                )}

                {/* Cards */}
                <div
                    className={`mt-12 grid gap-5 ${
                        plans.length === 1
                            ? "mx-auto max-w-sm"
                            : plans.length === 2
                              ? "mx-auto max-w-2xl md:grid-cols-2"
                              : "md:grid-cols-3"
                    }`}
                >
                    {isPending &&
                        Array.from({ length: 3 }).map((_, i) => (
                            <PlanCardSkeleton key={i} />
                        ))}

                    {isError && (
                        <div className="col-span-3 flex flex-col items-center justify-center gap-3 rounded-2xl border border-white/[0.08] bg-zinc-900/50 py-16 text-center">
                            <AlertCircle className="h-8 w-8 text-zinc-600" />
                            <p className="text-sm text-zinc-500">
                                Could not load plans. Please try again later.
                            </p>
                        </div>
                    )}

                    {!isPending &&
                        !isError &&
                        plans.map((plan, i) => (
                            <PlanCard
                                key={plan.id}
                                plan={plan}
                                highlighted={i === highlightedIndex}
                                yearly={yearly}
                            />
                        ))}
                </div>
            </div>
        </section>
    );
}
