"use client";

import { useEffect, useMemo, useState } from "react";
import {
    type ColumnFiltersState,
    type VisibilityState,
    flexRender,
    getCoreRowModel,
    useReactTable,
} from "@tanstack/react-table";
import {
    type StorageSettings,
    type StorageProviderFilterEqualsEnumKey,
    type StorageProviderFilterInEnumKey,
    type GetStorageSettingsAsAdminQueryParams,
    useGetStorageSettingsAsAdmin,
} from "@api-client";
import { cn } from "@/lib/utils";
import { type NavigateFn, useTableUrlState } from "@/hooks/use-table-url-state";
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from "@/components/ui/table";
import { DataTablePagination } from "@/components/data-table";
import { buildStorageSettingsColumns } from "./storage-settings-columns";
import { StorageSettingsToolbar } from "./storage-settings-toolbar";

type StorageSettingsTableProps = {
    search: Record<string, unknown>;
    navigate: NavigateFn;
    onEdit: (storageSettings: StorageSettings) => void;
    onDelete: (storageSettings: StorageSettings) => void;
    onTotalItemsChange?: (totalItems: number) => void;
};

function toStringArray(value: unknown): string[] {
    if (Array.isArray(value)) {
        return value.filter(
            (item): item is string =>
                typeof item === "string" && item.length > 0
        );
    }
    if (typeof value === "string" && value.length > 0) return [value];
    return [];
}

function toBooleanArray(value: unknown): boolean[] {
    return toStringArray(value)
        .map((item) => {
            if (item === "true") return true;
            if (item === "false") return false;
            return null;
        })
        .filter((item): item is boolean => item !== null);
}

function getArrayFilterValue(
    columnFilters: ColumnFiltersState,
    columnId: string
): string[] {
    const filter = columnFilters.find((item) => item.id === columnId);
    if (!filter) return [];
    if (Array.isArray(filter.value)) {
        return filter.value.filter(
            (item): item is string =>
                typeof item === "string" && item.length > 0
        );
    }
    if (typeof filter.value === "string" && filter.value.length > 0) {
        return [filter.value];
    }
    return [];
}

function buildQueryParams(
    columnFilters: ColumnFiltersState,
    page: number,
    size: number
): GetStorageSettingsAsAdminQueryParams {
    const providers = getArrayFilterValue(columnFilters, "provider");
    const active = toBooleanArray(getArrayFilterValue(columnFilters, "active"));

    return {
        filter: {
            provider:
                providers.length === 1
                    ? {
                          equals: providers[0] as StorageProviderFilterEqualsEnumKey,
                      }
                    : providers.length > 1
                      ? {
                            in: providers as StorageProviderFilterInEnumKey[],
                        }
                      : undefined,
            active:
                active.length === 1
                    ? { equals: active[0] }
                    : active.length > 1
                      ? { in: active }
                      : undefined,
        },
        pageable: { page, size },
    };
}

export function StorageSettingsTable({
    search,
    navigate,
    onEdit,
    onDelete,
    onTotalItemsChange,
}: StorageSettingsTableProps) {
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>(
        {}
    );

    const {
        columnFilters,
        onColumnFiltersChange,
        pagination,
        onPaginationChange,
        ensurePageInRange,
    } = useTableUrlState({
        search,
        navigate,
        pagination: { defaultPage: 1, defaultPageSize: 10 },
        globalFilter: { enabled: false },
        columnFilters: [
            { columnId: "provider", searchKey: "provider", type: "array" },
            {
                columnId: "active",
                searchKey: "active",
                type: "array",
                serialize: (value) => value,
                deserialize: (value) =>
                    Array.isArray(value)
                        ? value.map(String)
                        : value !== undefined
                          ? [String(value)]
                          : [],
            },
        ],
    });

    const queryParams = useMemo(
        () =>
            buildQueryParams(
                columnFilters,
                pagination.pageIndex,
                pagination.pageSize
            ),
        [columnFilters, pagination.pageIndex, pagination.pageSize]
    );

    const { data, isLoading, isError } =
        useGetStorageSettingsAsAdmin(queryParams);

    const items = data?.items ?? [];
    const totalPages = data?.totalPages ?? 0;
    const totalItems = data?.totalItems ?? 0;
    const columns = buildStorageSettingsColumns({ onEdit, onDelete });

    // eslint-disable-next-line react-hooks/incompatible-library
    const table = useReactTable({
        data: items,
        columns,
        pageCount: totalPages,
        state: {
            pagination,
            columnFilters,
            columnVisibility,
        },
        manualPagination: true,
        manualFiltering: true,
        onPaginationChange,
        onColumnFiltersChange,
        onColumnVisibilityChange: setColumnVisibility,
        getCoreRowModel: getCoreRowModel(),
    });

    useEffect(() => {
        ensurePageInRange(totalPages, { resetTo: "last" });
    }, [ensurePageInRange, totalPages]);

    useEffect(() => {
        onTotalItemsChange?.(totalItems);
    }, [onTotalItemsChange, totalItems]);

    return (
        <div
            className={cn(
                'max-sm:has-[div[role="toolbar"]]:mb-16',
                "flex flex-1 flex-col gap-4"
            )}
        >
            <StorageSettingsToolbar
                table={table}
                onReset={() => {
                    table.resetColumnFilters();
                    navigate({
                        search: (prev) => ({
                            ...prev,
                            page: undefined,
                            provider: undefined,
                            active: undefined,
                        }),
                    });
                }}
            />
            <div className="overflow-hidden rounded-md border">
                <Table>
                    <TableHeader>
                        {table.getHeaderGroups().map((headerGroup) => (
                            <TableRow
                                key={headerGroup.id}
                                className="group/row"
                            >
                                {headerGroup.headers.map((header) => (
                                    <TableHead
                                        key={header.id}
                                        colSpan={header.colSpan}
                                        className={cn(
                                            "bg-background group-hover/row:bg-muted group-data-[state=selected]/row:bg-muted",
                                            header.column.columnDef.meta
                                                ?.className,
                                            header.column.columnDef.meta
                                                ?.thClassName
                                        )}
                                    >
                                        {header.isPlaceholder
                                            ? null
                                            : flexRender(
                                                  header.column.columnDef
                                                      .header,
                                                  header.getContext()
                                              )}
                                    </TableHead>
                                ))}
                            </TableRow>
                        ))}
                    </TableHeader>
                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="h-24 text-center"
                                >
                                    Loading storage settings...
                                </TableCell>
                            </TableRow>
                        ) : isError ? (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="h-24 text-center text-destructive"
                                >
                                    Failed to load storage settings.
                                </TableCell>
                            </TableRow>
                        ) : table.getRowModel().rows.length ? (
                            table.getRowModel().rows.map((row) => (
                                <TableRow key={row.id} className="group/row">
                                    {row.getVisibleCells().map((cell) => (
                                        <TableCell
                                            key={cell.id}
                                            className={cn(
                                                "bg-background group-hover/row:bg-muted",
                                                cell.column.columnDef.meta
                                                    ?.className,
                                                cell.column.columnDef.meta
                                                    ?.tdClassName
                                            )}
                                        >
                                            {flexRender(
                                                cell.column.columnDef.cell,
                                                cell.getContext()
                                            )}
                                        </TableCell>
                                    ))}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="h-24 text-center"
                                >
                                    No storage settings found.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
            <DataTablePagination table={table} className="mt-auto" />
        </div>
    );
}
