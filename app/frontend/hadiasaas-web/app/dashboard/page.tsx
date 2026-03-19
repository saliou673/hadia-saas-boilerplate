import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Dashboard",
};

const DashboardClient = dynamic(() => import("./dashboard-client"), {
    ssr: false,
});

export default function DashboardPage() {
    return <DashboardClient />;
}
