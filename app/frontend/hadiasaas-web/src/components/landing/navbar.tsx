"use client";

import { useState, useEffect } from "react";
import { LayoutDashboard, Menu, X, Sparkles } from "lucide-react";
import { useSession } from "next-auth/react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { ThemeSwitch } from "@/components/theme-switch";

const NAV_LINKS = [
    { label: "Features", href: "#features" },
    { label: "How it works", href: "#process" },
    { label: "Pricing", href: "#pricing" },
    { label: "Contact", href: "/contact" },
];

export function LandingNavbar() {
    const [scrolled, setScrolled] = useState(false);
    const [mobileOpen, setMobileOpen] = useState(false);
    const { status } = useSession();
    const isAuthenticated = status === "authenticated";

    useEffect(() => {
        const handleScroll = () => setScrolled(window.scrollY > 20);
        window.addEventListener("scroll", handleScroll, { passive: true });
        return () => window.removeEventListener("scroll", handleScroll);
    }, []);

    return (
        <header
            className={`fixed inset-x-0 top-0 z-50 transition-all duration-500 ${
                scrolled
                    ? "border-b border-black/[0.08] bg-white/80 shadow-sm backdrop-blur-2xl dark:border-white/[0.08] dark:bg-black/80 dark:shadow-black/30"
                    : "bg-transparent"
            }`}
        >
            <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 sm:px-6">
                {/* Logo */}
                <Link href="/" className="group flex items-center gap-2.5">
                    <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-violet-500 to-indigo-600 shadow-lg shadow-violet-500/30 transition-transform duration-200 group-hover:scale-110">
                        <Sparkles className="h-4 w-4 text-white" />
                    </div>
                    <span className="text-base font-semibold tracking-tight text-gray-900 dark:text-white">
                        Hadia
                        <span className="text-violet-600 dark:text-violet-400">
                            SaaS
                        </span>
                    </span>
                </Link>

                {/* Desktop nav */}
                <nav className="hidden items-center gap-1 md:flex">
                    {NAV_LINKS.map((link) => (
                        <Link
                            key={link.label}
                            href={link.href}
                            className="rounded-md px-3 py-1.5 text-sm text-gray-600 transition-colors hover:bg-gray-100 hover:text-gray-900 dark:text-zinc-400 dark:hover:bg-white/5 dark:hover:text-white"
                        >
                            {link.label}
                        </Link>
                    ))}
                </nav>

                {/* Desktop CTA */}
                <div className="hidden items-center gap-2 md:flex">
                    <ThemeSwitch />
                    {isAuthenticated ? (
                        <Button
                            size="sm"
                            asChild
                            className="bg-violet-600 text-white shadow-lg shadow-violet-500/25 hover:bg-violet-500 hover:shadow-violet-500/40"
                        >
                            <Link href="/dashboard">
                                <LayoutDashboard className="mr-1.5 h-3.5 w-3.5" />
                                Dashboard
                            </Link>
                        </Button>
                    ) : (
                        <>
                            <Button
                                variant="ghost"
                                size="sm"
                                asChild
                                className="text-gray-600 hover:bg-gray-100 hover:text-gray-900 dark:text-zinc-400 dark:hover:bg-white/5 dark:hover:text-white"
                            >
                                <Link href="/sign-in">Sign in</Link>
                            </Button>
                            <Button
                                size="sm"
                                asChild
                                className="bg-violet-600 text-white shadow-lg shadow-violet-500/25 hover:bg-violet-500 hover:shadow-violet-500/40"
                            >
                                <Link href="/sign-up">Get started →</Link>
                            </Button>
                        </>
                    )}
                </div>

                {/* Mobile toggle */}
                <button
                    className="rounded-md p-1.5 text-gray-600 transition-colors hover:bg-gray-100 hover:text-gray-900 md:hidden dark:text-zinc-400 dark:hover:bg-white/5 dark:hover:text-white"
                    onClick={() => setMobileOpen(!mobileOpen)}
                    aria-label="Toggle menu"
                >
                    {mobileOpen ? (
                        <X className="h-5 w-5" />
                    ) : (
                        <Menu className="h-5 w-5" />
                    )}
                </button>
            </div>

            {/* Mobile menu */}
            {mobileOpen && (
                <div className="border-t border-black/[0.08] bg-white/95 backdrop-blur-2xl md:hidden dark:border-white/[0.08] dark:bg-black/95">
                    <div className="mx-auto max-w-6xl space-y-1 px-4 py-3">
                        {NAV_LINKS.map((link) => (
                            <Link
                                key={link.label}
                                href={link.href}
                                className="block rounded-md px-3 py-2 text-sm text-gray-600 transition-colors hover:bg-gray-100 hover:text-gray-900 dark:text-zinc-400 dark:hover:bg-white/5 dark:hover:text-white"
                                onClick={() => setMobileOpen(false)}
                            >
                                {link.label}
                            </Link>
                        ))}
                        <div className="flex items-center justify-between border-t border-black/[0.08] pt-3 pb-1 dark:border-white/[0.08]">
                            <span className="px-3 text-xs text-gray-500 dark:text-zinc-500">
                                Theme
                            </span>
                            <ThemeSwitch />
                        </div>
                        <div className="flex flex-col gap-2">
                            {isAuthenticated ? (
                                <Button
                                    size="sm"
                                    asChild
                                    className="bg-violet-600 text-white hover:bg-violet-500"
                                >
                                    <Link
                                        href="/dashboard"
                                        onClick={() => setMobileOpen(false)}
                                    >
                                        <LayoutDashboard className="mr-1.5 h-3.5 w-3.5" />
                                        Dashboard
                                    </Link>
                                </Button>
                            ) : (
                                <>
                                    <Button
                                        variant="ghost"
                                        size="sm"
                                        asChild
                                        className="justify-start text-gray-600 dark:text-zinc-400"
                                    >
                                        <Link href="/sign-in">Sign in</Link>
                                    </Button>
                                    <Button
                                        size="sm"
                                        asChild
                                        className="bg-violet-600 text-white hover:bg-violet-500"
                                    >
                                        <Link href="/sign-up">
                                            Get started →
                                        </Link>
                                    </Button>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            )}
        </header>
    );
}
