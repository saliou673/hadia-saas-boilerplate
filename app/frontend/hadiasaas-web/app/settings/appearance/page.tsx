import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Appearance Settings",
};

const SettingsAppearanceClient = dynamic(
    () => import("./settings-appearance-client"),
    { ssr: false }
);

export default function SettingsAppearancePage() {
    return <SettingsAppearanceClient />;
}
