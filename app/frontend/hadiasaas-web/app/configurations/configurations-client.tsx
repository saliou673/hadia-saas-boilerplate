"use client";

import { AuthenticatedLayout } from "@/components/layout/authenticated-layout";
import { Configurations } from "@/features/configurations";

export default function ConfigurationsClient() {
    return (
        <AuthenticatedLayout>
            <Configurations />
        </AuthenticatedLayout>
    );
}
