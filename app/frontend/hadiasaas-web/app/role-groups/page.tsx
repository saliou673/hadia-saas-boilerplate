import { requirePermission } from "@/lib/server/require-permission";
import RoleGroupsPageClient from "./role-groups-page-client";

export default async function RoleGroupsPage() {
    await requirePermission("role-group:read");

    return <RoleGroupsPageClient />;
}
