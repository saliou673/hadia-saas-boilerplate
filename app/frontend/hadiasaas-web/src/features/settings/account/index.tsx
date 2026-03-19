"use client";

import { useState } from "react";
import { Separator } from "@/components/ui/separator";
import { Button } from "@/components/ui/button";
import { ContentSection } from "../components/content-section";
import { AccountForm } from "./account-form";
import { DeleteAccountDialog } from "./delete-account-dialog";

export function SettingsAccount() {
    const [deleteOpen, setDeleteOpen] = useState(false);

    return (
        <ContentSection
            title="Account"
            desc="Update the personal and account information stored for your user."
        >
            <div className="space-y-10">
                <AccountForm />
                <div className="space-y-4">
                    <Separator />
                    <div>
                        <h4 className="text-sm font-medium text-destructive">
                            Danger Zone
                        </h4>
                        <p className="mt-1 text-sm text-muted-foreground">
                            Deleting your account is reversible within 30 days.
                            After that, all your data will be permanently
                            erased.
                        </p>
                    </div>
                    <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => setDeleteOpen(true)}
                    >
                        Delete account
                    </Button>
                </div>
                <DeleteAccountDialog
                    open={deleteOpen}
                    onOpenChange={setDeleteOpen}
                />
            </div>
        </ContentSection>
    );
}
