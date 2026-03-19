import type { Metadata } from "next";
import { requirePermission } from "@/lib/server/require-permission";
import UsersPageClient from "./users-page-client";

export const metadata: Metadata = {
    title: "Users",
};

export default async function UsersPage() {
    await requirePermission("user:read");

    return <UsersPageClient />;
}
