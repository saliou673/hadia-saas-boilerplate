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
    type GetSubscriptionPlansAsAdminQueryParams,
    type SubscriptionPlan,
    useGetSubscriptionPlansAsAdmin,
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
import { buildSubscriptionPlansColumns } from "./subscription-plans-columns";
import { SubscriptionPlansToolbar } from "./subscription-plans-toolbar";

type SubscriptionPlansTableProps = {
    search: Record<string, unknown>;
    navigate: NavigateFn;
    onEdit: (plan: SubscriptionPlan) => void;
    onDelete: (plan: SubscriptionPlan) => void;
    onPreview: (plan: SubscriptionPlan) => void;
    onTotalItemsChange?: (totalItems: number) => void;
};

function toStringValue(value: unknown): string {
    return typeof value === "string" ? value : "";
}

function toStringArray(value: unknown): string[] {
    if (Array.isArray(value)) {
        return value.filter(
            (item): item is string =>
                typeof item === "string" && item.length > 0
        );
    }

    if (typeof value === "string" && value.length > 0) {
        return [value];
    }

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
    title: string,
    columnFilters: ColumnFiltersState,
    page: number,
    size: number
): GetSubscriptionPlansAsAdminQueryParams {
    const trimmedTitle = title.trim();
    const active = toBooleanArray(getArrayFilterValue(columnFilters, "active"));

    return {
        filter: {
            title: trimmedTitle ? { contains: trimmedTitle } : undefined,
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

export function SubscriptionPlansTable({
    search,
    navigate,
    onEdit,
    onDelete,
    onPreview,
    onTotalItemsChange,
}: SubscriptionPlansTableProps) {
    const [columnVisibility, setColumnVisibility] = useState<VisibilityState>(
        {}
    );
    const [titleFilter, setTitleFilter] = useState(() =>
        toStringValue(search.title)
    );
    const [debouncedTitleFilter, setDebouncedTitleFilter] = useState(() =>
        toStringValue(search.title)
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

    useEffect(() => {
        setTitleFilter(toStringValue(search.title));
    }, [search.title]);

    useEffect(() => {
        const timeout = window.setTimeout(() => {
            setDebouncedTitleFilter(titleFilter);
        }, 300);

        return () => window.clearTimeout(timeout);
    }, [titleFilter]);

    useEffect(() => {
        const trimmedValue = titleFilter.trim();
        const currentValue = toStringValue(search.title).trim();

        if (trimmedValue === currentValue) return;

        const timeout = window.setTimeout(() => {
            navigate({
                search: (prev) => ({
                    ...prev,
                    page: undefined,
                    title: trimmedValue || undefined,
                }),
                replace: true,
            });
        }, 300);

        return () => window.clearTimeout(timeout);
    }, [titleFilter, navigate, search.title]);

    const queryParams = useMemo(
        () =>
            buildQueryParams(
                debouncedTitleFilter,
                columnFilters,
                pagination.pageIndex,
                pagination.pageSize
            ),
        [
            columnFilters,
            debouncedTitleFilter,
            pagination.pageIndex,
            pagination.pageSize,
        ]
    );

    const { data, isLoading, isError } =
        useGetSubscriptionPlansAsAdmin(queryParams);

    const items = data?.items ?? [];
    const totalPages = data?.totalPages ?? 0;
    const totalItems = data?.totalItems ?? 0;
    const columns = buildSubscriptionPlansColumns({
        onEdit,
        onDelete,
        onPreview,
    });

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
            <SubscriptionPlansToolbar
                table={table}
                titleFilter={titleFilter}
                onTitleFilterChange={setTitleFilter}
                onReset={() => {
                    setTitleFilter("");
                    setDebouncedTitleFilter("");
                    table.resetColumnFilters();
                    navigate({
                        search: (prev) => ({
                            ...prev,
                            page: undefined,
                            title: undefined,
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
                                    Loading plans...
                                </TableCell>
                            </TableRow>
                        ) : isError ? (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length}
                                    className="h-24 text-center text-destructive"
                                >
                                    Failed to load plans.
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
                                    No subscription plans found.
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
