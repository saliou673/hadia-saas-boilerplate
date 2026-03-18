"use client";

import dynamic from "next/dynamic";

const ChatsClient = dynamic(() => import("./chats-client"), { ssr: false });

export default function ChatsPage() {
    return <ChatsClient />;
}
