"use client";

import { useEffect } from "react";
import { z } from "zod";
import { format, parseISO } from "date-fns";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import {
    getUserDetailsQueryKey,
    type UpdateAccountMutationRequest,
    type UserSummary,
    updateUserRequestGenderEnum,
    useGetUserDetails,
    useUpdateAccount,
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
import { Skeleton } from "@/components/ui/skeleton";
import { Textarea } from "@/components/ui/textarea";
import { DatePicker } from "@/components/date-picker";
import { SelectDropdown } from "@/components/select-dropdown";

const languageOptions = [
    { label: "English", value: "en" },
    { label: "French", value: "fr" },
] as const;

const genderOptions = [
    { label: "Male", value: updateUserRequestGenderEnum.MALE },
    { label: "Female", value: updateUserRequestGenderEnum.FEMALE },
] as const;

const accountFormSchema = z.object({
    firstName: z
        .string()
        .trim()
        .min(1, "Please enter your first name.")
        .max(100, "First name must not be longer than 100 characters."),
    lastName: z
        .string()
        .trim()
        .min(1, "Please enter your last name.")
        .max(100, "Last name must not be longer than 100 characters."),
    email: z.email("Please enter a valid email."),
    phoneNumber: z
        .string()
        .max(50, "Phone number must not be longer than 50 characters.")
        .optional(),
    birthDate: z.date("Please select your birth date."),
    gender: z.enum(["MALE", "FEMALE"], "Please select your gender."),
    address: z
        .string()
        .max(500, "Address must not be longer than 500 characters.")
        .optional(),
    languageKey: z
        .string()
        .max(20, "Language must not be longer than 20 characters.")
        .optional(),
    imageUrl: z
        .union([z.literal(""), z.url("Please enter a valid image URL.")])
        .optional(),
});

type AccountFormValues = z.infer<typeof accountFormSchema>;

const defaultValues: AccountFormValues = {
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    birthDate: new Date("1990-01-01"),
    gender: "MALE",
    address: "",
    languageKey: "",
    imageUrl: "",
};

function normalizeOptional(value?: string) {
    const trimmed = value?.trim();
    return trimmed ? trimmed : undefined;
}

function mapUserToFormValues(user: UserSummary): AccountFormValues {
    return {
        firstName: user.firstName ?? "",
        lastName: user.lastName ?? "",
        email: user.email ?? "",
        phoneNumber: user.phoneNumber ?? "",
        birthDate: parseISO(user.birthDate),
        gender: user.gender,
        address: user.address ?? "",
        languageKey: user.languageKey ?? "",
        imageUrl: user.imageUrl ?? "",
    };
}

function toUpdatePayload(
    values: AccountFormValues
): UpdateAccountMutationRequest {
    return {
        firstName: values.firstName.trim(),
        lastName: values.lastName.trim(),
        phoneNumber: normalizeOptional(values.phoneNumber),
        birthDate: format(values.birthDate, "yyyy-MM-dd"),
        gender: values.gender,
        address: normalizeOptional(values.address),
        languageKey: normalizeOptional(values.languageKey),
        imageUrl: normalizeOptional(values.imageUrl),
    };
}

function AccountFormSkeleton() {
    return (
        <div className="space-y-4">
            <Skeleton className="h-20 w-full" />
            <Skeleton className="h-20 w-full" />
            <Skeleton className="h-20 w-full" />
            <Skeleton className="h-20 w-full" />
            <Skeleton className="h-20 w-full" />
            <Skeleton className="h-20 w-full" />
            <Skeleton className="h-24 w-full" />
            <Skeleton className="h-24 w-full" />
        </div>
    );
}

export function AccountForm() {
    const queryClient = useQueryClient();
    const { data: user, isLoading, isError } = useGetUserDetails();
    const updateAccount = useUpdateAccount({
        mutation: {
            onSuccess: (updatedUser) => {
                queryClient.setQueryData(getUserDetailsQueryKey(), updatedUser);
                toast.success("Account updated");
            },
            onError: handleServerError,
        },
    });

    const form = useForm<AccountFormValues>({
        resolver: zodResolver(accountFormSchema),
        defaultValues,
        mode: "onChange",
    });

    useEffect(() => {
        if (!user) return;
        form.reset(mapUserToFormValues(user));
    }, [form, user]);

    if (isLoading) {
        return <AccountFormSkeleton />;
    }

    if (isError || !user) {
        return (
            <p className="text-sm text-muted-foreground">
                Unable to load account details. Refresh the page and try again.
            </p>
        );
    }

    return (
        <Form {...form}>
            <form
                onSubmit={form.handleSubmit((values) =>
                    updateAccount.mutate({ data: toUpdatePayload(values) })
                )}
                className="space-y-8"
            >
                <div className="grid gap-6 md:grid-cols-2">
                    <FormField
                        control={form.control}
                        name="firstName"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>First name</FormLabel>
                                <FormControl>
                                    <Input
                                        placeholder="Enter your first name"
                                        {...field}
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
                                <FormLabel>Last name</FormLabel>
                                <FormControl>
                                    <Input
                                        placeholder="Enter your last name"
                                        {...field}
                                    />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                </div>

                <FormField
                    control={form.control}
                    name="email"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Email</FormLabel>
                            <FormControl>
                                <Input {...field} disabled />
                            </FormControl>
                            <FormDescription>
                                Email is managed separately and cannot be
                                changed from this page.
                            </FormDescription>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                <FormField
                    control={form.control}
                    name="phoneNumber"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Phone number</FormLabel>
                            <FormControl>
                                <Input
                                    placeholder="Enter your phone number"
                                    {...field}
                                    value={field.value ?? ""}
                                />
                            </FormControl>
                            <FormDescription>
                                Add a contact number for account-related
                                communication.
                            </FormDescription>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                <div className="grid gap-6 md:grid-cols-2">
                    <FormField
                        control={form.control}
                        name="birthDate"
                        render={({ field }) => (
                            <FormItem className="flex flex-col">
                                <FormLabel>Birth date</FormLabel>
                                <FormControl>
                                    <DatePicker
                                        selected={field.value}
                                        onSelect={(date) =>
                                            field.onChange(date ?? field.value)
                                        }
                                        placeholder="Select your birth date"
                                    />
                                </FormControl>
                                <FormDescription>
                                    Used as part of your account profile.
                                </FormDescription>
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
                                    onValueChange={field.onChange}
                                    placeholder="Select gender"
                                    className="w-full"
                                    items={genderOptions.map((option) => ({
                                        label: option.label,
                                        value: option.value,
                                    }))}
                                    isControlled
                                />
                                <FormDescription>
                                    This value is stored on your account.
                                </FormDescription>
                                <FormMessage />
                            </FormItem>
                        )}
                    />
                </div>

                <FormField
                    control={form.control}
                    name="languageKey"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Language</FormLabel>
                            <FormControl>
                                <Input
                                    list="account-language-options"
                                    placeholder="Select or type a language code"
                                    {...field}
                                    value={field.value ?? ""}
                                />
                            </FormControl>
                            <datalist id="account-language-options">
                                {languageOptions.map((language) => (
                                    <option
                                        key={language.value}
                                        value={language.value}
                                    >
                                        {language.label}
                                    </option>
                                ))}
                            </datalist>
                            <FormDescription>
                                Stored as a language code such as `en` or `fr`.
                            </FormDescription>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                <FormField
                    control={form.control}
                    name="imageUrl"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Profile image URL</FormLabel>
                            <FormControl>
                                <Input
                                    placeholder="https://example.com/avatar.png"
                                    {...field}
                                    value={field.value ?? ""}
                                />
                            </FormControl>
                            <FormDescription>
                                Use a public image URL for your avatar.
                            </FormDescription>
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
                                <Textarea
                                    placeholder="Enter your address"
                                    className="resize-none"
                                    {...field}
                                    value={field.value ?? ""}
                                />
                            </FormControl>
                            <FormDescription>
                                This address is stored on your account profile.
                            </FormDescription>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                <Button
                    type="submit"
                    disabled={
                        updateAccount.isPending || !form.formState.isDirty
                    }
                >
                    {updateAccount.isPending
                        ? "Saving changes..."
                        : "Update account"}
                </Button>
            </form>
        </Form>
    );
}
