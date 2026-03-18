"use client";

import { useEffect } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import {
    type AppConfiguration,
    type CreateAppConfigurationRequestCategoryEnumKey,
    useCreateAppConfigurationAsAdmin,
    useGetCategoriesAsAdmin,
    useUpdateAppConfigurationAsAdmin,
} from "@api-client";
import { toast } from "sonner";
import { handleServerError } from "@/lib/handle-server-error";
import { Button } from "@/components/ui/button";
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
import {
    Sheet,
    SheetClose,
    SheetContent,
    SheetDescription,
    SheetFooter,
    SheetHeader,
    SheetTitle,
} from "@/components/ui/sheet";
import { Switch } from "@/components/ui/switch";
import { Textarea } from "@/components/ui/textarea";
import { SelectDropdown } from "@/components/select-dropdown";
import { getAppConfigurationsAsAdminQueryKey } from "../../../../hadiasaas-apiclient";

const formSchema = z.object({
    category: z.string().min(1, "Category is required."),
    code: z
        .string()
        .trim()
        .min(1, "Code is required.")
        .max(50, "Code must be 50 characters or fewer."),
    label: z.string().trim().min(1, "Label is required."),
    description: z.string().optional(),
    active: z.boolean(),
});

type ConfigurationForm = z.infer<typeof formSchema>;

type ConfigurationsMutateDrawerProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow?: AppConfiguration | null;
};

export function ConfigurationsMutateDrawer({
    open,
    onOpenChange,
    currentRow,
}: ConfigurationsMutateDrawerProps) {
    const isUpdate = !!currentRow;
    const queryClient = useQueryClient();
    const { data: categoriesData } = useGetCategoriesAsAdmin();
    const categoryOptions = (categoriesData ?? []).map(
        ({ value, description }) => ({
            label: description ?? value ?? "",
            value: value ?? "",
        })
    );
    const form = useForm<ConfigurationForm>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            category: currentRow?.category ?? "",
            code: currentRow?.code ?? "",
            label: currentRow?.label ?? "",
            description: currentRow?.description ?? "",
            active: currentRow?.active ?? true,
        },
    });

    useEffect(() => {
        form.reset({
            category: currentRow?.category ?? "",
            code: currentRow?.code ?? "",
            label: currentRow?.label ?? "",
            description: currentRow?.description ?? "",
            active: currentRow?.active ?? true,
        });
    }, [currentRow, form, open]);

    const invalidateConfigurations = async () => {
        await queryClient.invalidateQueries({
            queryKey: getAppConfigurationsAsAdminQueryKey(),
        });
    };

    const createMutation = useCreateAppConfigurationAsAdmin({
        mutation: {
            onSuccess: async () => {
                await invalidateConfigurations();
                toast.success("Configuration created.");
                onOpenChange(false);
                form.reset();
            },
            onError: handleServerError,
        },
    });

    const updateMutation = useUpdateAppConfigurationAsAdmin({
        mutation: {
            onSuccess: async () => {
                await invalidateConfigurations();
                toast.success("Configuration updated.");
                onOpenChange(false);
                form.reset();
            },
            onError: handleServerError,
        },
    });

    const isPending = createMutation.isPending || updateMutation.isPending;

    const onSubmit = (values: ConfigurationForm) => {
        const description = values.description?.trim() || undefined;

        if (isUpdate && currentRow?.id) {
            updateMutation.mutate({
                id: currentRow.id,
                data: {
                    code: values.code.trim(),
                    label: values.label.trim(),
                    description,
                    active: values.active,
                },
            });
            return;
        }

        createMutation.mutate({
            data: {
                category:
                    values.category as CreateAppConfigurationRequestCategoryEnumKey,
                code: values.code.trim(),
                label: values.label.trim(),
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
        <Sheet open={open} onOpenChange={handleOpenChange}>
            <SheetContent className="flex flex-col">
                <SheetHeader className="text-start">
                    <SheetTitle>
                        {isUpdate ? "Edit" : "Create"} Configuration
                    </SheetTitle>
                    <SheetDescription>
                        {isUpdate
                            ? "Update the configuration entry."
                            : "Create a new configuration entry."}
                    </SheetDescription>
                </SheetHeader>
                <Form {...form}>
                    <form
                        id="configurations-form"
                        onSubmit={form.handleSubmit(onSubmit)}
                        className="flex-1 space-y-6 overflow-y-auto px-4"
                    >
                        <FormField
                            control={form.control}
                            name="category"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Category</FormLabel>
                                    <SelectDropdown
                                        defaultValue={field.value}
                                        onValueChange={field.onChange}
                                        placeholder="Select a category"
                                        items={categoryOptions}
                                        disabled={isUpdate || isPending}
                                        isControlled
                                    />
                                    {isUpdate && (
                                        <FormDescription>
                                            Category cannot be changed after
                                            creation.
                                        </FormDescription>
                                    )}
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
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
                                            placeholder="USD"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="label"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Label</FormLabel>
                                    <FormControl>
                                        <Input
                                            {...field}
                                            disabled={isPending}
                                            placeholder="US Dollar"
                                        />
                                    </FormControl>
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
                                                Toggle whether this
                                                configuration is currently
                                                active.
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
                                New configurations are created as active by
                                default. You can change the status afterward.
                            </div>
                        )}
                    </form>
                </Form>
                <SheetFooter className="gap-2">
                    <SheetClose asChild>
                        <Button variant="outline" disabled={isPending}>
                            Close
                        </Button>
                    </SheetClose>
                    <Button
                        form="configurations-form"
                        type="submit"
                        disabled={isPending}
                    >
                        {isUpdate ? "Save changes" : "Create configuration"}
                    </Button>
                </SheetFooter>
            </SheetContent>
        </Sheet>
    );
}
