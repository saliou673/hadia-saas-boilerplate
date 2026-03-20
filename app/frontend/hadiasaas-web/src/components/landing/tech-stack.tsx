const TECH = [
    { name: "Next.js", color: "text-gray-800 dark:text-white" },
    { name: "Spring Boot", color: "text-emerald-600 dark:text-emerald-400" },
    { name: "TypeScript", color: "text-blue-600 dark:text-blue-400" },
    { name: "PostgreSQL", color: "text-sky-600 dark:text-sky-400" },
    { name: "Docker", color: "text-cyan-600 dark:text-cyan-400" },
    { name: "Tailwind CSS", color: "text-teal-600 dark:text-teal-400" },
    { name: "Liquibase", color: "text-orange-600 dark:text-orange-400" },
    { name: "NextAuth", color: "text-violet-600 dark:text-violet-400" },
];

export function TechStack() {
    return (
        <section className="relative overflow-hidden border-y border-gray-200 bg-slate-50 py-10 dark:border-white/[0.06] dark:bg-zinc-950">
            {/* Edge fades */}
            <div className="pointer-events-none absolute inset-y-0 left-0 z-10 w-24 bg-gradient-to-r from-slate-50 to-transparent dark:from-zinc-950" />
            <div className="pointer-events-none absolute inset-y-0 right-0 z-10 w-24 bg-gradient-to-l from-slate-50 to-transparent dark:from-zinc-950" />

            <div className="mb-5 text-center text-xs font-medium tracking-widest text-gray-400 uppercase dark:text-zinc-600">
                Powered by the best technologies
            </div>

            <div className="flex gap-0 overflow-hidden">
                <div className="flex min-w-full shrink-0 animate-marquee items-center gap-10 pr-10">
                    {TECH.map((t) => (
                        <span
                            key={t.name}
                            className={`shrink-0 text-sm font-semibold tracking-tight ${t.color} opacity-70 transition-opacity hover:opacity-100`}
                        >
                            {t.name}
                        </span>
                    ))}
                </div>
                <div
                    aria-hidden="true"
                    className="flex min-w-full shrink-0 animate-marquee items-center gap-10 pr-10"
                >
                    {TECH.map((t) => (
                        <span
                            key={t.name}
                            className={`shrink-0 text-sm font-semibold tracking-tight ${t.color} opacity-70`}
                        >
                            {t.name}
                        </span>
                    ))}
                </div>
            </div>
        </section>
    );
}
