import type { Metadata } from "next";
import { requirePermission } from "@/lib/server/require-permission";
import SubscriptionPlansClient from "./subscription-plans-client";

export const metadata: Metadata = {
    title: "Subscription Plans",
};

export default async function SubscriptionPlansPage() {
    await requirePermission("plan:read");

    return <SubscriptionPlansClient />;
}
