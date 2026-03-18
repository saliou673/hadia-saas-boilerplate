"use client";

import { AuthenticatedLayout } from "@/components/layout/authenticated-layout";
import { ConfigurationsLayout } from "@/features/configurations/layout";
import { TaxConfigurations } from "@/features/tax-configurations";

export default function TaxConfigurationsClient() {
    return (
        <AuthenticatedLayout>
            <ConfigurationsLayout>
                <TaxConfigurations />
            </ConfigurationsLayout>
        </AuthenticatedLayout>
    );
}
