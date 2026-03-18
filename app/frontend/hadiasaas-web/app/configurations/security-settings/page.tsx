import SecuritySettingsClient from "./security-settings-client";
import { requirePermission } from "@/lib/server/require-permission";

export default async function SecuritySettingsPage() {
    await requirePermission("config:manage");

    return <SecuritySettingsClient />;
}
