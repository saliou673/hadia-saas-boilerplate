"use client";

import dynamic from "next/dynamic";

const TasksClient = dynamic(() => import("./tasks-client"), { ssr: false });

export default function TasksPage() {
    return <TasksClient />;
}
