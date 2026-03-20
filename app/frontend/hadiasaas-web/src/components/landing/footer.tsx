import { Sparkles, Github, Twitter } from "lucide-react";
import Link from "next/link";

const FOOTER_LINKS = {
    Product: [
        { label: "Features", href: "#features" },
        { label: "Pricing", href: "#pricing" },
        { label: "Changelog", href: "#" },
        { label: "Roadmap", href: "#" },
    ],
    Developers: [
        { label: "Documentation", href: "#" },
        { label: "GitHub", href: "https://github.com" },
        { label: "API Reference", href: "#" },
        { label: "Status", href: "#" },
    ],
    Company: [
        { label: "About", href: "#" },
        { label: "Blog", href: "#" },
        { label: "Careers", href: "#" },
        { label: "Contact", href: "#" },
    ],
    Legal: [
        { label: "Privacy", href: "#" },
        { label: "Terms", href: "#" },
        { label: "Cookie Policy", href: "#" },
        { label: "Licenses", href: "#" },
    ],
};

export function Footer() {
    return (
        <footer className="relative bg-white dark:bg-black">
            <div className="absolute inset-x-0 top-0 h-px bg-gradient-to-r from-transparent via-gray-200 to-transparent dark:via-white/[0.08]" />

            <div className="mx-auto max-w-6xl px-4 pt-16 pb-12 sm:px-6">
                <div className="grid gap-10 sm:grid-cols-2 md:grid-cols-5">
                    {/* Brand column */}
                    <div className="sm:col-span-2 md:col-span-1">
                        <Link
                            href="/"
                            className="mb-4 inline-flex items-center gap-2.5"
                        >
                            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-violet-500 to-indigo-600">
                                <Sparkles className="h-4 w-4 text-white" />
                            </div>
                            <span className="text-base font-semibold text-gray-900 dark:text-white">
                                Hadia
                                <span className="text-violet-600 dark:text-violet-400">
                                    SaaS
                                </span>
                            </span>
                        </Link>
                        <p className="mb-5 max-w-xs text-sm leading-relaxed text-gray-500 dark:text-zinc-500">
                            The production-ready SaaS boilerplate. Build and
                            ship your product faster.
                        </p>
                        <div className="flex gap-3">
                            <Link
                                href="https://github.com"
                                target="_blank"
                                rel="noreferrer"
                                className="text-gray-400 transition-colors hover:text-gray-700 dark:text-zinc-600 dark:hover:text-zinc-300"
                                aria-label="GitHub"
                            >
                                <Github className="h-5 w-5" />
                            </Link>
                            <Link
                                href="https://twitter.com"
                                target="_blank"
                                rel="noreferrer"
                                className="text-gray-400 transition-colors hover:text-gray-700 dark:text-zinc-600 dark:hover:text-zinc-300"
                                aria-label="Twitter"
                            >
                                <Twitter className="h-5 w-5" />
                            </Link>
                        </div>
                    </div>

                    {/* Link columns */}
                    {Object.entries(FOOTER_LINKS).map(([category, links]) => (
                        <div key={category}>
                            <h4 className="mb-4 text-[11px] font-semibold tracking-widest text-gray-500 uppercase dark:text-zinc-500">
                                {category}
                            </h4>
                            <ul className="space-y-3">
                                {links.map((link) => (
                                    <li key={link.label}>
                                        <Link
                                            href={link.href}
                                            className="text-sm text-gray-500 transition-colors hover:text-gray-800 dark:text-zinc-500 dark:hover:text-zinc-300"
                                        >
                                            {link.label}
                                        </Link>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </div>

                <div className="mt-14 flex flex-col items-center justify-between gap-3 border-t border-gray-100 pt-8 sm:flex-row dark:border-white/[0.05]">
                    <p className="text-sm text-gray-400 dark:text-zinc-600">
                        © {new Date().getFullYear()} HadiaSaaS. All rights
                        reserved.
                    </p>
                    <p className="text-xs text-gray-300 dark:text-zinc-700">
                        Built with Next.js · Spring Boot · TypeScript
                    </p>
                </div>
            </div>
        </footer>
    );
}
