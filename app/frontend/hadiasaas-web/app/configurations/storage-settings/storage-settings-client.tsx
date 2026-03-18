"use client";

import { AuthenticatedLayout } from "@/components/layout/authenticated-layout";
import { ConfigurationsLayout } from "@/features/configurations/layout";
import { StorageSettingsFeature } from "@/features/storage-settings";

export default function StorageSettingsClient() {
    return (
        <AuthenticatedLayout>
            <ConfigurationsLayout>
                <StorageSettingsFeature />
            </ConfigurationsLayout>
        </AuthenticatedLayout>
    );
}
