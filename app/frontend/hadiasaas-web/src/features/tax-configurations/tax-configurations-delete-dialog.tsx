"use client";

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import {
    type TaxConfiguration,
    useDeleteTaxConfiguration,
    getTaxConfigurationsAsAdminQueryKey,
} from "@api-client";
import { AlertTriangle } from "lucide-react";
import { handleServerError } from "@/lib/handle-server-error";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ConfirmDialog } from "@/components/confirm-dialog";

type TaxConfigurationsDeleteDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow: TaxConfiguration | null;
    onDeleted: () => void;
};

export function TaxConfigurationsDeleteDialog({
    open,
    onOpenChange,
    currentRow,
    onDeleted,
}: TaxConfigurationsDeleteDialogProps) {
    const [value, setValue] = useState("");
    const queryClient = useQueryClient();
    const { mutate: deleteTaxConfiguration, isPending: isDeleting } =
        useDeleteTaxConfiguration({
            mutation: {
                onSuccess: async () => {
                    await queryClient.invalidateQueries({
                        queryKey: getTaxConfigurationsAsAdminQueryKey(),
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
        if (!currentRow?.id || !isConfirmed) return;
        deleteTaxConfiguration({ id: currentRow.id });
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
            confirmForm="tax-configuration-delete-form"
            confirmButtonType="submit"
            disabled={!isConfirmed}
            isLoading={isDeleting}
            title={
                <span className="text-destructive">
                    <AlertTriangle
                        className="me-1 inline-block stroke-destructive"
                        size={18}
                    />{" "}
                    Delete Tax Configuration
                </span>
            }
            desc={
                <form
                    id="tax-configuration-delete-form"
                    className="space-y-4"
                    onSubmit={handleSubmit}
                >
                    <p className="mb-2">
                        Delete tax configuration{" "}
                        <span className="font-bold">{currentRow?.code}</span>?
                        <br />
                        This action permanently removes the tax configuration
                        entry from the system.
                    </p>

                    <Label className="my-2">
                        Enter configuration code to confirm:
                        <Input
                            value={value}
                            onChange={(event) => setValue(event.target.value)}
                            placeholder="Enter code"
                        />
                    </Label>
                </form>
            }
            confirmText="Delete"
            destructive
        />
    );
}
