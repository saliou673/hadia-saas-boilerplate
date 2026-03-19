import type { Metadata } from "next";
import { requirePermission } from "@/lib/server/require-permission";
import EnterpriseProfileClient from "./enterprise-profile-client";

export const metadata: Metadata = {
    title: "Enterprise Profile",
};

export default async function EnterpriseProfilePage() {
    await requirePermission("config:manage");

    return <EnterpriseProfileClient />;
}
