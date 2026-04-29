"use client";

import { useEffect, useState } from "react";
import { type TaxConfiguration } from "@api-client";
import { Plus } from "lucide-react";
import { toast } from "sonner";
import {
    useNextNavigateSearch,
    useNextSearchObject,
} from "@/hooks/use-next-search-state";
import { Button } from "@/components/ui/button";
import { TaxConfigurationsDeleteDialog } from "./tax-configurations-delete-dialog";
import { TaxConfigurationsFormDialog } from "./tax-configurations-form-dialog";
import { TaxConfigurationsTable } from "./tax-configurations-table";

export function TaxConfigurations() {
    const search = useNextSearchObject();
    const navigate = useNextNavigateSearch();
    const [currentRow, setCurrentRow] = useState<TaxConfiguration | null>(null);
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

    const handleOnEdit = (taxConfiguration: TaxConfiguration) => {
        setCurrentRow(taxConfiguration);
        setMutateOpen(true);
    };

    const handleOnDelete = (taxConfiguration: TaxConfiguration) => {
        setCurrentRow(taxConfiguration);
        setDeleteOpen(true);
    };

    return (
        <>
            <div className="flex flex-1 flex-col gap-4 sm:gap-6">
                <div className="flex flex-wrap items-end justify-between gap-2">
                    <div>
                        <h2 className="text-2xl font-bold tracking-tight">
                            Tax Configurations
                        </h2>
                        <p className="text-muted-foreground">
                            Manage tax rates applied to subscriptions.
                        </p>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="text-sm text-muted-foreground">
                            {totalItems} tax rate
                            {totalItems === 1 ? "" : "s"}
                        </div>
                        <Button
                            onClick={() => {
                                setCurrentRow(null);
                                setMutateOpen(true);
                            }}
                        >
                            <Plus className="size-4" />
                            New Tax Rate
                        </Button>
                    </div>
                </div>

                <TaxConfigurationsTable
                    search={search}
                    navigate={navigate}
                    onEdit={handleOnEdit}
                    onDelete={handleOnDelete}
                    onTotalItemsChange={setTotalItems}
                />
            </div>

            <TaxConfigurationsFormDialog
                open={mutateOpen}
                onOpenChange={setMutateOpen}
                currentRow={currentRow}
            />
            <TaxConfigurationsDeleteDialog
                open={deleteOpen}
                onOpenChange={setDeleteOpen}
                currentRow={currentRow}
                onDeleted={() => toast.success("Tax configuration deleted.")}
            />
        </>
    );
}
