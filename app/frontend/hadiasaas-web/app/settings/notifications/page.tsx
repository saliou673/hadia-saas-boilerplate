import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Notification Settings",
};

const SettingsNotificationsClient = dynamic(
    () => import("./settings-notifications-client"),
    { ssr: false }
);

export default function SettingsNotificationsPage() {
    return <SettingsNotificationsClient />;
}
