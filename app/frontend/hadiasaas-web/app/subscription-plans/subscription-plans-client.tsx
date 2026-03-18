"use client";

import { AuthenticatedLayout } from "@/components/layout/authenticated-layout";
import { Main } from "@/components/layout/main";
import { SubscriptionPlans } from "@/features/subscription-plans";

export default function SubscriptionPlansClient() {
    return (
        <AuthenticatedLayout>
            <Main fixed>
                <SubscriptionPlans />
            </Main>
        </AuthenticatedLayout>
    );
}
