"use client";

import { format, parseISO } from "date-fns";
import { type UserSummary, useGetUserDetails } from "@api-client";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";

function formatBirthDate(birthDate?: string) {
    if (!birthDate) return "Not provided";

    const parsedDate = parseISO(birthDate);
    return Number.isNaN(parsedDate.getTime())
        ? birthDate
        : format(parsedDate, "MMMM d, yyyy");
}

function formatEnumValue(value?: string) {
    if (!value) return "Not provided";

    return value
        .toLowerCase()
        .split("_")
        .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
        .join(" ");
}

function ProfileSummary({ user }: { user: UserSummary }) {
    return (
        <Card>
            <CardHeader>
                <CardTitle>Profile summary</CardTitle>
                <CardDescription>
                    Review the account information currently stored for your
                    profile.
                </CardDescription>
            </CardHeader>
            <CardContent className="grid gap-4 sm:grid-cols-2">
                <div className="space-y-1">
                    <p className="text-sm font-medium">Email</p>
                    <p className="text-sm text-muted-foreground">
                        {user.email}
                    </p>
                </div>
                <div className="space-y-1">
                    <p className="text-sm font-medium">Status</p>
                    <p className="text-sm text-muted-foreground">
                        {formatEnumValue(user.status)}
                    </p>
                </div>
                <div className="space-y-1">
                    <p className="text-sm font-medium">First name</p>
                    <p className="text-sm text-muted-foreground">
                        {user.firstName}
                    </p>
                </div>
                <div className="space-y-1">
                    <p className="text-sm font-medium">Last name</p>
                    <p className="text-sm text-muted-foreground">
                        {user.lastName}
                    </p>
                </div>
                <div className="space-y-1">
                    <p className="text-sm font-medium">Birth date</p>
                    <p className="text-sm text-muted-foreground">
                        {formatBirthDate(user.birthDate)}
                    </p>
                </div>
                <div className="space-y-1">
                    <p className="text-sm font-medium">Gender</p>
                    <p className="text-sm text-muted-foreground">
                        {formatEnumValue(user.gender)}
                    </p>
                </div>
                <div className="space-y-1">
                    <p className="text-sm font-medium">Phone number</p>
                    <p className="text-sm text-muted-foreground">
                        {user.phoneNumber || "Not provided"}
                    </p>
                </div>
                <div className="space-y-1">
                    <p className="text-sm font-medium">Language</p>
                    <p className="text-sm text-muted-foreground">
                        {user.languageKey || "Not provided"}
                    </p>
                </div>
                <div className="space-y-1 sm:col-span-2">
                    <p className="text-sm font-medium">Address</p>
                    <p className="text-sm text-muted-foreground">
                        {user.address || "Not provided"}
                    </p>
                </div>
            </CardContent>
        </Card>
    );
}

function ProfileFormSkeleton() {
    return <Skeleton className="h-72 w-full" />;
}

export function ProfileForm() {
    const { data: user, isLoading, isError } = useGetUserDetails();

    if (isLoading) {
        return <ProfileFormSkeleton />;
    }

    if (isError || !user) {
        return (
            <Card>
                <CardHeader>
                    <CardTitle>Unable to load profile</CardTitle>
                    <CardDescription>
                        Refresh the page and try again.
                    </CardDescription>
                </CardHeader>
            </Card>
        );
    }

    return <ProfileSummary user={user} />;
}
