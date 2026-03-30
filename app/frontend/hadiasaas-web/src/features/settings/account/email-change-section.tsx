"use client";

import { useEffect, useState } from "react";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useConfirmEmailChange, useRequestEmailChange } from "@api-client";
import { CheckCircle2Icon } from "lucide-react";
import { toast } from "sonner";
import { handleServerError } from "@/lib/handle-server-error";
import { Button } from "@/components/ui/button";
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
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
    InputOTP,
    InputOTPGroup,
    InputOTPSlot,
} from "@/components/ui/input-otp";

const requestSchema = z.object({
    newEmail: z.email("Please enter a valid email address."),
});

const confirmSchema = z.object({
    code: z.string().length(4, "Please enter the 4-digit code."),
});

type RequestFormValues = z.infer<typeof requestSchema>;
type ConfirmFormValues = z.infer<typeof confirmSchema>;

export function EmailChangeSection() {
    const [otpOpen, setOtpOpen] = useState(false);
    const [pendingEmail, setPendingEmail] = useState("");
    const [confirmed, setConfirmed] = useState(false);
    const [countdown, setCountdown] = useState(5);

    useEffect(() => {
        if (!confirmed) return;
        if (countdown <= 0) {
            window.location.href = "/sign-in";
            return;
        }
        const timer = setTimeout(() => setCountdown((c) => c - 1), 1000);
        return () => clearTimeout(timer);
    }, [confirmed, countdown]);

    const requestForm = useForm<RequestFormValues>({
        resolver: zodResolver(requestSchema),
        defaultValues: { newEmail: "" },
    });

    const confirmForm = useForm<ConfirmFormValues>({
        resolver: zodResolver(confirmSchema),
        defaultValues: { code: "" },
    });

    const requestMutation = useRequestEmailChange({
        mutation: {
            onSuccess: () => {
                setOtpOpen(true);
                toast.success(
                    `A 4-digit code has been sent to ${requestForm.getValues("newEmail")}.`
                );
            },
            onError: handleServerError,
        },
    });

    const confirmMutation = useConfirmEmailChange({
        mutation: {
            onSuccess: () => {
                setConfirmed(true);
            },
            onError: (error) => {
                handleServerError(error);
                confirmForm.reset();
            },
        },
    });

    function onRequestSubmit(values: RequestFormValues) {
        setPendingEmail(values.newEmail);
        requestMutation.mutate({ data: { newEmail: values.newEmail } });
    }

    function onConfirmSubmit(values: ConfirmFormValues) {
        confirmMutation.mutate({ data: { code: values.code } });
    }

    function handleCancel() {
        setOtpOpen(false);
        confirmForm.reset();
        requestForm.reset();
    }

    return (
        <>
            <div className="space-y-4">
                <div>
                    <h4 className="text-sm font-medium">
                        Change email address
                    </h4>
                    <p className="mt-1 text-sm text-muted-foreground">
                        A 4-digit confirmation code will be sent to your new
                        email address to verify ownership. You will be signed
                        out after the change.
                    </p>
                </div>
                <Form {...requestForm}>
                    <form
                        onSubmit={requestForm.handleSubmit(onRequestSubmit)}
                        className="space-y-4"
                    >
                        <FormField
                            control={requestForm.control}
                            name="newEmail"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>New email address</FormLabel>
                                    <FormControl>
                                        <Input
                                            type="email"
                                            placeholder="your-new-email@example.com"
                                            {...field}
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <Button
                            type="submit"
                            size="sm"
                            disabled={requestMutation.isPending}
                        >
                            {requestMutation.isPending
                                ? "Sending code..."
                                : "Send confirmation code"}
                        </Button>
                    </form>
                </Form>
            </div>

            <Dialog
                open={otpOpen}
                onOpenChange={(open) => {
                    // Prevent closing by clicking outside or pressing Escape
                    if (!open) return;
                }}
            >
                <DialogContent showCloseButton={false}>
                    {confirmed ? (
                        <>
                            <DialogHeader>
                                <DialogTitle className="flex items-center gap-2">
                                    <CheckCircle2Icon className="size-5 text-green-500" />
                                    Email changed
                                </DialogTitle>
                                <DialogDescription>
                                    Your email has been successfully updated to{" "}
                                    <span className="font-medium text-foreground">
                                        {pendingEmail}
                                    </span>
                                    . You will be signed out and redirected to
                                    the sign-in page in{" "}
                                    <span className="font-medium text-foreground">
                                        {countdown}s
                                    </span>
                                    .
                                </DialogDescription>
                            </DialogHeader>
                            <DialogFooter>
                                <Button
                                    onClick={() => {
                                        window.location.href = "/sign-in";
                                    }}
                                >
                                    Sign in now
                                </Button>
                            </DialogFooter>
                        </>
                    ) : (
                        <>
                            <DialogHeader>
                                <DialogTitle>
                                    Confirm your new email
                                </DialogTitle>
                                <DialogDescription>
                                    Enter the 4-digit code sent to{" "}
                                    <span className="font-medium text-foreground">
                                        {pendingEmail}
                                    </span>
                                    .
                                </DialogDescription>
                            </DialogHeader>
                            <Form {...confirmForm}>
                                <form
                                    onSubmit={confirmForm.handleSubmit(
                                        onConfirmSubmit
                                    )}
                                    className="space-y-6"
                                >
                                    <FormField
                                        control={confirmForm.control}
                                        name="code"
                                        render={({ field }) => (
                                            <FormItem className="flex flex-col items-center">
                                                <FormLabel className="sr-only">
                                                    Confirmation code
                                                </FormLabel>
                                                <FormControl>
                                                    <InputOTP
                                                        maxLength={4}
                                                        autoFocus
                                                        {...field}
                                                    >
                                                        <InputOTPGroup>
                                                            <InputOTPSlot
                                                                index={0}
                                                            />
                                                            <InputOTPSlot
                                                                index={1}
                                                            />
                                                            <InputOTPSlot
                                                                index={2}
                                                            />
                                                            <InputOTPSlot
                                                                index={3}
                                                            />
                                                        </InputOTPGroup>
                                                    </InputOTP>
                                                </FormControl>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                    <DialogFooter>
                                        <Button
                                            type="button"
                                            variant="outline"
                                            onClick={handleCancel}
                                            disabled={confirmMutation.isPending}
                                        >
                                            Cancel
                                        </Button>
                                        <Button
                                            type="submit"
                                            disabled={confirmMutation.isPending}
                                        >
                                            {confirmMutation.isPending
                                                ? "Confirming..."
                                                : "Confirm"}
                                        </Button>
                                    </DialogFooter>
                                </form>
                            </Form>
                        </>
                    )}
                </DialogContent>
            </Dialog>
        </>
    );
}
