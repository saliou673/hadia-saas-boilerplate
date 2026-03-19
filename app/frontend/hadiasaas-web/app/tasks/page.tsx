import type { Metadata } from "next";
import dynamic from "next/dynamic";

export const metadata: Metadata = {
    title: "Tasks",
};

const TasksClient = dynamic(() => import("./tasks-client"), { ssr: false });

export default function TasksPage() {
    return <TasksClient />;
}
