"use client";

import { useEffect, useState } from "react";
import { type SubscriptionPlan } from "@api-client";
import { Plus } from "lucide-react";
import { toast } from "sonner";
import {
    useNextNavigateSearch,
    useNextSearchObject,
} from "@/hooks/use-next-search-state";
import { Button } from "@/components/ui/button";
import { SubscriptionPlansDeleteDialog } from "./subscription-plans-delete-dialog";
import { SubscriptionPlansMutateDrawer } from "./subscription-plans-mutate-drawer";
import { SubscriptionPlansTable } from "./subscription-plans-table";

export function SubscriptionPlans() {
    const search = useNextSearchObject();
    const navigate = useNextNavigateSearch();
    const [currentRow, setCurrentRow] = useState<SubscriptionPlan | null>(null);
    const [deleteOpen, setDeleteOpen] = useState(false);
    const [mutateOpen, setMutateOpen] = useState(false);
    const [mutateInitialTab, setMutateInitialTab] = useState<
        "form" | "preview"
    >("form");
    const [totalItems, setTotalItems] = useState(0);

    useEffect(() => {
        if (!deleteOpen) {
            const timeout = window.setTimeout(() => {
                setCurrentRow(null);
            }, 200);

            return () => window.clearTimeout(timeout);
        }
    }, [deleteOpen]);

    const handleOnEdit = (plan: SubscriptionPlan) => {
        setCurrentRow(plan);
        setMutateInitialTab("form");
        setMutateOpen(true);
    };

    const handleOnPreview = (plan: SubscriptionPlan) => {
        setCurrentRow(plan);
        setMutateInitialTab("preview");
        setMutateOpen(true);
    };

    const handleOnDelete = (plan: SubscriptionPlan) => {
        setCurrentRow(plan);
        setDeleteOpen(true);
    };

    return (
        <>
            <div className="flex flex-1 flex-col gap-4 sm:gap-6">
                <div className="flex flex-wrap items-end justify-between gap-2">
                    <div>
                        <h2 className="text-2xl font-bold tracking-tight">
                            Subscription Plans
                        </h2>
                        <p className="text-muted-foreground">
                            Manage the subscription plans available to users —
                            pricing, features, and availability.
                        </p>
                    </div>
                    <div className="flex items-center gap-3">
                        <div className="text-sm text-muted-foreground">
                            {totalItems} plan{totalItems === 1 ? "" : "s"}
                        </div>
                        <Button
                            onClick={() => {
                                setCurrentRow(null);
                                setMutateInitialTab("form");
                                setMutateOpen(true);
                            }}
                        >
                            <Plus className="size-4" />
                            New Plan
                        </Button>
                    </div>
                </div>

                <SubscriptionPlansTable
                    search={search}
                    navigate={navigate}
                    onEdit={handleOnEdit}
                    onDelete={handleOnDelete}
                    onPreview={handleOnPreview}
                    onTotalItemsChange={setTotalItems}
                />
            </div>

            <SubscriptionPlansMutateDrawer
                open={mutateOpen}
                onOpenChange={setMutateOpen}
                currentRow={currentRow}
                initialTab={mutateInitialTab}
            />
            <SubscriptionPlansDeleteDialog
                open={deleteOpen}
                onOpenChange={setDeleteOpen}
                currentRow={currentRow}
                onDeleted={() => toast.success("Subscription plan deleted.")}
            />
        </>
    );
}
