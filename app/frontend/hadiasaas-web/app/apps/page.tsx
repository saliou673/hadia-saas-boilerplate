import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Apps",
};

const AppsClient = dynamic(() => import("./apps-client"), { ssr: false });

export default function AppsPage() {
    return <AppsClient />;
}
