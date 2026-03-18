import EnterpriseProfileClient from "./enterprise-profile-client";
import { requirePermission } from "@/lib/server/require-permission";

export default async function EnterpriseProfilePage() {
    await requirePermission("config:manage");

    return <EnterpriseProfileClient />;
}
