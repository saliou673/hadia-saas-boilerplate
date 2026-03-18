import { format } from "date-fns";
import { DotsHorizontalIcon } from "@radix-ui/react-icons";
import { type ColumnDef } from "@tanstack/react-table";
import { type StorageSettings } from "@api-client";
import { Pencil, Trash2 } from "lucide-react";
import { cn } from "@/lib/utils";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { DataTableColumnHeader } from "@/components/data-table";
import { formatProvider } from "./data";

type BuildStorageSettingsColumnsParams = {
    onEdit: (storageSettings: StorageSettings) => void;
    onDelete: (storageSettings: StorageSettings) => void;
};

function formatDate(value?: string) {
    if (!value) return "N/A";
    return format(new Date(value), "PPp");
}

export function buildStorageSettingsColumns({
    onEdit,
    onDelete,
}: BuildStorageSettingsColumnsParams): ColumnDef<StorageSettings>[] {
    return [
        {
            accessorKey: "provider",
            header: ({ column }) => (
                <DataTableColumnHeader column={column} title="Provider" />
            ),
            cell: ({ row }) => (
                <Badge variant="outline">
                    {formatProvider(row.original.provider)}
                </Badge>
            ),
            enableSorting: false,
            enableHiding: false,
        },
        {
            accessorKey: "bucketName",
            header: ({ column }) => (
                <DataTableColumnHeader
                    column={column}
                    title="Bucket / Container"
                />
            ),
            cell: ({ row }) => row.original.bucketName ?? "—",
            enableSorting: false,
        },
        {
            accessorKey: "region",
            header: ({ column }) => (
                <DataTableColumnHeader column={column} title="Region" />
            ),
            cell: ({ row }) => row.original.region ?? "—",
            enableSorting: false,
        },
        {
            accessorKey: "active",
            header: ({ column }) => (
                <DataTableColumnHeader column={column} title="Status" />
            ),
            cell: ({ row }) => (
                <Badge variant={row.original.active ? "default" : "secondary"}>
                    {row.original.active ? "Active" : "Inactive"}
                </Badge>
            ),
            enableSorting: false,
        },
        {
            accessorKey: "lastUpdatedBy",
            header: ({ column }) => (
                <DataTableColumnHeader column={column} title="Updated By" />
            ),
            cell: ({ row }) => row.original.lastUpdatedBy ?? "N/A",
            enableSorting: false,
        },
        {
            accessorKey: "lastUpdateDate",
            header: ({ column }) => (
                <DataTableColumnHeader column={column} title="Updated At" />
            ),
            cell: ({ row }) => formatDate(row.original.lastUpdateDate),
            enableSorting: false,
        },
        {
            id: "actions",
            cell: ({ row }) => (
                <DropdownMenu modal={false}>
                    <DropdownMenuTrigger asChild>
                        <Button
                            variant="ghost"
                            className="flex h-8 w-8 p-0 data-[state=open]:bg-muted"
                        >
                            <DotsHorizontalIcon className="h-4 w-4" />
                            <span className="sr-only">Open menu</span>
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end" className="w-[160px]">
                        <DropdownMenuItem onClick={() => onEdit(row.original)}>
                            Edit
                            <Pencil className="ms-auto size-4" />
                        </DropdownMenuItem>
                        <DropdownMenuItem
                            onClick={() => onDelete(row.original)}
                            className={cn("text-red-500!")}
                        >
                            Delete
                            <Trash2 className="ms-auto size-4" />
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            ),
        },
    ];
}
