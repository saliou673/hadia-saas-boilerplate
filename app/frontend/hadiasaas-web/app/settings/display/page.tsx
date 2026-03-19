import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Display Settings",
};

const SettingsDisplayClient = dynamic(
    () => import("./settings-display-client"),
    { ssr: false }
);

export default function SettingsDisplayPage() {
    return <SettingsDisplayClient />;
}
