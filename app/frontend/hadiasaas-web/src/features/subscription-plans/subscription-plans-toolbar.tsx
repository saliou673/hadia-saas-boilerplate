"use client";

import { Cross2Icon } from "@radix-ui/react-icons";
import { type Table } from "@tanstack/react-table";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { DataTableFacetedFilter } from "@/components/data-table/faceted-filter";
import { DataTableViewOptions } from "@/components/data-table/view-options";
import { activeOptions } from "./data";

type SubscriptionPlansToolbarProps<TData> = {
    table: Table<TData>;
    titleFilter: string;
    onTitleFilterChange: (value: string) => void;
    onReset: () => void;
};

export function SubscriptionPlansToolbar<TData>({
    table,
    titleFilter,
    onTitleFilterChange,
    onReset,
}: SubscriptionPlansToolbarProps<TData>) {
    const isFiltered =
        titleFilter.trim().length > 0 ||
        table.getState().columnFilters.length > 0;

    const handleTitleFilterChange = (
        event: ChangeEvent<HTMLInputElement, HTMLInputElement>
    ) => {
        onTitleFilterChange(event.target.value);
    };
    return (
        <div className="flex items-center justify-between">
            <div
                suppressHydrationWarning
                className="flex flex-1 flex-col-reverse items-start gap-y-2 sm:flex-row sm:items-center sm:space-x-2"
            >
                <Input
                    placeholder="Filter by title..."
                    value={titleFilter}
                    onChange={handleTitleFilterChange}
                    className="h-8 w-[150px] lg:w-[250px]"
                />
                <div className="flex gap-x-2">
                    <DataTableFacetedFilter
                        column={table.getColumn("active")}
                        title="Status"
                        options={activeOptions.map((o) => ({ ...o }))}
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
