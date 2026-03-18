"use client";

import { Cross2Icon } from "@radix-ui/react-icons";
import { type Table } from "@tanstack/react-table";
import { Button } from "@/components/ui/button";
import { DataTableFacetedFilter } from "@/components/data-table/faceted-filter";
import { DataTableViewOptions } from "@/components/data-table/view-options";
import { activeOptions, providerOptions } from "./data";

type StorageSettingsToolbarProps<TData> = {
    table: Table<TData>;
    onReset: () => void;
};

export function StorageSettingsToolbar<TData>({
    table,
    onReset,
}: StorageSettingsToolbarProps<TData>) {
    const isFiltered = table.getState().columnFilters.length > 0;

    return (
        <div className="flex items-center justify-between">
            <div
                suppressHydrationWarning
                className="flex flex-1 flex-col-reverse items-start gap-y-2 sm:flex-row sm:items-center sm:space-x-2"
            >
                <div className="flex gap-x-2">
                    <DataTableFacetedFilter
                        column={table.getColumn("provider")}
                        title="Provider"
                        options={providerOptions}
                    />
                    <DataTableFacetedFilter
                        column={table.getColumn("active")}
                        title="Status"
                        options={activeOptions}
                    />
                </div>
                {isFiltered && (
                    <Button
                        variant="ghost"
                        onClick={onReset}
                        className="h-8 px-2 lg:px-3"
                    >
                        Reset
                        <Cross2Icon className="ms-2 h-4 w-4" />
                    </Button>
                )}
            </div>
            <DataTableViewOptions table={table} />
        </div>
    );
}
