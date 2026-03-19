"use client";

import { useQueryClient } from "@tanstack/react-query";
import {
    getRoleGroupsAsAdminQueryKey,
    useDeleteRoleGroupAsAdmin,
} from "@api-client";
import { AlertTriangle } from "lucide-react";
import { toast } from "sonner";
import { handleServerError } from "@/lib/handle-server-error";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { type RoleGroupRow } from "../data/schema";

type RoleGroupDeleteDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow: RoleGroupRow;
};

export function RoleGroupsDeleteDialog({
    open,
    onOpenChange,
    currentRow,
}: RoleGroupDeleteDialogProps) {
    const queryClient = useQueryClient();
    const { mutate, isPending } = useDeleteRoleGroupAsAdmin({
        mutation: {
            onSuccess: async () => {
                await queryClient.invalidateQueries({
                    queryKey: getRoleGroupsAsAdminQueryKey(),
                });
                toast.success("Role group deleted.");
                onOpenChange(false);
            },
            onError: handleServerError,
        },
    });

    const handleDelete = () => {
        mutate({ id: currentRow.id });
    };

    return (
        <ConfirmDialog
            open={open}
            onOpenChange={onOpenChange}
            handleConfirm={handleDelete}
            isLoading={isPending}
            title={
                <span className="text-destructive">
                    <AlertTriangle
                        className="me-1 inline-block stroke-destructive"
                        size={18}
                    />{" "}
                    Delete Role Group
                </span>
            }
            desc={
                <p>
                    Are you sure you want to delete{" "}
                    <span className="font-bold">{currentRow.name}</span>? This
                    action cannot be undone. The deletion will fail if the role
                    group is still assigned to users.
                </p>
            }
            confirmText="Delete"
            destructive
        />
    );
}
