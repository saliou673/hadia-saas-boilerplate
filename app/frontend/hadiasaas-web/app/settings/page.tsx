"use client";

import dynamic from "next/dynamic";

const SettingsProfileClient = dynamic(
    () => import("./settings-profile-client"),
    {
        ssr: false,
    }
);

export default function SettingsPage() {
    return <SettingsProfileClient />;
}
