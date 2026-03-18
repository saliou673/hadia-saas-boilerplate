import StorageSettingsClient from "./storage-settings-client";
import { requirePermission } from "@/lib/server/require-permission";

export default async function StorageSettingsPage() {
    await requirePermission("config:manage");

    return <StorageSettingsClient />;
}
