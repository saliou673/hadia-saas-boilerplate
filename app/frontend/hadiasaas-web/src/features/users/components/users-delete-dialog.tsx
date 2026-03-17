"use client";

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useDeleteUserAsAdmin } from "@api-client";
import { AlertTriangle } from "lucide-react";
import { toast } from "sonner";
import { handleServerError } from "@/lib/handle-server-error";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { getUsersAsAdminQueryKey } from "../../../../../hadiasaas-apiclient";
import { type UserRow } from "../data/schema";

type UserDeleteDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow: UserRow;
};

export function UsersDeleteDialog({
    open,
    onOpenChange,
    currentRow,
}: UserDeleteDialogProps) {
    const [value, setValue] = useState("");
    const queryClient = useQueryClient();
    const { mutate, isPending } = useDeleteUserAsAdmin({
        mutation: {
            onSuccess: async () => {
                await queryClient.invalidateQueries({
                    queryKey: getUsersAsAdminQueryKey(),
                });
                toast.success("User deleted.");
                setValue("");
                onOpenChange(false);
            },
            onError: handleServerError,
        },
    });

    const handleDelete = () => {
        if (value.trim() !== currentRow.email) {
            return;
        }

        mutate({ id: currentRow.id });
    };

    return (
        <ConfirmDialog
            open={open}
            onOpenChange={onOpenChange}
            handleConfirm={handleDelete}
            disabled={value.trim() !== currentRow.email}
            isLoading={isPending}
            title={
                <span className="text-destructive">
                    <AlertTriangle
                        className="me-1 inline-block stroke-destructive"
                        size={18}
                    />{" "}
                    Delete User
                </span>
            }
            desc={
                <div className="space-y-4">
                    <p className="mb-2">
                        Are you sure you want to delete{" "}
                        <span className="font-bold">{currentRow.email}</span>
                        ?
                        <br />
                        This action will permanently remove this managed user
                        from the system. This cannot be undone.
                    </p>

                    <Label className="my-2">
                        User email:
                        <Input
                            value={value}
                            onChange={(e) => setValue(e.target.value)}
                            placeholder="Enter the user email to confirm deletion."
                        />
                    </Label>

                    <Alert variant="destructive">
                        <AlertTitle>Warning!</AlertTitle>
                        <AlertDescription>
                            Please be careful, this operation can not be rolled
                            back.
                        </AlertDescription>
                    </Alert>
                </div>
            }
            confirmText="Delete"
            destructive
        />
    );
}
