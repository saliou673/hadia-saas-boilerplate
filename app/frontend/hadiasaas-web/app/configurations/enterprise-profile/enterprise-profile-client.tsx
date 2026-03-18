"use client";

import { AuthenticatedLayout } from "@/components/layout/authenticated-layout";
import { ConfigurationsLayout } from "@/features/configurations/layout";
import { EnterpriseProfileFeature } from "@/features/enterprise-profile";

export default function EnterpriseProfileClient() {
    return (
        <AuthenticatedLayout>
            <ConfigurationsLayout>
                <EnterpriseProfileFeature />
            </ConfigurationsLayout>
        </AuthenticatedLayout>
    );
}
