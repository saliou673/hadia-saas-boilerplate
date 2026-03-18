"use client";

import dynamic from "next/dynamic";

const SettingsAppearanceClient = dynamic(
    () => import("./settings-appearance-client"),
    { ssr: false }
);

export default function SettingsAppearancePage() {
    return <SettingsAppearanceClient />;
}
