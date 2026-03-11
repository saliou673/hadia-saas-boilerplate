"use client";

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { type Table } from "@tanstack/react-table";
import { useDeleteUserAsAdmin } from "@api-client";
import { AlertTriangle } from "lucide-react";
import { toast } from "sonner";
import { handleServerError } from "@/lib/handle-server-error";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { type UserRow } from "../data/schema";

type UserMultiDeleteDialogProps<TData> = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    table: Table<TData>;
};

const CONFIRM_WORD = "DELETE";

export function UsersMultiDeleteDialog<TData>({
    open,
    onOpenChange,
    table,
}: UserMultiDeleteDialogProps<TData>) {
    const [value, setValue] = useState("");
    const queryClient = useQueryClient();
    const { mutateAsync, isPending } = useDeleteUserAsAdmin();

    const selectedRows = table.getFilteredSelectedRowModel().rows;

    const handleDelete = async () => {
        if (value.trim() !== CONFIRM_WORD) {
            toast.error(`Please type "${CONFIRM_WORD}" to confirm.`);
            return;
        }

        let deletedCount = 0;

        for (const row of selectedRows) {
            const user = row.original as UserRow;

            try {
                await mutateAsync({ id: user.id });
                deletedCount += 1;
            } catch (error) {
                handleServerError(error);
            }
        }

        if (deletedCount > 0) {
            await queryClient.invalidateQueries({
                queryKey: [{ url: "/api/v1/admin/users" }],
            });
        }

        setValue("");
        onOpenChange(false);

        if (deletedCount === selectedRows.length) {
            table.resetRowSelection();
            toast.success(
                `Deleted ${deletedCount} ${deletedCount > 1 ? "users" : "user"}.`
            );
            return;
        }

        toast.error(
            `Deleted ${deletedCount} of ${selectedRows.length} selected users.`
        );
    };

    return (
        <ConfirmDialog
            open={open}
            onOpenChange={onOpenChange}
            handleConfirm={handleDelete}
            disabled={value.trim() !== CONFIRM_WORD}
            isLoading={isPending}
            title={
                <span className="text-destructive">
                    <AlertTriangle
                        className="me-1 inline-block stroke-destructive"
                        size={18}
                    />{" "}
                    Delete {selectedRows.length}{" "}
                    {selectedRows.length > 1 ? "users" : "user"}
                </span>
            }
            desc={
                <div className="space-y-4">
                    <p className="mb-2">
                        Are you sure you want to delete the selected users?{" "}
                        <br />
                        This action cannot be undone.
                    </p>

                    <Label className="my-4 flex flex-col items-start gap-1.5">
                        <span className="">
                            Confirm by typing "{CONFIRM_WORD}":
                        </span>
                        <Input
                            value={value}
                            onChange={(e) => setValue(e.target.value)}
                            placeholder={`Type "${CONFIRM_WORD}" to confirm.`}
                        />
                    </Label>

                    <Alert variant="destructive">
                        <AlertTitle>Warning!</AlertTitle>
                        <AlertDescription>
                            Please be careful, this operation cannot be rolled
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
