import { requirePermission } from "@/lib/server/require-permission";
import TaxConfigurationsClient from "./tax-configurations-client";

export default async function TaxConfigurationsPage() {
    await requirePermission("config:manage");

    return <TaxConfigurationsClient />;
}
