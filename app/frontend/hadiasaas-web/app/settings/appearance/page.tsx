import type { Metadata } from "next";
import SettingsAppearanceClient from "./settings-appearance-client";

export const metadata: Metadata = {
    title: "Appearance Settings",
};

export default function SettingsAppearancePage() {
    return <SettingsAppearanceClient />;
}
