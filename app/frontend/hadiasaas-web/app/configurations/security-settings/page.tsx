import { requirePermission } from "@/lib/server/require-permission";
import SecuritySettingsClient from "./security-settings-client";

export default async function SecuritySettingsPage() {
    await requirePermission("config:manage");

    return <SecuritySettingsClient />;
}
