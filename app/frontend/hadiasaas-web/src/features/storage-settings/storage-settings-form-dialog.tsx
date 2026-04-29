"use client";

import { useEffect } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import {
    type StorageSettings,
    type CreateStorageSettingsRequestProviderEnumKey,
    useCreateStorageSettingsAsAdmin,
    useUpdateStorageSettingsAsAdmin,
    getStorageSettingsAsAdminQueryKey,
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
import { SelectDropdown } from "@/components/select-dropdown";
import { providerOptions } from "./data";

const formSchema = z.object({
    provider: z.string().min(1, "Provider is required."),
    bucketName: z.string().optional(),
    region: z.string().optional(),
    endpoint: z.string().optional(),
    active: z.boolean(),
});

type StorageSettingsForm = z.infer<typeof formSchema>;

type StorageSettingsFormDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow?: StorageSettings | null;
};

export function StorageSettingsFormDialog({
    open,
    onOpenChange,
    currentRow,
}: StorageSettingsFormDialogProps) {
    const isUpdate = !!currentRow;
    const queryClient = useQueryClient();

    const form = useForm<StorageSettingsForm>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            provider: currentRow?.provider ?? "",
            bucketName: currentRow?.bucketName ?? "",
            region: currentRow?.region ?? "",
            endpoint: currentRow?.endpoint ?? "",
            active: currentRow?.active ?? true,
        },
    });

    useEffect(() => {
        form.reset({
            provider: currentRow?.provider ?? "",
            bucketName: currentRow?.bucketName ?? "",
            region: currentRow?.region ?? "",
            endpoint: currentRow?.endpoint ?? "",
            active: currentRow?.active ?? true,
        });
    }, [currentRow, form, open]);

    const invalidateStorageSettings = async () => {
        await queryClient.invalidateQueries({
            queryKey: getStorageSettingsAsAdminQueryKey(),
        });
    };

    const createMutation = useCreateStorageSettingsAsAdmin({
        mutation: {
            onSuccess: async () => {
                await invalidateStorageSettings();
                toast.success("Storage settings created.");
                onOpenChange(false);
                form.reset();
            },
            onError: handleServerError,
        },
    });

    const updateMutation = useUpdateStorageSettingsAsAdmin({
        mutation: {
            onSuccess: async () => {
                await invalidateStorageSettings();
                toast.success("Storage settings updated.");
                onOpenChange(false);
                form.reset();
            },
            onError: handleServerError,
        },
    });

    const isPending = createMutation.isPending || updateMutation.isPending;

    const onSubmit = (values: StorageSettingsForm) => {
        const bucketName = values.bucketName?.trim() || undefined;
        const region = values.region?.trim() || undefined;
        const endpoint = values.endpoint?.trim() || undefined;

        if (isUpdate && currentRow?.id) {
            updateMutation.mutate({
                id: currentRow.id,
                data: {
                    provider:
                        values.provider as CreateStorageSettingsRequestProviderEnumKey,
                    bucketName,
                    region,
                    endpoint,
                    active: values.active,
                },
            });
            return;
        }

        createMutation.mutate({
            data: {
                provider:
                    values.provider as CreateStorageSettingsRequestProviderEnumKey,
                bucketName,
                region,
                endpoint,
                active: values.active,
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
                        {isUpdate ? "Edit" : "Create"} Storage Settings
                    </DialogTitle>
                    <DialogDescription>
                        {isUpdate
                            ? "Update the storage settings entry."
                            : "Configure a new file storage provider."}
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form
                        id="storage-settings-form"
                        onSubmit={form.handleSubmit(onSubmit)}
                        className="max-h-[60vh] space-y-6 overflow-y-auto px-1"
                    >
                        <FormField
                            control={form.control}
                            name="provider"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Provider</FormLabel>
                                    <SelectDropdown
                                        defaultValue={field.value}
                                        onValueChange={field.onChange}
                                        placeholder="Select a provider"
                                        items={providerOptions}
                                        disabled={isUpdate || isPending}
                                        isControlled
                                    />
                                    {isUpdate && (
                                        <FormDescription>
                                            Provider cannot be changed after
                                            creation.
                                        </FormDescription>
                                    )}
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="bucketName"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>
                                        Bucket / Container Name
                                    </FormLabel>
                                    <FormControl>
                                        <Input
                                            {...field}
                                            value={field.value ?? ""}
                                            disabled={isPending}
                                            placeholder="my-bucket"
                                        />
                                    </FormControl>
                                    <FormDescription>
                                        Required for cloud providers (S3, Azure,
                                        GCS).
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="region"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Region</FormLabel>
                                    <FormControl>
                                        <Input
                                            {...field}
                                            value={field.value ?? ""}
                                            disabled={isPending}
                                            placeholder="eu-west-1"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="endpoint"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Custom Endpoint</FormLabel>
                                    <FormControl>
                                        <Input
                                            {...field}
                                            value={field.value ?? ""}
                                            disabled={isPending}
                                            placeholder="https://s3.example.com"
                                        />
                                    </FormControl>
                                    <FormDescription>
                                        Optional. Use for S3-compatible
                                        services.
                                    </FormDescription>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="active"
                            render={({ field }) => (
                                <FormItem className="flex items-center justify-between rounded-lg border p-4">
                                    <div className="space-y-1">
                                        <FormLabel>Active</FormLabel>
                                        <FormDescription>
                                            Only one storage provider can be
                                            active at a time.
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
                    </form>
                </Form>
                <DialogFooter className="gap-2">
                    <DialogClose asChild>
                        <Button variant="outline" disabled={isPending}>
                            Close
                        </Button>
                    </DialogClose>
                    <Button
                        form="storage-settings-form"
                        type="submit"
                        disabled={isPending}
                    >
                        {isUpdate ? "Save changes" : "Create"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
