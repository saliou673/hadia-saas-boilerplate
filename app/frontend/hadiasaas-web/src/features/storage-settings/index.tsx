"use client";

import { useEffect, useState } from "react";
import { type StorageSettings } from "@api-client";
import { Plus } from "lucide-react";
import { toast } from "sonner";
import {
    useNextNavigateSearch,
    useNextSearchObject,
} from "@/hooks/use-next-search-state";
import { Button } from "@/components/ui/button";
import { StorageSettingsDeleteDialog } from "./storage-settings-delete-dialog";
import { StorageSettingsFormDialog } from "./storage-settings-form-dialog";
import { StorageSettingsTable } from "./storage-settings-table";

export function StorageSettingsFeature() {
    const search = useNextSearchObject();
    const navigate = useNextNavigateSearch();
    const [currentRow, setCurrentRow] = useState<StorageSettings | null>(null);
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

    const handleOnEdit = (storageSettings: StorageSettings) => {
        setCurrentRow(storageSettings);
        setMutateOpen(true);
    };

    const handleOnDelete = (storageSettings: StorageSettings) => {
        setCurrentRow(storageSettings);
        setDeleteOpen(true);
    };

    return (
        <>
            <div className="flex flex-1 flex-col gap-4 sm:gap-6">
                <div className="flex flex-wrap items-end justify-between gap-2">
                    <div>
                        <h2 className="text-2xl font-bold tracking-tight">
                            Storage Settings
                        </h2>
                        <p className="text-muted-foreground">
                            Manage file storage providers.
                        </p>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="text-sm text-muted-foreground">
                            {totalItems} provider
                            {totalItems === 1 ? "" : "s"}
                        </div>
                        <Button
                            onClick={() => {
                                setCurrentRow(null);
                                setMutateOpen(true);
                            }}
                        >
                            <Plus className="size-4" />
                            New Provider
                        </Button>
                    </div>
                </div>

                <StorageSettingsTable
                    search={search}
                    navigate={navigate}
                    onEdit={handleOnEdit}
                    onDelete={handleOnDelete}
                    onTotalItemsChange={setTotalItems}
                />
            </div>

            <StorageSettingsFormDialog
                open={mutateOpen}
                onOpenChange={setMutateOpen}
                currentRow={currentRow}
            />
            <StorageSettingsDeleteDialog
                open={deleteOpen}
                onOpenChange={setDeleteOpen}
                currentRow={currentRow}
                onDeleted={() => toast.success("Storage settings deleted.")}
            />
        </>
    );
}
