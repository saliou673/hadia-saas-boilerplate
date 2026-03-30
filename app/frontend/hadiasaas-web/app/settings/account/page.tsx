import type { Metadata } from "next";
import SettingsAccountClient from "./settings-account-client";

export const metadata: Metadata = {
    title: "Account Settings",
};

export default function SettingsAccountPage() {
    return <SettingsAccountClient />;
}
