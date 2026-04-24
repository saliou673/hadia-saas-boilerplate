import type { Metadata } from "next";
import SettingsNotificationsClient from "./settings-notifications-client";

export const metadata: Metadata = {
    title: "Notification Settings",
};

export default function SettingsNotificationsPage() {
    return <SettingsNotificationsClient />;
}
