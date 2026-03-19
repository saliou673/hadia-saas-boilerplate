import { type ColumnDef } from "@tanstack/react-table";
import { Badge } from "@/components/ui/badge";
import { LongText } from "@/components/long-text";
import { type RoleGroupRow } from "../data/schema";
import { DataTableRowActions } from "./data-table-row-actions";

type BuildRoleGroupsColumnsOptions = {
    canManageRoleGroups: boolean;
};

export function buildRoleGroupsColumns({
    canManageRoleGroups,
}: BuildRoleGroupsColumnsOptions): ColumnDef<RoleGroupRow>[] {
    const columns: ColumnDef<RoleGroupRow>[] = [
        {
            accessorKey: "name",
            header: "Name",
            cell: ({ row }) => (
                <LongText className="max-w-40">{row.original.name}</LongText>
            ),
            enableHiding: false,
        },
        {
            accessorKey: "description",
            header: "Description",
            cell: ({ row }) => (
                <LongText className="max-w-64">
                    {row.original.description ?? "—"}
                </LongText>
            ),
        },
        {
            accessorKey: "permissionCodes",
            header: "Permissions",
            cell: ({ row }) => {
                const codes = row.original.permissionCodes;
                const maxVisible = 3;
                const visible = codes.slice(0, maxVisible);
                const overflow = codes.length - maxVisible;

                return (
                    <div className="flex flex-wrap gap-1">
                        {visible.map((code) => (
                            <Badge
                                key={code}
                                variant="secondary"
                                className="font-mono text-xs"
                            >
                                {code}
                            </Badge>
                        ))}
                        {overflow > 0 && (
                            <Badge variant="outline" className="text-xs">
                                +{overflow}
                            </Badge>
                        )}
                    </div>
                );
            },
            enableSorting: false,
        },
    ];

    if (canManageRoleGroups) {
        columns.push({
            id: "actions",
            cell: ({ row }) => (
                <DataTableRowActions
                    row={row}
                    canManageRoleGroups={canManageRoleGroups}
                />
            ),
        });
    }

    return columns;
}
