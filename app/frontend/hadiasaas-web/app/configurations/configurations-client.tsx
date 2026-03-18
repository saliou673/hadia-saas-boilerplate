"use client";

import { AuthenticatedLayout } from "@/components/layout/authenticated-layout";
import { ConfigurationsLayout } from "@/features/configurations/layout";
import { Configurations } from "@/features/configurations";

export default function ConfigurationsClient() {
    return (
        <AuthenticatedLayout>
            <ConfigurationsLayout>
                <Configurations />
            </ConfigurationsLayout>
        </AuthenticatedLayout>
    );
}
