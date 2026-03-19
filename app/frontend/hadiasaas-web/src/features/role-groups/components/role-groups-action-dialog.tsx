"use client";

import { useEffect, useMemo } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import {
    getRoleGroupsAsAdminQueryKey,
    useCreateRoleGroupAsAdmin,
    useGetPermissionsAsAdmin,
    useUpdateRoleGroupAsAdmin,
} from "@api-client";
import { toast } from "sonner";
import { handleServerError } from "@/lib/handle-server-error";
import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import {
    Dialog,
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
import { ScrollArea } from "@/components/ui/scroll-area";
import { Textarea } from "@/components/ui/textarea";
import { type RoleGroupRow } from "../data/schema";

const formSchema = z.object({
    name: z.string().trim().min(1, "Name is required.").max(100),
    description: z.string().optional(),
    permissionCodes: z
        .array(z.string())
        .min(1, "Select at least one permission."),
});

type RoleGroupForm = z.infer<typeof formSchema>;

type RoleGroupActionDialogProps = {
    currentRow?: RoleGroupRow;
    open: boolean;
    onOpenChange: (open: boolean) => void;
};

function getDefaultValues(currentRow?: RoleGroupRow): RoleGroupForm {
    return {
        name: currentRow?.name ?? "",
        description: currentRow?.description ?? "",
        permissionCodes: currentRow?.permissionCodes ?? [],
    };
}

export function RoleGroupsActionDialog({
    currentRow,
    open,
    onOpenChange,
}: RoleGroupActionDialogProps) {
    const isEdit = !!currentRow;
    const queryClient = useQueryClient();

    const form = useForm<RoleGroupForm>({
        resolver: zodResolver(formSchema),
        defaultValues: getDefaultValues(currentRow),
    });

    const { data: permissionsData, isLoading: isPermissionsLoading } =
        useGetPermissionsAsAdmin(
            { pageable: { page: 0, size: 1000 } },
            { query: { enabled: open } }
        );

    const permissionOptions = useMemo(
        () =>
            (permissionsData?.items ?? [])
                .filter((p): p is typeof p & { code: string } => !!p.code)
                .sort((a, b) => a.code.localeCompare(b.code)),
        [permissionsData?.items]
    );

    useEffect(() => {
        if (open) {
            form.reset(getDefaultValues(currentRow));
        }
    }, [currentRow, form, open]);

    const invalidateRoleGroups = async () => {
        await queryClient.invalidateQueries({
            queryKey: getRoleGroupsAsAdminQueryKey(),
        });
    };

    const { mutate: createRoleGroup, isPending: isCreating } =
        useCreateRoleGroupAsAdmin({
            mutation: {
                onSuccess: async () => {
                    await invalidateRoleGroups();
                    toast.success("Role group created.");
                    form.reset(getDefaultValues());
                    onOpenChange(false);
                },
                onError: handleServerError,
            },
        });

    const { mutate: updateRoleGroup, isPending: isUpdating } =
        useUpdateRoleGroupAsAdmin({
            mutation: {
                onSuccess: async () => {
                    await invalidateRoleGroups();
                    toast.success("Role group updated.");
                    onOpenChange(false);
                },
                onError: handleServerError,
            },
        });

    const isPending = isCreating || isUpdating;

    const onSubmit = (values: RoleGroupForm) => {
        const description = values.description?.trim() ?? "";

        if (isEdit && currentRow?.id) {
            updateRoleGroup({
                id: currentRow.id,
                data: {
                    name: values.name.trim(),
                    description,
                    permissionCodes: values.permissionCodes,
                },
            });
            return;
        }

        createRoleGroup({
            data: {
                name: values.name.trim(),
                description,
                permissionCodes: values.permissionCodes,
            },
        });
    };

    return (
        <Dialog
            open={open}
            onOpenChange={(nextOpen) => {
                if (!isPending) {
                    if (!nextOpen) {
                        form.reset(getDefaultValues(currentRow));
                    }
                    onOpenChange(nextOpen);
                }
            }}
        >
            <DialogContent className="flex max-h-[90vh] flex-col overflow-y-auto sm:max-w-lg sm:overflow-hidden">
                <DialogHeader className="text-start">
                    <DialogTitle>
                        {isEdit ? "Edit Role Group" : "Add New Role Group"}
                    </DialogTitle>
                    <DialogDescription>
                        {isEdit
                            ? "Update the role group name, description, and assigned permissions."
                            : "Create a role group and assign at least one permission."}
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form
                        id="role-group-form"
                        onSubmit={form.handleSubmit(onSubmit)}
                        className="flex min-h-0 flex-1 flex-col space-y-4"
                    >
                        <ScrollArea className="pe-4 sm:min-h-0 sm:flex-1">
                            <div className="space-y-4 px-1">
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
                                                    placeholder="e.g. Admin, Editor"
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
                                                    disabled={isPending}
                                                    placeholder="Describe the role group..."
                                                    rows={3}
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="permissionCodes"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Permissions</FormLabel>
                                            <div className="rounded-md border">
                                                <ScrollArea className="h-56">
                                                    <div className="space-y-3 p-4">
                                                        {isPermissionsLoading ? (
                                                            <p className="text-sm text-muted-foreground">
                                                                Loading
                                                                permissions...
                                                            </p>
                                                        ) : permissionOptions.length ===
                                                          0 ? (
                                                            <p className="text-sm text-muted-foreground">
                                                                No permissions
                                                                available.
                                                            </p>
                                                        ) : (
                                                            permissionOptions.map(
                                                                (
                                                                    permission
                                                                ) => {
                                                                    const checked =
                                                                        field.value.includes(
                                                                            permission.code
                                                                        );

                                                                    return (
                                                                        <label
                                                                            key={
                                                                                permission.code
                                                                            }
                                                                            className="flex items-start gap-3"
                                                                        >
                                                                            <Checkbox
                                                                                checked={
                                                                                    checked
                                                                                }
                                                                                onCheckedChange={(
                                                                                    nextChecked
                                                                                ) => {
                                                                                    const nextValues =
                                                                                        nextChecked
                                                                                            ? [
                                                                                                  ...field.value,
                                                                                                  permission.code,
                                                                                              ]
                                                                                            : field.value.filter(
                                                                                                  (
                                                                                                      v
                                                                                                  ) =>
                                                                                                      v !==
                                                                                                      permission.code
                                                                                              );
                                                                                    field.onChange(
                                                                                        nextValues
                                                                                    );
                                                                                }}
                                                                            />
                                                                            <div className="space-y-1">
                                                                                <p className="font-mono text-sm leading-none font-medium">
                                                                                    {
                                                                                        permission.code
                                                                                    }
                                                                                </p>
                                                                                {permission.description && (
                                                                                    <p className="text-sm text-muted-foreground">
                                                                                        {
                                                                                            permission.description
                                                                                        }
                                                                                    </p>
                                                                                )}
                                                                            </div>
                                                                        </label>
                                                                    );
                                                                }
                                                            )
                                                        )}
                                                    </div>
                                                </ScrollArea>
                                            </div>
                                            <FormDescription>
                                                Select the permissions granted
                                                by this role group.
                                            </FormDescription>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>
                        </ScrollArea>
                        <DialogFooter className="mt-2 border-t pt-4">
                            <Button
                                type="submit"
                                form="role-group-form"
                                disabled={isPending}
                                className="w-full sm:w-auto"
                            >
                                {isEdit ? "Save changes" : "Create role group"}
                            </Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
