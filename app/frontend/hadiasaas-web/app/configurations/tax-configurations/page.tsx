import TaxConfigurationsClient from "./tax-configurations-client";
import { requirePermission } from "@/lib/server/require-permission";

export default async function TaxConfigurationsPage() {
    await requirePermission("config:manage");

    return <TaxConfigurationsClient />;
}
