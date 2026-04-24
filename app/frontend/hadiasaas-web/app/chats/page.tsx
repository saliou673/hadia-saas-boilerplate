import type { Metadata } from "next";
import ChatsClient from "./chats-client";

export const metadata: Metadata = {
    title: "Chats",
};

export default function ChatsPage() {
    return <ChatsClient />;
}
