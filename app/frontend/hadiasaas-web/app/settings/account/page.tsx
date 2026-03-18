"use client";

import dynamic from "next/dynamic";

const SettingsAccountClient = dynamic(
    () => import("./settings-account-client"),
    { ssr: false }
);

export default function SettingsAccountPage() {
    return <SettingsAccountClient />;
}
