import type { Metadata } from "next";
import { requirePermission } from "@/lib/server/require-permission";
import TaxConfigurationsClient from "./tax-configurations-client";

export const metadata: Metadata = {
    title: "Tax Configurations",
};

export default async function TaxConfigurationsPage() {
    await requirePermission("config:manage");

    return <TaxConfigurationsClient />;
}
