import { Terminal, Settings2, Rocket } from "lucide-react";

const STEPS = [
    {
        number: "01",
        icon: Terminal,
        title: "Clone & install",
        description:
            "Clone the repository and run pnpm install. All dependencies are pre-configured and ready to go — frontend and backend.",
        snippet: "git clone github.com/org/hadia-saas-boilerplate",
        iconClass:
            "text-violet-600 bg-violet-100 dark:text-violet-400 dark:bg-violet-500/10",
        badgeClass:
            "bg-violet-100 text-violet-700 border-violet-200 dark:bg-violet-600/15 dark:text-violet-300 dark:border-violet-500/20",
        cardBorder: "hover:border-violet-300 dark:hover:border-violet-500/25",
        snippetBg:
            "bg-violet-50 border-violet-100 dark:bg-violet-950/40 dark:border-violet-500/10",
        snippetText: "text-violet-800 dark:text-zinc-300",
    },
    {
        number: "02",
        icon: Settings2,
        title: "Configure & run",
        description:
            "Set your environment variables, start the database with Docker, and run both the Next.js frontend and Spring Boot backend.",
        snippet: "cp .env.example .env && docker compose up -d",
        iconClass:
            "text-indigo-600 bg-indigo-100 dark:text-indigo-400 dark:bg-indigo-500/10",
        badgeClass:
            "bg-indigo-100 text-indigo-700 border-indigo-200 dark:bg-indigo-600/15 dark:text-indigo-300 dark:border-indigo-500/20",
        cardBorder: "hover:border-indigo-300 dark:hover:border-indigo-500/25",
        snippetBg:
            "bg-indigo-50 border-indigo-100 dark:bg-indigo-950/40 dark:border-indigo-500/10",
        snippetText: "text-indigo-800 dark:text-zinc-300",
    },
    {
        number: "03",
        icon: Rocket,
        title: "Customize & deploy",
        description:
            "Adjust the branding, add your domain logic, and deploy with Docker or your preferred cloud provider. Your SaaS is live.",
        snippet: "pnpm build && docker build -t my-saas .",
        iconClass:
            "text-fuchsia-600 bg-fuchsia-100 dark:text-fuchsia-400 dark:bg-fuchsia-500/10",
        badgeClass:
            "bg-fuchsia-100 text-fuchsia-700 border-fuchsia-200 dark:bg-fuchsia-600/15 dark:text-fuchsia-300 dark:border-fuchsia-500/20",
        cardBorder: "hover:border-fuchsia-300 dark:hover:border-fuchsia-500/25",
        snippetBg:
            "bg-fuchsia-50 border-fuchsia-100 dark:bg-fuchsia-950/40 dark:border-fuchsia-500/10",
        snippetText: "text-fuchsia-800 dark:text-zinc-300",
    },
];

export function Process() {
    return (
        <section
            id="process"
            className="relative bg-slate-50 py-24 sm:py-32 dark:bg-zinc-950"
        >
            <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-gray-200 to-transparent dark:via-white/[0.08]" />
            <div className="absolute inset-x-0 bottom-0 h-px bg-gradient-to-r from-transparent via-gray-200 to-transparent dark:via-white/[0.08]" />

            <div className="relative mx-auto max-w-6xl px-4 sm:px-6">
                <div className="mx-auto max-w-2xl text-center">
                    <div className="mb-4 inline-flex items-center gap-2 rounded-full border border-indigo-200 bg-indigo-50 px-3 py-1.5 text-sm text-indigo-700 dark:border-indigo-500/20 dark:bg-indigo-500/[0.08] dark:text-indigo-300">
                        <Rocket className="h-3.5 w-3.5" />
                        Up in minutes
                    </div>
                    <h2 className="text-4xl font-bold tracking-tight text-gray-900 sm:text-5xl dark:text-white">
                        From zero to{" "}
                        <span className="bg-gradient-to-r from-indigo-600 to-cyan-600 bg-clip-text text-transparent dark:from-indigo-400 dark:to-cyan-400">
                            production
                        </span>{" "}
                        in 3 steps
                    </h2>
                    <p className="mt-4 text-lg text-gray-600 dark:text-zinc-400">
                        No boilerplate hunting, no wiring auth for the tenth
                        time. Everything is ready.
                    </p>
                </div>

                <div className="mt-16 grid gap-5 md:grid-cols-3">
                    {STEPS.map((step) => {
                        const Icon = step.icon;
                        return (
                            <div
                                key={step.number}
                                className={`group relative rounded-2xl border border-gray-200 bg-white p-6 shadow-sm transition-all duration-300 hover:-translate-y-1 hover:shadow-md dark:border-white/[0.07] dark:bg-zinc-900/60 dark:shadow-none ${step.cardBorder}`}
                            >
                                <div className="mb-5 flex items-start justify-between">
                                    <div
                                        className={`inline-flex h-10 w-10 items-center justify-center rounded-xl ${step.iconClass}`}
                                    >
                                        <Icon className="h-5 w-5" />
                                    </div>
                                    <span
                                        className={`rounded-full border px-2.5 py-0.5 text-xs font-bold tracking-widest ${step.badgeClass}`}
                                    >
                                        {step.number}
                                    </span>
                                </div>
                                <h3 className="mb-2.5 text-lg font-semibold text-gray-900 dark:text-white">
                                    {step.title}
                                </h3>
                                <p className="mb-5 text-sm leading-relaxed text-gray-600 dark:text-zinc-400">
                                    {step.description}
                                </p>
                                <div
                                    className={`overflow-x-auto rounded-lg border p-3 font-mono text-[11px] ${step.snippetBg} ${step.snippetText}`}
                                >
                                    <span className="mr-2 text-gray-400 select-none dark:text-zinc-600">
                                        $
                                    </span>
                                    {step.snippet}
                                </div>
                            </div>
                        );
                    })}
                </div>
            </div>
        </section>
    );
}
