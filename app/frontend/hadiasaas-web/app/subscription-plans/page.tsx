import { requirePermission } from "@/lib/server/require-permission";
import SubscriptionPlansClient from "./subscription-plans-client";

export default async function SubscriptionPlansPage() {
    await requirePermission("plan:read");

    return <SubscriptionPlansClient />;
}
