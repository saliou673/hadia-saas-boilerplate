import { ArrowRight, Sparkles } from "lucide-react";
import Link from "next/link";
import { Button } from "@/components/ui/button";

export function CTA() {
    return (
        <section className="relative overflow-hidden bg-slate-50 py-24 sm:py-32 dark:bg-zinc-950">
            <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-gray-200 to-transparent dark:via-white/[0.08]" />

            {/* Ambient glow */}
            <div className="pointer-events-none absolute inset-0 flex items-center justify-center">
                <div className="h-80 w-[500px] rounded-full bg-violet-300/20 blur-[120px] dark:bg-violet-600/[0.12]" />
            </div>
            <div className="pointer-events-none absolute top-1/2 left-1/4 h-48 w-48 -translate-y-1/2 rounded-full bg-indigo-300/20 blur-[80px] dark:bg-indigo-600/[0.10]" />
            <div className="pointer-events-none absolute top-1/2 right-1/4 h-48 w-48 -translate-y-1/2 rounded-full bg-fuchsia-300/20 blur-[80px] dark:bg-fuchsia-600/[0.10]" />

            <div className="relative z-10 mx-auto max-w-3xl px-4 text-center sm:px-6">
                <div className="mx-auto mb-6 flex h-14 w-14 items-center justify-center rounded-2xl bg-gradient-to-br from-violet-500 to-indigo-600 shadow-2xl ring-1 shadow-violet-500/40 ring-white/10">
                    <Sparkles className="h-6 w-6 text-white" />
                </div>

                <h2 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-5xl dark:text-white">
                    Ready to build your{" "}
                    <span className="bg-gradient-to-r from-violet-600 via-fuchsia-500 to-indigo-600 bg-clip-text text-transparent dark:from-violet-400 dark:via-fuchsia-400 dark:to-indigo-400">
                        next big thing?
                    </span>
                </h2>

                <p className="mx-auto mt-5 max-w-xl text-lg text-gray-600 dark:text-zinc-400">
                    Join developers who ship their SaaS products faster with
                    HadiaSaaS. Start for free, no credit card required.
                </p>

                <div className="mt-10 flex flex-col items-center justify-center gap-4 sm:flex-row">
                    <Button
                        size="lg"
                        asChild
                        className="group bg-violet-600 px-8 text-white shadow-xl shadow-violet-500/30 transition-all hover:scale-105 hover:bg-violet-500 hover:shadow-violet-500/50"
                    >
                        <Link href="/sign-up">
                            Get started for free
                            <ArrowRight className="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1" />
                        </Link>
                    </Button>
                    <Button
                        size="lg"
                        variant="outline"
                        asChild
                        className="border-gray-300 bg-white px-8 text-gray-700 transition-all hover:scale-105 hover:border-gray-400 hover:bg-gray-50 dark:border-white/15 dark:bg-white/[0.04] dark:text-white dark:hover:border-white/25 dark:hover:bg-white/[0.08]"
                    >
                        <Link href="/sign-in">Sign in to your account →</Link>
                    </Button>
                </div>

                <p className="mt-7 text-sm text-gray-400 dark:text-zinc-600">
                    Free forever plan available · No credit card required
                </p>
            </div>
        </section>
    );
}
