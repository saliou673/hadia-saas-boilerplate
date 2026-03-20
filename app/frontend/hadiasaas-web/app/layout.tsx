import "@/styles/index.css";
import type { Metadata } from "next";
import { Providers } from "./providers";

export const metadata: Metadata = {
    title: {
        default: "Hadia SaaS",
        template: "%s | Hadia SaaS",
    },
    description: "Hadia SaaS dashboard",
};

type RootLayoutProps = Readonly<{
    children: React.ReactNode;
}>;

export default function RootLayout({ children }: RootLayoutProps) {
    return (
        <html lang="en" suppressHydrationWarning>
            <head>
                {/* Prevent FOUC: read cookie and apply theme class before React hydrates */}
                <script
                    dangerouslySetInnerHTML={{
                        __html: `(function(){try{var m=/vite-ui-theme=([^;]+)/.exec(document.cookie);var t=m?decodeURIComponent(m[1]):'system';if(t==='system')t=window.matchMedia('(prefers-color-scheme: dark)').matches?'dark':'light';document.documentElement.classList.add(t);}catch(e){}})()`,
                    }}
                />
            </head>
            <body>
                <Providers>{children}</Providers>
            </body>
        </html>
    );
}
