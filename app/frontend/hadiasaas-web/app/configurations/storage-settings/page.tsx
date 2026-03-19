import type { Metadata } from "next";
import { requirePermission } from "@/lib/server/require-permission";
import StorageSettingsClient from "./storage-settings-client";

export const metadata: Metadata = {
    title: "Storage Settings",
};

export default async function StorageSettingsPage() {
    await requirePermission("config:manage");

    return <StorageSettingsClient />;
}
