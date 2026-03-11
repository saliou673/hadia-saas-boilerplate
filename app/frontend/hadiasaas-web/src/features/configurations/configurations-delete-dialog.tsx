"use client";

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { type AppConfiguration, useDelete } from "@api-client";
import { AlertTriangle } from "lucide-react";
import { handleServerError } from "@/lib/handle-server-error";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ConfirmDialog } from "@/components/confirm-dialog";

type ConfigurationsDeleteDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow: AppConfiguration | null;
    onDeleted: () => void;
};

export function ConfigurationsDeleteDialog({
    open,
    onOpenChange,
    currentRow,
    onDeleted,
}: ConfigurationsDeleteDialogProps) {
    const [value, setValue] = useState("");
    const queryClient = useQueryClient();
    const deleteConfiguration = useDelete({
        mutation: {
            onSuccess: async () => {
                await queryClient.invalidateQueries({
                    queryKey: ["admin-configurations"],
                });

                setValue("");
                onOpenChange(false);
                onDeleted();
            },
            onError: handleServerError,
        },
    });

    const expectedCode = currentRow?.code?.trim() ?? "";
    const isConfirmed = value.trim() === expectedCode;

    const handleDelete = () => {
        if (!currentRow?.id || !isConfirmed) {
            return;
        }

        deleteConfiguration.mutate({ id: currentRow.id });
    };

    return (
        <ConfirmDialog
            open={open}
            onOpenChange={(nextOpen) => {
                if (!deleteConfiguration.isPending) {
                    onOpenChange(nextOpen);
                    if (!nextOpen) {
                        setValue("");
                    }
                }
            }}
            handleConfirm={handleDelete}
            confirmForm="configuration-delete-form"
            confirmButtonType="submit"
            disabled={!isConfirmed}
            isLoading={deleteConfiguration.isPending}
            title={
                <span className="text-destructive">
                    <AlertTriangle
                        className="me-1 inline-block stroke-destructive"
                        size={18}
                    />{" "}
                    Delete Configuration
                </span>
            }
            desc={
                <form
                    id="configuration-delete-form"
                    className="space-y-4"
                    onSubmit={(event) => {
                        event.preventDefault();
                        handleDelete();
                    }}
                >
                    <p className="mb-2">
                        Delete configuration{" "}
                        <span className="font-bold">{currentRow?.code}</span> in{" "}
                        <span className="font-bold">
                            {currentRow?.category}
                        </span>
                        ?
                        <br />
                        This action permanently removes the configuration entry
                        from the system.
                    </p>

                    <Label className="my-2">
                        Enter configuration code to confirm:
                        <Input
                            value={value}
                            onChange={(event) => setValue(event.target.value)}
                            placeholder="Enter code"
                        />
                    </Label>

                    <Alert variant="destructive">
                        <AlertTitle>Warning!</AlertTitle>
                        <AlertDescription>
                            Backend rules still apply. If this configuration is
                            referenced elsewhere, deletion can fail.
                        </AlertDescription>
                    </Alert>
                </form>
            }
            confirmText="Delete"
            destructive
        />
    );
}
