import ConfigurationsClient from "./configurations-client";
import { requirePermission } from "@/lib/server/require-permission";

export default async function ConfigurationsPage() {
    await requirePermission("config:manage");

    return <ConfigurationsClient />;
}
