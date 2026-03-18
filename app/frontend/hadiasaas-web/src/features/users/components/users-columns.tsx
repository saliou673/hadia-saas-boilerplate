import { type ColumnDef } from "@tanstack/react-table";
import { cn } from "@/lib/utils";
import { Badge } from "@/components/ui/badge";
import { Checkbox } from "@/components/ui/checkbox";
import { LongText } from "@/components/long-text";
import {
    genderLabels,
    userStatusBadgeClassNames,
    userStatusLabels,
} from "../data/data";
import { type UserRow } from "../data/schema";
import { DataTableRowActions } from "./data-table-row-actions";

type BuildUsersColumnsOptions = {
    canDeleteUsers: boolean;
    canUpdateUsers: boolean;
};

export function buildUsersColumns({
    canDeleteUsers,
    canUpdateUsers,
}: BuildUsersColumnsOptions): ColumnDef<UserRow>[] {
    const columns: ColumnDef<UserRow>[] = [];

    if (canDeleteUsers) {
        columns.push({
            id: "select",
            header: ({ table }) => (
                <Checkbox
                    checked={
                        table.getIsAllPageRowsSelected() ||
                        (table.getIsSomePageRowsSelected() && "indeterminate")
                    }
                    onCheckedChange={(value) =>
                        table.toggleAllPageRowsSelected(!!value)
                    }
                    aria-label="Select all"
                    className="translate-y-[2px]"
                />
            ),
            meta: {
                className: cn(
                    "max-md:sticky start-0 z-10 rounded-tl-[inherit]"
                ),
            },
            cell: ({ row }) => (
                <Checkbox
                    checked={row.getIsSelected()}
                    onCheckedChange={(value) => row.toggleSelected(!!value)}
                    aria-label="Select row"
                    className="translate-y-[2px]"
                />
            ),
            enableSorting: false,
            enableHiding: false,
        });
    }

    columns.push(
        {
            accessorKey: "fullName",
            header: "Name",
            cell: ({ row }) => (
                <LongText className="max-w-40 ps-3">
                    {row.original.fullName}
                </LongText>
            ),
            meta: {
                className: cn(
                    "drop-shadow-[0_1px_2px_rgb(0_0_0_/_0.1)] dark:drop-shadow-[0_1px_2px_rgb(255_255_255_/_0.1)]",
                    canDeleteUsers
                        ? "ps-0.5 max-md:sticky start-6 @4xl/content:table-cell @4xl/content:drop-shadow-none"
                        : "ps-0.5"
                ),
            },
            enableHiding: false,
        },
        {
            accessorKey: "email",
            header: "Email",
            cell: ({ row }) => (
                <LongText className="max-w-52">{row.original.email}</LongText>
            ),
            enableHiding: false,
        },
        {
            accessorKey: "phoneNumber",
            header: "Phone Number",
            cell: ({ row }) => <div>{row.original.phoneNumber ?? "—"}</div>,
            enableSorting: false,
        },
        {
            accessorKey: "roleGroupNames",
            header: "Role Groups",
            cell: ({ row }) => (
                <div className="flex flex-wrap gap-1">
                    {row.original.roleGroupNames.map((name) => (
                        <Badge key={name} variant="outline" className="text-xs">
                            {name}
                        </Badge>
                    ))}
                </div>
            ),
            filterFn: (row, _id, value: string[]) =>
                value.some((v) => row.original.roleGroupNames.includes(v)),
            enableSorting: false,
        },
        {
            accessorKey: "gender",
            header: "Gender",
            cell: ({ row }) => <div>{genderLabels[row.original.gender]}</div>,
            filterFn: (row, id, value) => value.includes(row.getValue(id)),
            enableSorting: false,
        },
        {
            accessorKey: "status",
            header: "Status",
            cell: ({ row }) => (
                <Badge
                    variant="outline"
                    className={cn(
                        userStatusBadgeClassNames[row.original.status]
                    )}
                >
                    {userStatusLabels[row.original.status]}
                </Badge>
            ),
            filterFn: (row, id, value) => value.includes(row.getValue(id)),
            enableHiding: false,
            enableSorting: false,
        }
    );

    if (canUpdateUsers || canDeleteUsers) {
        columns.push({
            id: "actions",
            cell: ({ row }) => (
                <DataTableRowActions
                    row={row}
                    canUpdateUsers={canUpdateUsers}
                    canDeleteUsers={canDeleteUsers}
                />
            ),
        });
    }

    return columns;
}
