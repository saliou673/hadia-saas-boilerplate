"use client";

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import {
    type StorageSettings,
    useDeleteStorageSettings,
    getStorageSettingsAsAdminQueryKey,
} from "@api-client";
import { AlertTriangle } from "lucide-react";
import { handleServerError } from "@/lib/handle-server-error";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { formatProvider } from "./data";

type StorageSettingsDeleteDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow: StorageSettings | null;
    onDeleted: () => void;
};

export function StorageSettingsDeleteDialog({
    open,
    onOpenChange,
    currentRow,
    onDeleted,
}: StorageSettingsDeleteDialogProps) {
    const [value, setValue] = useState("");
    const queryClient = useQueryClient();
    const { mutate: deleteStorageSettings, isPending: isDeleting } =
        useDeleteStorageSettings({
            mutation: {
                onSuccess: async () => {
                    await queryClient.invalidateQueries({
                        queryKey: getStorageSettingsAsAdminQueryKey(),
                    });
                    setValue("");
                    onOpenChange(false);
                    onDeleted();
                },
                onError: handleServerError,
            },
        });

    const expectedValue = currentRow?.provider?.trim() ?? "";
    const isConfirmed = value.trim() === expectedValue;

    const handleDelete = () => {
        if (!currentRow?.id || !isConfirmed) return;
        deleteStorageSettings({ id: currentRow.id });
    };

    const handleOpenChange = (nextOpen: boolean) => {
        if (!isDeleting) {
            onOpenChange(nextOpen);
            if (!nextOpen) setValue("");
        }
    };

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        handleDelete();
    };

    return (
        <ConfirmDialog
            open={open}
            onOpenChange={handleOpenChange}
            handleConfirm={handleDelete}
            confirmForm="storage-settings-delete-form"
            confirmButtonType="submit"
            disabled={!isConfirmed}
            isLoading={isDeleting}
            title={
                <span className="text-destructive">
                    <AlertTriangle
                        className="me-1 inline-block stroke-destructive"
                        size={18}
                    />{" "}
                    Delete Storage Settings
                </span>
            }
            desc={
                <form
                    id="storage-settings-delete-form"
                    className="space-y-4"
                    onSubmit={handleSubmit}
                >
                    <p className="mb-2">
                        Delete{" "}
                        <span className="font-bold">
                            {formatProvider(currentRow?.provider)}
                        </span>{" "}
                        storage settings?
                        <br />
                        This action permanently removes this storage
                        configuration.
                    </p>

                    <Label className="my-2">
                        Enter the provider key to confirm (
                        <span className="font-mono">{expectedValue}</span>):
                        <Input
                            value={value}
                            onChange={(event) => setValue(event.target.value)}
                            placeholder="Enter provider key"
                        />
                    </Label>
                </form>
            }
            confirmText="Delete"
            destructive
        />
    );
}
