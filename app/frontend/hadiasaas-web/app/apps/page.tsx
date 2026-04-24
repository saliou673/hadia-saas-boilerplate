import type { Metadata } from "next";
import AppsClient from "./apps-client";

export const metadata: Metadata = {
    title: "Apps",
};

export default function AppsPage() {
    return <AppsClient />;
}
