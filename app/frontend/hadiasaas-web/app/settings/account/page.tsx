import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Account Settings",
};

const SettingsAccountClient = dynamic(
    () => import("./settings-account-client"),
    { ssr: false }
);

export default function SettingsAccountPage() {
    return <SettingsAccountClient />;
}
