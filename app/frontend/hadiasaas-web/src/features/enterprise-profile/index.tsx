"use client";

import { useEffect } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import {
    useGetEnterpriseProfileAsAdmin,
    useUpsertEnterpriseProfileAsAdmin,
    getEnterpriseProfileAsAdminQueryKey,
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
import { Separator } from "@/components/ui/separator";

const formSchema = z.object({
    companyName: z.string().trim().min(1, "Company name is required."),
    legalForm: z.string().optional(),
    registrationNumber: z.string().optional(),
    vatNumber: z.string().optional(),
    addressLine1: z.string().optional(),
    addressLine2: z.string().optional(),
    city: z.string().optional(),
    postalCode: z.string().optional(),
    countryCode: z
        .string()
        .length(2, "Country code must be exactly 2 characters.")
        .optional()
        .or(z.literal("")),
    phoneNumber: z.string().optional(),
    email: z
        .string()
        .email("Invalid email address.")
        .optional()
        .or(z.literal("")),
    website: z.string().optional(),
    logoUrl: z.string().optional(),
});

type EnterpriseProfileForm = z.infer<typeof formSchema>;

export function EnterpriseProfileFeature() {
    const queryClient = useQueryClient();
    const { data: profile, isLoading } = useGetEnterpriseProfileAsAdmin();

    const form = useForm<EnterpriseProfileForm>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            companyName: "",
            legalForm: "",
            registrationNumber: "",
            vatNumber: "",
            addressLine1: "",
            addressLine2: "",
            city: "",
            postalCode: "",
            countryCode: "",
            phoneNumber: "",
            email: "",
            website: "",
            logoUrl: "",
        },
    });

    useEffect(() => {
        if (profile) {
            form.reset({
                companyName: profile.companyName ?? "",
                legalForm: profile.legalForm ?? "",
                registrationNumber: profile.registrationNumber ?? "",
                vatNumber: profile.vatNumber ?? "",
                addressLine1: profile.addressLine1 ?? "",
                addressLine2: profile.addressLine2 ?? "",
                city: profile.city ?? "",
                postalCode: profile.postalCode ?? "",
                countryCode: profile.countryCode ?? "",
                phoneNumber: profile.phoneNumber ?? "",
                email: profile.email ?? "",
                website: profile.website ?? "",
                logoUrl: profile.logoUrl ?? "",
            });
        }
    }, [profile, form]);

    const upsertMutation = useUpsertEnterpriseProfileAsAdmin({
        mutation: {
            onSuccess: async () => {
                await queryClient.invalidateQueries({
                    queryKey: getEnterpriseProfileAsAdminQueryKey(),
                });
                toast.success("Enterprise profile saved.");
            },
            onError: handleServerError,
        },
    });

    const isPending = upsertMutation.isPending;

    const onSubmit = (values: EnterpriseProfileForm) => {
        upsertMutation.mutate({
            data: {
                companyName: values.companyName.trim(),
                legalForm: values.legalForm?.trim() || undefined,
                registrationNumber:
                    values.registrationNumber?.trim() || undefined,
                vatNumber: values.vatNumber?.trim() || undefined,
                addressLine1: values.addressLine1?.trim() || undefined,
                addressLine2: values.addressLine2?.trim() || undefined,
                city: values.city?.trim() || undefined,
                postalCode: values.postalCode?.trim() || undefined,
                countryCode: values.countryCode?.trim() || undefined,
                phoneNumber: values.phoneNumber?.trim() || undefined,
                email: values.email?.trim() || undefined,
                website: values.website?.trim() || undefined,
                logoUrl: values.logoUrl?.trim() || undefined,
            },
        });
    };

    return (
        <div className="w-full max-w-2xl">
            <div className="mb-6">
                <h2 className="text-2xl font-bold tracking-tight">
                    Enterprise Profile
                </h2>
                <p className="text-muted-foreground">
                    Company information used in invoices and billing documents.
                </p>
            </div>

            {isLoading ? (
                <p className="text-muted-foreground">
                    Loading enterprise profile...
                </p>
            ) : (
                <Form {...form}>
                    <form
                        onSubmit={form.handleSubmit(onSubmit)}
                        className="space-y-8"
                    >
                        <div className="space-y-4">
                            <h3 className="font-semibold">
                                Company Information
                            </h3>
                            <FormField
                                control={form.control}
                                name="companyName"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>
                                            Company Name{" "}
                                            <span className="text-destructive">
                                                *
                                            </span>
                                        </FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                disabled={isPending}
                                                placeholder="Acme Inc."
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <div className="grid grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="legalForm"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Legal Form</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    value={field.value ?? ""}
                                                    disabled={isPending}
                                                    placeholder="SARL, SAS, Ltd…"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="registrationNumber"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>
                                                Registration Number
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    value={field.value ?? ""}
                                                    disabled={isPending}
                                                    placeholder="SIRET / EIN…"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>
                            <FormField
                                control={form.control}
                                name="vatNumber"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>VAT Number</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                value={field.value ?? ""}
                                                disabled={isPending}
                                                placeholder="FR12345678901"
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>

                        <Separator />

                        <div className="space-y-4">
                            <h3 className="font-semibold">Address</h3>
                            <FormField
                                control={form.control}
                                name="addressLine1"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Address Line 1</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                value={field.value ?? ""}
                                                disabled={isPending}
                                                placeholder="10 Rue de Paris"
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="addressLine2"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Address Line 2</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                value={field.value ?? ""}
                                                disabled={isPending}
                                                placeholder="Apt 4B"
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <div className="grid grid-cols-3 gap-4">
                                <FormField
                                    control={form.control}
                                    name="postalCode"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Postal Code</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    value={field.value ?? ""}
                                                    disabled={isPending}
                                                    placeholder="75000"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="city"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>City</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    value={field.value ?? ""}
                                                    disabled={isPending}
                                                    placeholder="Paris"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="countryCode"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Country Code</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    value={field.value ?? ""}
                                                    disabled={isPending}
                                                    placeholder="FR"
                                                    maxLength={2}
                                                />
                                            </FormControl>
                                            <FormDescription>
                                                ISO 3166-1 alpha-2
                                            </FormDescription>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>
                        </div>

                        <Separator />

                        <div className="space-y-4">
                            <h3 className="font-semibold">Contact</h3>
                            <div className="grid grid-cols-2 gap-4">
                                <FormField
                                    control={form.control}
                                    name="phoneNumber"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Phone Number</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    value={field.value ?? ""}
                                                    disabled={isPending}
                                                    placeholder="+33 1 23 45 67 89"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="email"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Email</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    value={field.value ?? ""}
                                                    type="email"
                                                    disabled={isPending}
                                                    placeholder="contact@company.com"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />
                            </div>
                            <FormField
                                control={form.control}
                                name="website"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Website</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                value={field.value ?? ""}
                                                disabled={isPending}
                                                placeholder="https://company.com"
                                            />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="logoUrl"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Logo URL</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                value={field.value ?? ""}
                                                disabled={isPending}
                                                placeholder="https://cdn.example.com/logo.png"
                                            />
                                        </FormControl>
                                        <FormDescription>
                                            URL to the company logo used in
                                            invoices.
                                        </FormDescription>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>

                        <div className="flex justify-end">
                            <Button type="submit" disabled={isPending}>
                                {isPending ? "Saving…" : "Save profile"}
                            </Button>
                        </div>
                    </form>
                </Form>
            )}
        </div>
    );
}
