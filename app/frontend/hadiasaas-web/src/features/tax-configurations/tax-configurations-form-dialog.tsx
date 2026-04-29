"use client";

import { useEffect } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import {
    type TaxConfiguration,
    useCreateTaxConfigurationAsAdmin,
    useUpdateTaxConfigurationAsAdmin,
    getTaxConfigurationsAsAdminQueryKey,
} from "@api-client";
import { toast } from "sonner";
import { handleServerError } from "@/lib/handle-server-error";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Switch } from "@/components/ui/switch";
import { Textarea } from "@/components/ui/textarea";

const formSchema = z.object({
    code: z
        .string()
        .trim()
        .min(1, "Code is required.")
        .max(50, "Code must be 50 characters or fewer."),
    name: z.string().trim().min(1, "Name is required."),
    ratePercent: z
        .number({
            required_error: "Rate is required.",
            invalid_type_error: "Rate must be a number.",
        })
        .min(0, "Rate must be at least 0%.")
        .max(100, "Rate must be at most 100%."),
    description: z.string().optional(),
    active: z.boolean(),
});

type TaxConfigurationForm = z.infer<typeof formSchema>;

type TaxConfigurationsFormDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow?: TaxConfiguration | null;
};

export function TaxConfigurationsFormDialog({
    open,
    onOpenChange,
    currentRow,
}: TaxConfigurationsFormDialogProps) {
    const isUpdate = !!currentRow;
    const queryClient = useQueryClient();

    const form = useForm<TaxConfigurationForm>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            code: currentRow?.code ?? "",
            name: currentRow?.name ?? "",
            ratePercent:
                currentRow?.rate != null
                    ? parseFloat((currentRow.rate * 100).toFixed(6))
                    : 0,
            description: currentRow?.description ?? "",
            active: currentRow?.active ?? true,
        },
    });

    useEffect(() => {
        form.reset({
            code: currentRow?.code ?? "",
            name: currentRow?.name ?? "",
            ratePercent:
                currentRow?.rate != null
                    ? parseFloat((currentRow.rate * 100).toFixed(6))
                    : 0,
            description: currentRow?.description ?? "",
            active: currentRow?.active ?? true,
        });
    }, [currentRow, form, open]);

    const invalidateTaxConfigurations = async () => {
        await queryClient.invalidateQueries({
            queryKey: getTaxConfigurationsAsAdminQueryKey(),
        });
    };

    const createMutation = useCreateTaxConfigurationAsAdmin({
        mutation: {
            onSuccess: async () => {
                await invalidateTaxConfigurations();
                toast.success("Tax configuration created.");
                onOpenChange(false);
                form.reset();
            },
            onError: handleServerError,
        },
    });

    const updateMutation = useUpdateTaxConfigurationAsAdmin({
        mutation: {
            onSuccess: async () => {
                await invalidateTaxConfigurations();
                toast.success("Tax configuration updated.");
                onOpenChange(false);
                form.reset();
            },
            onError: handleServerError,
        },
    });

    const isPending = createMutation.isPending || updateMutation.isPending;

    const onSubmit = (values: TaxConfigurationForm) => {
        const description = values.description?.trim() || undefined;
        const rate = values.ratePercent / 100;

        if (isUpdate && currentRow?.id) {
            updateMutation.mutate({
                id: currentRow.id,
                data: {
                    code: values.code.trim(),
                    name: values.name.trim(),
                    rate,
                    description,
                    active: values.active,
                },
            });
            return;
        }

        createMutation.mutate({
            data: {
                code: values.code.trim(),
                name: values.name.trim(),
                rate,
                description,
            },
        });
    };

    const handleOpenChange = (nextOpen: boolean) => {
        if (!isPending) {
            onOpenChange(nextOpen);
            if (!nextOpen) {
                form.reset();
            }
        }
    };

    return (
        <Dialog open={open} onOpenChange={handleOpenChange}>
            <DialogContent className="sm:max-w-lg">
                <DialogHeader>
                    <DialogTitle>
                        {isUpdate ? "Edit" : "Create"} Tax Configuration
                    </DialogTitle>
                    <DialogDescription>
                        {isUpdate
                            ? "Update the tax configuration entry."
                            : "Create a new tax configuration entry."}
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form
                        id="tax-configurations-form"
                        onSubmit={form.handleSubmit(onSubmit)}
                        className="max-h-[60vh] space-y-6 overflow-y-auto px-1"
                    >
                        <FormField
                            control={form.control}
                            name="code"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Code</FormLabel>
                                    <FormControl>
                                        <Input
                                            {...field}
                                            disabled={isPending}
                                            placeholder="VAT_20"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="name"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Name</FormLabel>
                                    <FormControl>
                                        <Input
                                            {...field}
                                            disabled={isPending}
                                            placeholder="Standard VAT"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="ratePercent"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Rate (%)</FormLabel>
                                    <FormControl>
                                        <Input
                                            type="number"
                                            step="0.01"
                                            min={0}
                                            max={100}
                                            value={field.value}
                                            onChange={(e) =>
                                                field.onChange(
                                                    parseFloat(
                                                        e.target.value
                                                    ) || 0
                                                )
                                            }
                                            disabled={isPending}
                                            placeholder="20"
                                        />
                                    </FormControl>
                                    <FormDescription>
                                        Enter the rate as a percentage (e.g. 20
                                        for 20%).
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="description"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Description</FormLabel>
                                    <FormControl>
                                        <Textarea
                                            {...field}
                                            value={field.value ?? ""}
                                            disabled={isPending}
                                            placeholder="Optional description"
                                            className="min-h-24"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        {isUpdate && (
                            <FormField
                                control={form.control}
                                name="active"
                                render={({ field }) => (
                                    <FormItem className="flex items-center justify-between rounded-lg border p-4">
                                        <div className="space-y-1">
                                            <FormLabel>Active</FormLabel>
                                            <FormDescription>
                                                Toggle whether this tax rate is
                                                currently active.
                                            </FormDescription>
                                        </div>
                                        <FormControl>
                                            <Switch
                                                checked={field.value}
                                                onCheckedChange={field.onChange}
                                                disabled={isPending}
                                            />
                                        </FormControl>
                                    </FormItem>
                                )}
                            />
                        )}
                        {!isUpdate && (
                            <div className="rounded-lg border border-dashed p-4 text-sm text-muted-foreground">
                                New tax configurations are created as active by
                                default. You can change the status afterward.
                            </div>
                        )}
                    </form>
                </Form>
                <DialogFooter className="gap-2">
                    <DialogClose asChild>
                        <Button variant="outline" disabled={isPending}>
                            Close
                        </Button>
                    </DialogClose>
                    <Button
                        form="tax-configurations-form"
                        type="submit"
                        disabled={isPending}
                    >
                        {isUpdate ? "Save changes" : "Create tax rate"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
