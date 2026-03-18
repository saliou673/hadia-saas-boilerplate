"use client";

import dynamic from "next/dynamic";

const SettingsDisplayClient = dynamic(
    () => import("./settings-display-client"),
    { ssr: false }
);

export default function SettingsDisplayPage() {
    return <SettingsDisplayClient />;
}
