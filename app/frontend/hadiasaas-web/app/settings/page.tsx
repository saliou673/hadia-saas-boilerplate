import type { Metadata } from "next";
import SettingsProfileClient from "./settings-profile-client";

export const metadata: Metadata = {
    title: "Settings",
};

export default function SettingsPage() {
    return <SettingsProfileClient />;
}
