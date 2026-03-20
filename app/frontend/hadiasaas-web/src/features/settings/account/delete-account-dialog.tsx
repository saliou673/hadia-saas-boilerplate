"use client";

import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useDeleteCurrentAccount } from "@api-client";
import { AlertTriangle } from "lucide-react";
import { signIn, signOut, useSession } from "next-auth/react";
import { toast } from "sonner";
import { Label } from "@/components/ui/label";
import { ConfirmDialog } from "@/components/confirm-dialog";
import { PasswordInput } from "@/components/password-input";

interface DeleteAccountDialogProps {
    open: boolean;
    onOpenChange: (open: boolean) => void;
}

export function DeleteAccountDialog({
    open,
    onOpenChange,
}: DeleteAccountDialogProps) {
    const { data: session } = useSession();
    const email = session?.user?.email ?? "";
    const [password, setPassword] = useState("");
    const [isVerifying, setIsVerifying] = useState(false);
    const queryClient = useQueryClient();

    const { mutate: deleteAccount, isPending: isDeleting } =
        useDeleteCurrentAccount();

    const isPending = isVerifying || isDeleting;

    const handleOpenChange = (nextOpen: boolean) => {
        if (!isPending) {
            onOpenChange(nextOpen);
            if (!nextOpen) setPassword("");
        }
    };

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        if (!password) return;

        setIsVerifying(true);
        const result = await signIn("credentials", {
            email,
            password,
            redirect: false,
        });
        setIsVerifying(false);

        if (result?.error) {
            toast.error("Invalid password. Please try again.");
            return;
        }

        deleteAccount(undefined, {
            onSuccess: async () => {
                queryClient.clear();
                await signOut({ redirect: true, callbackUrl: "/" });
            },
            onError: () => {
                toast.error(
                    "Failed to delete your account. Please try again later."
                );
            },
        });
    };

    return (
        <ConfirmDialog
            open={open}
            onOpenChange={handleOpenChange}
            handleConfirm={() => {}}
            confirmForm="delete-account-form"
            confirmButtonType="submit"
            disabled={!password || isPending}
            isLoading={isPending}
            title={
                <span className="text-destructive">
                    <AlertTriangle
                        className="me-1 inline-block stroke-destructive"
                        size={18}
                    />{" "}
                    Delete account
                </span>
            }
            desc={
                <form
                    id="delete-account-form"
                    className="space-y-4"
                    onSubmit={handleSubmit}
                >
                    <p>
                        This will permanently deactivate your account. You have
                        30 days to recover it before all your data is erased.
                        <br />
                        <br />
                        Enter your password to confirm you are the account
                        owner.
                    </p>
                    <div className="space-y-1.5">
                        <Label htmlFor="delete-account-email">Email</Label>
                        <p
                            id="delete-account-email"
                            className="rounded-md border bg-muted px-3 py-2 text-sm text-muted-foreground"
                        >
                            {email}
                        </p>
                    </div>
                    <div className="space-y-1.5">
                        <Label htmlFor="delete-account-password">
                            Password
                        </Label>
                        <PasswordInput
                            id="delete-account-password"
                            placeholder="Enter your password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            disabled={isPending}
                        />
                    </div>
                </form>
            }
            confirmText="Delete account"
            destructive
        />
    );
}
