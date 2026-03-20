import { ArrowRight, Github, Star } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";

export function Hero() {
    return (
        <section className="relative min-h-screen overflow-hidden bg-white dark:bg-black">
            {/* Dot grid — color switches via CSS variable */}
            <div className="absolute inset-0 [background-image:radial-gradient(circle,var(--lp-dot)_1px,transparent_1px)] [background-size:28px_28px]" />

            {/* Gradient orbs */}
            <div className="pointer-events-none absolute top-[30%] left-1/2 h-[600px] w-[600px] -translate-x-1/2 -translate-y-1/2 animate-orb rounded-full bg-violet-400/15 blur-[130px] dark:bg-violet-600/20" />
            <div className="pointer-events-none absolute top-[60%] left-[20%] h-72 w-72 animate-orb-slow rounded-full bg-indigo-400/15 blur-[100px] dark:bg-indigo-600/15" />
            <div className="pointer-events-none absolute top-[25%] right-[15%] h-56 w-56 animate-orb-delay rounded-full bg-fuchsia-400/15 blur-[90px] dark:bg-fuchsia-600/15" />

            {/* Vignette fade */}
            <div className="pointer-events-none absolute inset-0 [background:radial-gradient(ellipse_80%_60%_at_50%_-10%,transparent_60%,rgba(255,255,255,0.85)_100%)] dark:[background:radial-gradient(ellipse_80%_60%_at_50%_-10%,transparent_60%,rgba(0,0,0,0.85)_100%)]" />
            <div className="pointer-events-none absolute right-0 bottom-0 left-0 h-40 bg-gradient-to-t from-white to-transparent dark:from-black" />

            <div className="relative z-10 mx-auto flex min-h-screen max-w-6xl flex-col items-center justify-center px-4 pt-32 pb-20 sm:px-6">
                {/* Badge */}
                <div className="mb-8 inline-flex items-center gap-2 rounded-full border border-gray-300 bg-gray-100 px-4 py-1.5 text-sm text-gray-700 backdrop-blur-sm dark:border-white/10 dark:bg-white/[0.04] dark:text-zinc-300">
                    <span className="flex h-2 w-2 animate-pulse rounded-full bg-emerald-500" />
                    v2.2 — Now with roles &amp; permissions
                    <ArrowRight className="h-3 w-3 opacity-60" />
                </div>

                {/* Headline */}
                <h1 className="mx-auto max-w-4xl text-center text-5xl font-bold tracking-tight text-gray-900 sm:text-6xl lg:text-7xl dark:text-white">
                    Ship your SaaS product
                    <span className="mt-2 block bg-gradient-to-r from-violet-600 via-fuchsia-500 to-indigo-600 bg-clip-text text-transparent dark:from-violet-400 dark:via-fuchsia-400 dark:to-indigo-400">
                        faster than ever
                    </span>
                </h1>

                {/* Subtext */}
                <p className="mx-auto mt-7 max-w-2xl text-center text-lg leading-relaxed text-gray-600 dark:text-zinc-400">
                    A production-ready{" "}
                    <strong className="font-medium text-gray-800 dark:text-zinc-200">
                        Next.js + Spring Boot
                    </strong>{" "}
                    boilerplate with authentication, roles, billing,
                    multi-tenancy, and more — so you can focus on what makes
                    your product unique.
                </p>

                {/* CTA buttons */}
                <div className="mt-10 flex flex-col items-center justify-center gap-4 sm:flex-row">
                    <Button
                        size="lg"
                        asChild
                        className="group bg-violet-600 px-8 text-white shadow-2xl shadow-violet-500/30 transition-all hover:scale-105 hover:bg-violet-500 hover:shadow-violet-500/50"
                    >
                        <Link href="/sign-up">
                            Start building for free
                            <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                        </Link>
                    </Button>
                    <Button
                        size="lg"
                        variant="outline"
                        asChild
                        className="border-gray-300 bg-white px-8 text-gray-700 transition-all hover:scale-105 hover:border-gray-400 hover:bg-gray-50 dark:border-white/15 dark:bg-white/[0.04] dark:text-white dark:hover:border-white/25 dark:hover:bg-white/[0.08]"
                    >
                        <Link
                            href="https://github.com"
                            target="_blank"
                            rel="noreferrer"
                        >
                            <Github className="mr-2 h-4 w-4" />
                            View on GitHub
                        </Link>
                    </Button>
                </div>

                {/* Social proof */}
                <p className="mt-6 text-sm text-gray-500 dark:text-zinc-600">
                    <Star className="mr-1 inline h-3.5 w-3.5 fill-amber-400 text-amber-400" />
                    Trusted by 1,000+ developers worldwide
                </p>

                {/* Dashboard mockup */}
                <div className="relative mx-auto mt-16 w-full max-w-5xl">
                    {/* Outer glow ring */}
                    <div className="absolute -inset-1 rounded-2xl bg-gradient-to-r from-violet-500/30 via-fuchsia-500/20 to-indigo-500/30 blur-xl" />

                    {/* Card */}
                    <div className="relative overflow-hidden rounded-xl border border-gray-200 bg-gray-50 shadow-2xl shadow-gray-300/60 dark:border-white/[0.09] dark:bg-zinc-900/90 dark:shadow-black/70">
                        {/* Browser chrome */}
                        <div className="flex items-center gap-2 border-b border-gray-200 bg-gray-100/80 px-4 py-3 dark:border-white/[0.07] dark:bg-zinc-950/80">
                            <div className="flex gap-1.5">
                                <div className="h-3 w-3 rounded-full bg-red-400/80" />
                                <div className="h-3 w-3 rounded-full bg-amber-400/80" />
                                <div className="h-3 w-3 rounded-full bg-emerald-400/80" />
                            </div>
                            <div className="ml-2 flex-1 rounded-md bg-gray-200 px-3 py-1 text-center text-xs text-gray-500 dark:bg-white/[0.04] dark:text-zinc-500">
                                app.yoursaas.com/dashboard
                            </div>
                            <div className="h-5 w-16 rounded bg-gray-200 dark:bg-white/[0.03]" />
                        </div>

                        {/* Dashboard body */}
                        <div className="flex h-72 sm:h-[26rem]">
                            {/* Sidebar */}
                            <div className="hidden w-52 shrink-0 border-r border-gray-200 bg-white p-4 sm:block dark:border-white/[0.06] dark:bg-zinc-950/60">
                                <div className="mb-5 flex items-center gap-2">
                                    <div className="h-7 w-7 rounded-lg bg-violet-200 dark:bg-violet-600/40" />
                                    <div className="h-3.5 w-20 rounded bg-gray-200 dark:bg-white/10" />
                                </div>
                                {[
                                    { active: true, w: "w-16" },
                                    { active: false, w: "w-20" },
                                    { active: false, w: "w-14" },
                                    { active: false, w: "w-18" },
                                    { active: false, w: "w-16" },
                                    { active: false, w: "w-12" },
                                ].map((item, i) => (
                                    <div
                                        key={i}
                                        className={`mb-1.5 flex items-center gap-2.5 rounded-md px-2.5 py-2 ${item.active ? "bg-violet-100 dark:bg-violet-600/25" : ""}`}
                                    >
                                        <div
                                            className={`h-4 w-4 rounded-md ${item.active ? "bg-violet-300 dark:bg-violet-400/40" : "bg-gray-200 dark:bg-white/10"}`}
                                        />
                                        <div
                                            className={`h-3 rounded ${item.w} ${item.active ? "bg-violet-200 dark:bg-violet-300/30" : "bg-gray-200 dark:bg-white/10"}`}
                                        />
                                    </div>
                                ))}
                                <div className="my-3 border-t border-gray-100 dark:border-white/[0.05]" />
                                {[{ w: "w-14" }, { w: "w-16" }].map(
                                    (item, i) => (
                                        <div
                                            key={i}
                                            className="mb-1.5 flex items-center gap-2.5 rounded-md px-2.5 py-2"
                                        >
                                            <div className="h-4 w-4 rounded-md bg-gray-100 dark:bg-white/[0.06]" />
                                            <div
                                                className={`h-3 rounded bg-gray-100 dark:bg-white/[0.06] ${item.w}`}
                                            />
                                        </div>
                                    )
                                )}
                            </div>

                            {/* Main content */}
                            <div className="flex-1 overflow-hidden bg-white p-5 sm:p-6 dark:bg-transparent">
                                <div className="mb-6 flex items-center justify-between">
                                    <div>
                                        <div className="mb-1.5 h-5 w-28 rounded-md bg-gray-200 dark:bg-white/15" />
                                        <div className="h-3 w-44 rounded-md bg-gray-100 dark:bg-white/[0.06]" />
                                    </div>
                                    <div className="flex gap-2">
                                        <div className="h-8 w-8 rounded-lg bg-gray-100 dark:bg-white/[0.05]" />
                                        <div className="h-8 w-28 rounded-lg bg-violet-200 dark:bg-violet-600/35" />
                                    </div>
                                </div>

                                {/* Stats row */}
                                <div className="mb-5 grid grid-cols-3 gap-3">
                                    {[
                                        {
                                            bg: "bg-violet-50 dark:bg-violet-500/15",
                                            val: "bg-violet-200 dark:bg-violet-400/30",
                                        },
                                        {
                                            bg: "bg-emerald-50 dark:bg-emerald-500/10",
                                            val: "bg-emerald-200 dark:bg-emerald-400/30",
                                        },
                                        {
                                            bg: "bg-blue-50 dark:bg-blue-500/10",
                                            val: "bg-blue-200 dark:bg-blue-400/30",
                                        },
                                    ].map((card, i) => (
                                        <div
                                            key={i}
                                            className={`rounded-xl border border-gray-100 p-3.5 dark:border-white/[0.05] ${card.bg}`}
                                        >
                                            <div className="mb-2 h-2.5 w-16 rounded bg-gray-200 dark:bg-white/10" />
                                            <div
                                                className={`mb-1 h-6 w-14 rounded-md ${card.val}`}
                                            />
                                            <div className="h-2 w-20 rounded bg-gray-100 dark:bg-white/[0.06]" />
                                        </div>
                                    ))}
                                </div>

                                {/* Table */}
                                <div className="overflow-hidden rounded-xl border border-gray-100 bg-gray-50 dark:border-white/[0.06] dark:bg-white/[0.02]">
                                    <div className="flex items-center gap-3 border-b border-gray-100 bg-gray-100/60 px-4 py-2.5 dark:border-white/[0.05] dark:bg-white/[0.02]">
                                        {["w-24", "w-32", "w-20", "w-16"].map(
                                            (w, i) => (
                                                <div
                                                    key={i}
                                                    className={`h-2.5 rounded bg-gray-200 dark:bg-white/[0.07] ${w}`}
                                                />
                                            )
                                        )}
                                    </div>
                                    {[
                                        {
                                            pill: "bg-emerald-100 text-emerald-700 dark:bg-emerald-600/30 dark:text-emerald-300",
                                        },
                                        {
                                            pill: "bg-blue-100 text-blue-700 dark:bg-blue-600/30 dark:text-blue-300",
                                        },
                                        {
                                            pill: "bg-amber-100 text-amber-700 dark:bg-amber-600/30 dark:text-amber-300",
                                        },
                                        {
                                            pill: "bg-violet-100 text-violet-700 dark:bg-violet-600/30 dark:text-violet-300",
                                        },
                                    ].map((row, i) => (
                                        <div
                                            key={i}
                                            className="flex items-center gap-3 border-b border-gray-100 px-4 py-3 last:border-0 dark:border-white/[0.04]"
                                        >
                                            <div className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-gray-200 dark:bg-white/10">
                                                <div className="h-3 w-3 rounded-full bg-gray-300 dark:bg-white/20" />
                                            </div>
                                            <div className="h-3 w-28 rounded bg-gray-200 dark:bg-white/10" />
                                            <div className="ml-auto h-3 w-20 rounded bg-gray-100 dark:bg-white/[0.06]" />
                                            <div
                                                className={`h-5 w-16 rounded-full border border-transparent px-2 text-xs ${row.pill}`}
                                            />
                                        </div>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}
