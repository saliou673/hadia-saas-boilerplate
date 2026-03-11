"use client";

import { useEffect, useMemo } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import {
    type CreateAdminUserRequestGenderEnumKey,
    type UpdateUserRequestGenderEnumKey,
    getUserAsAdminQueryKey,
    useCreateUserAsAdmin,
    useGetRoleGroupsAsAdmin,
    useGetUserAsAdmin,
    useUpdateUserAsAdmin,
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
import { SelectDropdown } from "@/components/select-dropdown";
import { genderOptions } from "../data/data";
import {
    mapRoleGroupToOption,
    mapUserDetailsToRow,
    type RoleGroupOption,
    type UserRow,
} from "../data/schema";

const formSchema = z
    .object({
        email: z.string().trim().email("Email is required."),
        firstName: z.string().trim().min(1, "First name is required."),
        lastName: z.string().trim().min(1, "Last name is required."),
        birthDate: z.string().optional(),
        gender: z.string().optional(),
        phoneNumber: z.string().optional(),
        address: z.string().optional(),
        languageKey: z.string().optional(),
        imageUrl: z.string().optional(),
        roleGroupNames: z.array(z.string()),
        isEdit: z.boolean(),
    })
    .superRefine((values, context) => {
        if (!values.isEdit && values.roleGroupNames.length === 0) {
            context.addIssue({
                code: z.ZodIssueCode.custom,
                message: "At least one role group is required.",
                path: ["roleGroupNames"],
            });
        }
    });

type UserForm = z.infer<typeof formSchema>;

type UserActionDialogProps = {
    currentRow?: UserRow;
    open: boolean;
    onOpenChange: (open: boolean) => void;
};

function getDefaultValues(currentRow?: UserRow): UserForm {
    return {
        email: currentRow?.email ?? "",
        firstName: currentRow?.firstName ?? "",
        lastName: currentRow?.lastName ?? "",
        birthDate: currentRow?.birthDate ?? "",
        gender: currentRow?.gender ?? undefined,
        phoneNumber: currentRow?.phoneNumber ?? "",
        address: currentRow?.address ?? "",
        languageKey: currentRow?.languageKey ?? "",
        imageUrl: currentRow?.imageUrl ?? "",
        roleGroupNames: [],
        isEdit: !!currentRow,
    };
}

function normalizeOptionalString(value?: string) {
    const nextValue = value?.trim();
    return nextValue ? nextValue : undefined;
}

export function UsersActionDialog({
    currentRow,
    open,
    onOpenChange,
}: UserActionDialogProps) {
    const isEdit = !!currentRow;
    const queryClient = useQueryClient();
    const form = useForm<UserForm>({
        resolver: zodResolver(formSchema),
        defaultValues: getDefaultValues(currentRow),
    });

    const { data: userDetails } = useGetUserAsAdmin(currentRow?.id ?? 0, {
        query: {
            enabled: open && isEdit && !!currentRow?.id,
        },
    });
    const { data: roleGroupsData, isLoading: isRoleGroupsLoading } =
        useGetRoleGroupsAsAdmin(
            {
                pageable: {
                    page: 0,
                    size: 100,
                },
            },
            {
                query: {
                    enabled: open && !isEdit,
                },
            }
        );

    const roleGroupOptions = useMemo(
        () =>
            (roleGroupsData?.items ?? [])
                .map(mapRoleGroupToOption)
                .filter((option): option is RoleGroupOption => option !== null)
                .sort((left, right) => left.name.localeCompare(right.name)),
        [roleGroupsData?.items]
    );

    useEffect(() => {
        if (isEdit && userDetails) {
            form.reset({
                ...getDefaultValues(mapUserDetailsToRow(userDetails)),
                isEdit: true,
            });
            return;
        }

        form.reset(getDefaultValues(currentRow));
    }, [currentRow, form, isEdit, userDetails, open]);

    const invalidateUsers = async () => {
        await queryClient.invalidateQueries({
            queryKey: [{ url: getGetUsersAsAdminUrl().url }],
        });
    };

    const { mutate: createUser, isPending: isCreatingUser } =
        useCreateUserAsAdmin({
            mutation: {
                onSuccess: async () => {
                    await invalidateUsers();
                    toast.success("User created.");
                    form.reset(getDefaultValues());
                    onOpenChange(false);
                },
                onError: handleServerError,
            },
        });

    const { mutate: updateUser, isPending: isUpdatingUser } =
        useUpdateUserAsAdmin({
            mutation: {
                onSuccess: async (updatedUser) => {
                    await invalidateUsers();
                    if (currentRow?.id) {
                        queryClient.setQueryData(
                            getUserAsAdminQueryKey(currentRow.id),
                            updatedUser
                        );
                    }
                    toast.success("User updated.");
                    onOpenChange(false);
                },
                onError: handleServerError,
            },
        });

    const isPending = isCreatingUser || isUpdatingUser;

    const onSubmit = (values: UserForm) => {
        if (isEdit && currentRow?.id) {
            updateUser({
                id: currentRow.id,
                data: {
                    firstName: values.firstName.trim(),
                    lastName: values.lastName.trim(),
                    birthDate: normalizeOptionalString(values.birthDate),
                    gender: values.gender
                        ? (values.gender as UpdateUserRequestGenderEnumKey)
                        : undefined,
                    phoneNumber: normalizeOptionalString(values.phoneNumber),
                    address: normalizeOptionalString(values.address),
                    languageKey: normalizeOptionalString(values.languageKey),
                    imageUrl: normalizeOptionalString(values.imageUrl),
                },
            });
            return;
        }

        createUser({
            data: {
                email: values.email.trim(),
                firstName: values.firstName.trim(),
                lastName: values.lastName.trim(),
                birthDate: normalizeOptionalString(values.birthDate),
                gender: values.gender
                    ? (values.gender as CreateAdminUserRequestGenderEnumKey)
                    : undefined,
                phoneNumber: normalizeOptionalString(values.phoneNumber),
                address: normalizeOptionalString(values.address),
                languageKey: normalizeOptionalString(values.languageKey),
                imageUrl: normalizeOptionalString(values.imageUrl),
                roleGroupNames: values.roleGroupNames,
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
            <DialogContent className="flex max-h-[90vh] flex-col overflow-y-auto sm:max-w-2xl sm:overflow-hidden">
                <DialogHeader className="text-start">
                    <DialogTitle>
                        {isEdit ? "Edit User" : "Add New User"}
                    </DialogTitle>
                    <DialogDescription>
                        {isEdit
                            ? "Update the managed user profile. Email and assigned role groups cannot be changed from this dialog."
                            : "Create a managed user and assign at least one role group."}
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form
                        id="user-form"
                        onSubmit={form.handleSubmit(onSubmit)}
                        className="flex min-h-0 flex-1 flex-col space-y-4"
                    >
                        <ScrollArea className="pe-4 sm:min-h-0 sm:flex-1">
                            <div className="space-y-4 px-1">
                                <FormField
                                    control={form.control}
                                    name="email"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Email</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    disabled={
                                                        isEdit || isPending
                                                    }
                                                    placeholder="john.doe@example.com"
                                                />
                                            </FormControl>
                                            {isEdit && (
                                                <FormDescription>
                                                    Email updates are not
                                                    supported by the admin user
                                                    endpoint.
                                                </FormDescription>
                                            )}
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <div className="grid gap-4 sm:grid-cols-2">
                                    <FormField
                                        control={form.control}
                                        name="firstName"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>
                                                    First Name
                                                </FormLabel>
                                                <FormControl>
                                                    <Input
                                                        {...field}
                                                        disabled={isPending}
                                                        placeholder="John"
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                    <FormField
                                        control={form.control}
                                        name="lastName"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>Last Name</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        {...field}
                                                        disabled={isPending}
                                                        placeholder="Doe"
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                </div>
                                <div className="grid gap-4 sm:grid-cols-2">
                                    <FormField
                                        control={form.control}
                                        name="birthDate"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>
                                                    Birth Date
                                                </FormLabel>
                                                <FormControl>
                                                    <Input
                                                        {...field}
                                                        type="date"
                                                        disabled={isPending}
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                    <FormField
                                        control={form.control}
                                        name="gender"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>Gender</FormLabel>
                                                <SelectDropdown
                                                    defaultValue={field.value}
                                                    onValueChange={
                                                        field.onChange
                                                    }
                                                    placeholder="Select a gender"
                                                    items={genderOptions}
                                                    disabled={isPending}
                                                    className="w-full"
                                                    isControlled
                                                />
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                </div>
                                <FormField
                                    control={form.control}
                                    name="phoneNumber"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Phone Number</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    disabled={isPending}
                                                    placeholder="+33601020304"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="address"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Address</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    disabled={isPending}
                                                    placeholder="221B Baker Street"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <div className="grid gap-4 sm:grid-cols-2">
                                    <FormField
                                        control={form.control}
                                        name="languageKey"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>
                                                    Language Key
                                                </FormLabel>
                                                <FormControl>
                                                    <Input
                                                        {...field}
                                                        disabled={isPending}
                                                        placeholder="en"
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                    <FormField
                                        control={form.control}
                                        name="imageUrl"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>Image URL</FormLabel>
                                                <FormControl>
                                                    <Input
                                                        {...field}
                                                        disabled={isPending}
                                                        placeholder="https://example.com/avatar.png"
                                                    />
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                </div>
                                {!isEdit && (
                                    <FormField
                                        control={form.control}
                                        name="roleGroupNames"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>
                                                    Role Groups
                                                </FormLabel>
                                                <div className="rounded-md border">
                                                    <ScrollArea className="h-48">
                                                        <div className="space-y-3 p-4">
                                                            {isRoleGroupsLoading ? (
                                                                <p className="text-sm text-muted-foreground">
                                                                    Loading role
                                                                    groups...
                                                                </p>
                                                            ) : roleGroupOptions.length ===
                                                              0 ? (
                                                                <p className="text-sm text-muted-foreground">
                                                                    No role
                                                                    groups
                                                                    available.
                                                                </p>
                                                            ) : (
                                                                roleGroupOptions.map(
                                                                    (
                                                                        roleGroup
                                                                    ) => {
                                                                        const checked =
                                                                            field.value.includes(
                                                                                roleGroup.name
                                                                            );

                                                                        return (
                                                                            <label
                                                                                key={
                                                                                    roleGroup.id
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
                                                                                                      roleGroup.name,
                                                                                                  ]
                                                                                                : field.value.filter(
                                                                                                      (
                                                                                                          value
                                                                                                      ) =>
                                                                                                          value !==
                                                                                                          roleGroup.name
                                                                                                  );

                                                                                        field.onChange(
                                                                                            nextValues
                                                                                        );
                                                                                    }}
                                                                                />
                                                                                <div className="space-y-1">
                                                                                    <p className="text-sm leading-none font-medium">
                                                                                        {
                                                                                            roleGroup.name
                                                                                        }
                                                                                    </p>
                                                                                    {roleGroup.description && (
                                                                                        <p className="text-sm text-muted-foreground">
                                                                                            {
                                                                                                roleGroup.description
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
                                                    Role groups are required
                                                    when creating a managed
                                                    user.
                                                </FormDescription>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                )}
                            </div>
                        </ScrollArea>
                        <DialogFooter className="mt-2 border-t pt-4">
                            <Button
                                type="submit"
                                form="user-form"
                                disabled={isPending}
                                className="w-full sm:w-auto"
                            >
                                {isEdit ? "Save changes" : "Create user"}
                            </Button>
                        </DialogFooter>
                    </form>
                </Form>
            </DialogContent>
        </Dialog>
    );
}
