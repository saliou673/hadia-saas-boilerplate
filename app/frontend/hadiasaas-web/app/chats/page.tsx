import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Chats",
};

const ChatsClient = dynamic(() => import("./chats-client"), { ssr: false });

export default function ChatsPage() {
    return <ChatsClient />;
}
