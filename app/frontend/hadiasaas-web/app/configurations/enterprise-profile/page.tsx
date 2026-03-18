import { requirePermission } from "@/lib/server/require-permission";
import EnterpriseProfileClient from "./enterprise-profile-client";

export default async function EnterpriseProfilePage() {
    await requirePermission("config:manage");

    return <EnterpriseProfileClient />;
}
