import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Settings",
};

const SettingsProfileClient = dynamic(
    () => import("./settings-profile-client"),
    {
        ssr: false,
    }
);

export default function SettingsPage() {
    return <SettingsProfileClient />;
}
