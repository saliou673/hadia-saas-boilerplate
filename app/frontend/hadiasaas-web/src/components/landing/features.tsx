import {
    Shield,
    Users,
    Building2,
    CreditCard,
    Lock,
    Code2,
    Zap,
} from "lucide-react";

const FEATURES = [
    {
        icon: Shield,
        title: "Authentication",
        description:
            "Sign in, sign up, OTP verification, password reset — all production-ready with NextAuth and Spring Security.",
        iconColor: "text-violet-600 dark:text-violet-400",
        iconBg: "bg-violet-100 dark:bg-violet-500/10",
        glowFrom: "from-violet-500/[0.06] dark:from-violet-500/[0.08]",
        border: "hover:border-violet-300 dark:hover:border-violet-500/30",
    },
    {
        icon: Users,
        title: "Role & Permissions",
        description:
            "Fine-grained RBAC with resource-level control. Manage exactly what each user can see and do in your app.",
        iconColor: "text-blue-600 dark:text-blue-400",
        iconBg: "bg-blue-100 dark:bg-blue-500/10",
        glowFrom: "from-blue-500/[0.06] dark:from-blue-500/[0.08]",
        border: "hover:border-blue-300 dark:hover:border-blue-500/30",
    },
    {
        icon: Building2,
        title: "Multi-tenant",
        description:
            "Enterprise profiles, workspaces, and team management baked into the hexagonal architecture from day one.",
        iconColor: "text-emerald-600 dark:text-emerald-400",
        iconBg: "bg-emerald-100 dark:bg-emerald-500/10",
        glowFrom: "from-emerald-500/[0.06] dark:from-emerald-500/[0.08]",
        border: "hover:border-emerald-300 dark:hover:border-emerald-500/30",
    },
    {
        icon: CreditCard,
        title: "Subscriptions",
        description:
            "Flexible billing plans, usage quotas, and payment management built-in and ready for your pricing model.",
        iconColor: "text-amber-600 dark:text-amber-400",
        iconBg: "bg-amber-100 dark:bg-amber-500/10",
        glowFrom: "from-amber-500/[0.06] dark:from-amber-500/[0.08]",
        border: "hover:border-amber-300 dark:hover:border-amber-500/30",
    },
    {
        icon: Lock,
        title: "Security Settings",
        description:
            "Two-factor authentication, session management, IP restrictions, and enterprise-grade security policies.",
        iconColor: "text-rose-600 dark:text-rose-400",
        iconBg: "bg-rose-100 dark:bg-rose-500/10",
        glowFrom: "from-rose-500/[0.06] dark:from-rose-500/[0.08]",
        border: "hover:border-rose-300 dark:hover:border-rose-500/30",
    },
    {
        icon: Code2,
        title: "Type-safe API Client",
        description:
            "Auto-generated TypeScript API client from OpenAPI spec via Kubb. Full type safety from backend to UI.",
        iconColor: "text-indigo-600 dark:text-indigo-400",
        iconBg: "bg-indigo-100 dark:bg-indigo-500/10",
        glowFrom: "from-indigo-500/[0.06] dark:from-indigo-500/[0.08]",
        border: "hover:border-indigo-300 dark:hover:border-indigo-500/30",
    },
];

export function Features() {
    return (
        <section
            id="features"
            className="relative bg-white py-24 sm:py-32 dark:bg-black"
        >
            <div className="absolute inset-0 [background-image:radial-gradient(circle,var(--lp-dot)_1px,transparent_1px)] [background-size:28px_28px]" />

            <div className="relative z-10 mx-auto max-w-6xl px-4 sm:px-6">
                {/* Section header */}
                <div className="mx-auto max-w-2xl text-center">
                    <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-violet-200 bg-violet-50 px-3 py-1.5 text-sm text-violet-700 dark:border-violet-500/20 dark:bg-violet-500/[0.08] dark:text-violet-300">
                        <Zap className="h-3.5 w-3.5" />
                        Batteries included
                    </div>
                    <h2 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-5xl dark:text-white">
                        Everything you need to{" "}
                        <span className="bg-gradient-to-r from-violet-600 to-indigo-600 bg-clip-text text-transparent dark:from-violet-400 dark:to-indigo-400">
                            ship fast
                        </span>
                    </h2>
                    <p className="mt-4 text-lg text-gray-600 dark:text-zinc-400">
                        Stop reinventing the wheel. Start with a solid
                        foundation that handles the hard parts so you can focus
                        on your unique value.
                    </p>
                </div>

                {/* Feature grid */}
                <div className="mt-16 grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                    {FEATURES.map((feature) => {
                        const Icon = feature.icon;
                        return (
                            <div
                                key={feature.title}
                                className={`group relative overflow-hidden rounded-2xl border border-gray-200 bg-white p-6 shadow-sm transition-all duration-300 hover:-translate-y-1 hover:shadow-md dark:border-white/[0.07] dark:bg-zinc-900/60 dark:shadow-none dark:hover:shadow-black/50 ${feature.border}`}
                            >
                                <div
                                    className={`absolute inset-0 rounded-2xl bg-gradient-to-br opacity-0 transition-opacity duration-500 group-hover:opacity-100 ${feature.glowFrom} to-transparent`}
                                />
                                <div className="relative z-10">
                                    <div
                                        className={`mb-4 inline-flex h-10 w-10 items-center justify-center rounded-xl ${feature.iconBg}`}
                                    >
                                        <Icon
                                            className={`h-5 w-5 ${feature.iconColor}`}
                                        />
                                    </div>
                                    <h3 className="mb-2 text-base font-semibold text-gray-900 dark:text-white">
                                        {feature.title}
                                    </h3>
                                    <p className="text-sm leading-relaxed text-gray-600 dark:text-zinc-400">
                                        {feature.description}
                                    </p>
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </section>
    );
}
