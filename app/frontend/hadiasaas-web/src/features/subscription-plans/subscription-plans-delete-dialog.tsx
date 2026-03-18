"use client";

import React, { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import {
    type SubscriptionPlan,
    useDeleteSubscriptionPlanAsAdmin,
} from "@api-client";
import { AlertTriangle } from "lucide-react";
import { handleServerError } from "@/lib/handle-server-error";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { getSubscriptionPlansAsAdminQueryKey } from "../../../../hadiasaas-apiclient";

type SubscriptionPlansDeleteDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow: SubscriptionPlan | null;
    onDeleted: () => void;
};

export function SubscriptionPlansDeleteDialog({
    open,
    onOpenChange,
    currentRow,
    onDeleted,
}: SubscriptionPlansDeleteDialogProps) {
    const [value, setValue] = useState("");
    const queryClient = useQueryClient();

    const { mutate: deletePlan, isPending: isDeleting } =
        useDeleteSubscriptionPlanAsAdmin({
            mutation: {
                onSuccess: async () => {
                    await queryClient.invalidateQueries({
                        queryKey: getSubscriptionPlansAsAdminQueryKey(),
                    });
                    setValue("");
                    onOpenChange(false);
                    onDeleted();
                },
                onError: handleServerError,
            },
        });

    const expectedTitle = currentRow?.title?.trim() ?? "";
    const isConfirmed = value.trim() === expectedTitle;

    const handleDelete = () => {
        if (!currentRow?.id || !isConfirmed) return;
        deletePlan({ id: currentRow.id });
    };

    const handleOpenChange = (nextOpen: boolean) => {
        if (!isDeleting) {
            onOpenChange(nextOpen);
            if (!nextOpen) {
                setValue("");
            }
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
            confirmForm="subscription-plan-delete-form"
            confirmButtonType="submit"
            disabled={!isConfirmed}
            isLoading={isDeleting}
            title={
                <span className="text-destructive">
                    <AlertTriangle
                        className="me-1 inline-block stroke-destructive"
                        size={18}
                    />{" "}
                    Delete Subscription Plan
                </span>
            }
            desc={
                <form
                    id="subscription-plan-delete-form"
                    className="space-y-4"
                    onSubmit={handleSubmit}
                >
                    <p className="mb-2">
                        Delete plan{" "}
                        <span className="font-bold">{currentRow?.title}</span>?
                        <br />
                        This action permanently removes the plan from the
                        system.
                    </p>

                    <Label className="my-2">
                        Enter plan title to confirm:
                        <Input
                            value={value}
                            onChange={(event) => setValue(event.target.value)}
                            placeholder="Enter title"
                        />
                    </Label>

                    <Alert variant="destructive">
                        <AlertTitle>Warning!</AlertTitle>
                        <AlertDescription>
                            This action cannot be undone.
                        </AlertDescription>
                    </Alert>
                </form>
            }
            confirmText="Delete"
            destructive
        />
    );
}
