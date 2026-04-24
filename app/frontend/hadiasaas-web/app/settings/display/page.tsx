import type { Metadata } from "next";
import SettingsDisplayClient from "./settings-display-client";

export const metadata: Metadata = {
    title: "Display Settings",
};

export default function SettingsDisplayPage() {
    return <SettingsDisplayClient />;
}
