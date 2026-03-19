import Link from "next/link";

export default function HomePage() {
    return (
        <main className="container py-10">
            <h1 className="text-3xl font-bold tracking-tight">Hadia SaaS</h1>
            <p className="mt-3 max-w-2xl text-muted-foreground">
                App Router migration is in progress. Existing Vite/TanStack
                routes are being moved incrementally.
            </p>
            <div className="mt-6 flex gap-3">
                <Link className="underline underline-offset-4" href="/sign-in">
                    Sign in
                </Link>
                <Link
                    className="underline underline-offset-4"
                    href="/dashboard"
                >
                    Dashboard
                </Link>
                <Link
                    className="underline underline-offset-4"
                    href="/recover-account"
                >
                    Recover account
                </Link>
            </div>
        </main>
    );
}
