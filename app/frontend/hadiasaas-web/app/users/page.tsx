import { requirePermission } from "@/lib/server/require-permission";
import UsersPageClient from "./users-page-client";

export default async function UsersPage() {
    await requirePermission("user:read");

    return <UsersPageClient />;
}
