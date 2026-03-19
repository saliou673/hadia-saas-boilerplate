import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Help Center",
};

const HelpCenterClient = dynamic(() => import("./help-center-client"), {
    ssr: false,
});

export default function HelpCenterPage() {
    return <HelpCenterClient />;
}
