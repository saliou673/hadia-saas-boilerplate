import { requirePermission } from "@/lib/server/require-permission";
import StorageSettingsClient from "./storage-settings-client";

export default async function StorageSettingsPage() {
    await requirePermission("config:manage");

    return <StorageSettingsClient />;
}
