"use client";

import { useEffect, useState } from "react";
import { type AppConfiguration } from "@api-client";
import { Plus } from "lucide-react";
import { toast } from "sonner";
import {
    useNextNavigateSearch,
    useNextSearchObject,
} from "@/hooks/use-next-search-state";
import { Button } from "@/components/ui/button";
import { Main } from "@/components/layout/main";
import { ConfigurationsDeleteDialog } from "./configurations-delete-dialog";
import { ConfigurationsMutateDrawer } from "./configurations-mutate-drawer";
import { ConfigurationsTable } from "./configurations-table";

export function Configurations() {
    const search = useNextSearchObject();
    const navigate = useNextNavigateSearch();
    const [currentRow, setCurrentRow] = useState<AppConfiguration | null>(null);
    const [deleteOpen, setDeleteOpen] = useState(false);
    const [mutateOpen, setMutateOpen] = useState(false);
    const [totalItems, setTotalItems] = useState(0);

    useEffect(() => {
        if (!deleteOpen) {
            const timeout = window.setTimeout(() => {
                setCurrentRow(null);
            }, 200);

            return () => window.clearTimeout(timeout);
        }
    }, [deleteOpen]);

    const handleOnEdit = (configuration: AppConfiguration) => {
        setCurrentRow(configuration);
        setMutateOpen(true);
    }

    const handleOnDelete = (configuration: AppConfiguration) => {
        setCurrentRow(configuration);
        setDeleteOpen(true);
    }

    return (
        <>
            <Main className="flex flex-1 flex-col gap-4 sm:gap-6">
                <div className="flex flex-wrap items-end justify-between gap-2">
                    <div>
                        <h2 className="text-2xl font-bold tracking-tight">
                            Application Configurations
                        </h2>
                        <p className="text-muted-foreground">
                            Manage application configurations.
                        </p>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="text-sm text-muted-foreground">
                            {totalItems} configuration
                            {totalItems === 1 ? "" : "s"}
                        </div>
                        <Button
                            onClick={() => {
                                setCurrentRow(null);
                                setMutateOpen(true);
                            }}
                        >
                            <Plus className="size-4" />
                            New Configuration
                        </Button>
                    </div>
                </div>

                <ConfigurationsTable
                    search={search}
                    navigate={navigate}
                    onEdit={handleOnEdit}
                    onDelete={handleOnDelete}
                    onTotalItemsChange={setTotalItems}
                />
            </Main>

            <ConfigurationsMutateDrawer
                open={mutateOpen}
                onOpenChange={setMutateOpen}
                currentRow={currentRow}
            />
            <ConfigurationsDeleteDialog
                open={deleteOpen}
                onOpenChange={setDeleteOpen}
                currentRow={currentRow}
                onDeleted={() => toast.success("Configuration deleted.")}
            />
        </>
    );
}
