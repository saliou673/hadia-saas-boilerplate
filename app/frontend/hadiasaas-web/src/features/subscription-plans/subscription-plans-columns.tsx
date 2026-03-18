import { DotsHorizontalIcon } from "@radix-ui/react-icons";
import { type ColumnDef } from "@tanstack/react-table";
import { type SubscriptionPlan } from "@api-client";
import { Eye, Pencil, Trash2 } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { DataTableColumnHeader } from "@/components/data-table";
import { LongText } from "@/components/long-text";

type BuildSubscriptionPlansColumnsParams = {
    onEdit: (plan: SubscriptionPlan) => void;
    onDelete: (plan: SubscriptionPlan) => void;
    onPreview: (plan: SubscriptionPlan) => void;
};

function formatPrices(plan: SubscriptionPlan): string {
    const parts: string[] = [];
    const currency = plan.currencyCode ?? "";

    if (plan.monthlyPrice != null) {
        parts.push(`${plan.monthlyPrice} ${currency}/mo`);
    }
    if (plan.yearlyPrice != null) {
        parts.push(`${plan.yearlyPrice} ${currency}/yr`);
    }
    if (plan.lifetimePrice != null) {
        parts.push(`${plan.lifetimePrice} ${currency} lifetime`);
    }
    if (plan.price != null && plan.durationDays != null) {
        parts.push(`${plan.price} ${currency}/${plan.durationDays}d`);
    }

    return parts.length > 0 ? parts.join(" · ") : "N/A";
}

export function buildSubscriptionPlansColumns({
    onEdit,
    onDelete,
    onPreview,
}: BuildSubscriptionPlansColumnsParams): ColumnDef<SubscriptionPlan>[] {
    return [
        {
            accessorKey: "title",
            header: ({ column }) => (
                <DataTableColumnHeader column={column} title="Title" />
            ),
            cell: ({ row }) => (
                <LongText className="max-w-48 font-medium">
                    {row.original.title ?? "N/A"}
                </LongText>
            ),
            enableHiding: false,
        },
        {
            id: "pricing",
            header: ({ column }) => (
                <DataTableColumnHeader column={column} title="Pricing" />
            ),
            cell: ({ row }) => (
                <LongText className="max-w-64 text-muted-foreground">
                    {formatPrices(row.original)}
                </LongText>
            ),
            enableSorting: false,
        },
        {
            accessorKey: "features",
            header: ({ column }) => (
                <DataTableColumnHeader column={column} title="Features" />
            ),
            cell: ({ row }) => {
                const features = row.original.features ?? [];
                return (
                    <span className="text-muted-foreground">
                        {features.length > 0
                            ? `${features.length} feature${features.length === 1 ? "" : "s"}`
                            : "None"}
                    </span>
                );
            },
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
                        <DropdownMenuItem
                            onClick={() => onPreview(row.original)}
                        >
                            Preview
                            <Eye className="ms-auto size-4" />
                        </DropdownMenuItem>
                        <DropdownMenuItem onClick={() => onEdit(row.original)}>
                            Edit
                            <Pencil className="ms-auto size-4" />
                        </DropdownMenuItem>
                        <DropdownMenuItem
                            onClick={() => onDelete(row.original)}
                            className="text-red-500!"
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
