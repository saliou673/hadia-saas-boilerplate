"use client";

import React, { useEffect } from "react";
import { z } from "zod";
import { useFieldArray, useForm, useWatch } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useQueryClient } from "@tanstack/react-query";
import {
    type SubscriptionPlan,
    useCreateSubscriptionPlanAsAdmin,
    useUpdateSubscriptionPlanAsAdmin,
} from "@api-client";
import {
    DndContext,
    type DragEndEvent,
    KeyboardSensor,
    PointerSensor,
    closestCenter,
    useSensor,
    useSensors,
} from "@dnd-kit/core";
import {
    SortableContext,
    sortableKeyboardCoordinates,
    useSortable,
    verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { Check, GripVertical, Plus, Trash2 } from "lucide-react";
import { toast } from "sonner";
import { handleServerError } from "@/lib/handle-server-error";
import { toIntOrUndefined, toNumericOrUndefined } from "@/lib/number";
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
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Switch } from "@/components/ui/switch";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Textarea } from "@/components/ui/textarea";
import { getSubscriptionPlansAsAdminQueryKey } from "../../../../hadiasaas-apiclient";

const formSchema = z
    .object({
        title: z
            .string()
            .trim()
            .min(1, "Title is required.")
            .max(255, "Title must be 255 characters or fewer."),
        description: z.string().optional(),
        currencyCode: z
            .string()
            .trim()
            .min(1, "Currency code is required.")
            .max(10, "Currency code must be 10 characters or fewer."),
        monthlyPrice: z.string().optional(),
        yearlyPrice: z.string().optional(),
        lifetimePrice: z.string().optional(),
        price: z.string().optional(),
        durationDays: z.string().optional(),
        features: z.array(z.object({ value: z.string() })),
        active: z.boolean(),
    })
    .superRefine((data, ctx) => {
        const hasMonthly = !!data.monthlyPrice?.trim();
        const hasYearly = !!data.yearlyPrice?.trim();
        const hasLifetime = !!data.lifetimePrice?.trim();
        const hasCustomPrice = !!data.price?.trim();
        const hasDuration = !!data.durationDays?.trim();

        if (!hasMonthly && !hasYearly && !hasLifetime && !hasCustomPrice) {
            ctx.addIssue({
                code: z.ZodIssueCode.custom,
                message:
                    "At least one price (monthly, yearly, lifetime, or custom) is required.",
                path: ["monthlyPrice"],
            });
        }

        if (hasCustomPrice && !hasDuration) {
            ctx.addIssue({
                code: z.ZodIssueCode.custom,
                message:
                    "Duration (days) is required when a custom price is set.",
                path: ["durationDays"],
            });
        }

        if (hasDuration && !hasCustomPrice) {
            ctx.addIssue({
                code: z.ZodIssueCode.custom,
                message: "Custom price is required when duration is set.",
                path: ["price"],
            });
        }
    });

type PlanForm = z.infer<typeof formSchema>;

type SubscriptionPlansMutateDrawerProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow?: SubscriptionPlan | null;
    initialTab?: "form" | "preview";
};

type SortableFeatureItemProps = {
    id: string;
    index: number;
    disabled: boolean;
    onRemove: (index: number) => void;
    children: React.ReactNode;
};

function SortableFeatureItem({
    id,
    index,
    disabled,
    onRemove,
    children,
}: SortableFeatureItemProps) {
    const {
        attributes,
        listeners,
        setNodeRef,
        transform,
        transition,
        isDragging,
    } = useSortable({ id });

    return (
        <div
            ref={setNodeRef}
            style={{
                transform: CSS.Transform.toString(transform),
                transition,
                opacity: isDragging ? 0.5 : 1,
            }}
            className="flex items-center gap-2"
        >
            <button
                type="button"
                className="cursor-grab touch-none text-muted-foreground hover:text-foreground disabled:cursor-not-allowed"
                disabled={disabled}
                {...attributes}
                {...listeners}
            >
                <GripVertical className="size-4" />
            </button>
            <div className="flex-1">{children}</div>
            <Button
                type="button"
                variant="ghost"
                size="icon"
                onClick={() => onRemove(index)}
                disabled={disabled}
            >
                <Trash2 className="size-4 text-destructive" />
            </Button>
        </div>
    );
}

type PlanPreviewProps = {
    values: PlanForm;
};

function PlanPreview({ values }: PlanPreviewProps) {
    const currency = values.currencyCode || "USD";
    const features = values.features.map((f) => f.value.trim()).filter(Boolean);

    const pricingLines: string[] = [];
    if (values.monthlyPrice?.trim())
        pricingLines.push(`${values.monthlyPrice} ${currency} / month`);
    if (values.yearlyPrice?.trim())
        pricingLines.push(`${values.yearlyPrice} ${currency} / year`);
    if (values.lifetimePrice?.trim())
        pricingLines.push(`${values.lifetimePrice} ${currency} lifetime`);
    if (values.price?.trim() && values.durationDays?.trim())
        pricingLines.push(
            `${values.price} ${currency} / ${values.durationDays} days`
        );

    const primaryPrice = pricingLines[0] ?? null;
    const otherPrices = pricingLines.slice(1);

    return (
        <div className="flex h-full items-start justify-center pt-4">
            <div className="w-full max-w-sm rounded-2xl border bg-card p-6 shadow-sm">
                <div className="mb-4 flex items-start justify-between gap-2">
                    <div>
                        <h3 className="text-xl font-bold">
                            {values.title || (
                                <span className="text-muted-foreground italic">
                                    Untitled Plan
                                </span>
                            )}
                        </h3>
                        {values.description && (
                            <p className="mt-1 text-sm text-muted-foreground">
                                {values.description}
                            </p>
                        )}
                    </div>
                    <span
                        className={`shrink-0 rounded-full px-2.5 py-0.5 text-xs font-medium ${
                            values.active
                                ? "bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400"
                                : "bg-muted text-muted-foreground"
                        }`}
                    >
                        {values.active ? "Active" : "Inactive"}
                    </span>
                </div>

                {primaryPrice ? (
                    <div className="mb-1">
                        <span className="text-3xl font-extrabold">
                            {primaryPrice.split(" ")[0]}
                        </span>
                        <span className="ml-1 text-sm text-muted-foreground">
                            {primaryPrice.split(" ").slice(1).join(" ")}
                        </span>
                    </div>
                ) : (
                    <p className="mb-1 text-sm text-muted-foreground italic">
                        No pricing set
                    </p>
                )}

                {otherPrices.length > 0 && (
                    <div className="mb-4 space-y-0.5">
                        {otherPrices.map((line) => (
                            <p
                                key={line}
                                className="text-xs text-muted-foreground"
                            >
                                {line}
                            </p>
                        ))}
                    </div>
                )}

                {features.length > 0 && (
                    <ul className="mt-4 space-y-2 border-t pt-4">
                        {features.map((feat, i) => (
                            <li
                                key={i}
                                className="flex items-center gap-2 text-sm"
                            >
                                <Check className="size-4 shrink-0 text-green-500" />
                                {feat}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}

export function SubscriptionPlansMutateDrawer({
    open,
    onOpenChange,
    currentRow,
    initialTab = "form",
}: SubscriptionPlansMutateDrawerProps) {
    const isUpdate = !!currentRow;
    const queryClient = useQueryClient();

    const form = useForm<PlanForm>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            title: currentRow?.title ?? "",
            description: currentRow?.description ?? "",
            currencyCode: currentRow?.currencyCode ?? "",
            monthlyPrice:
                currentRow?.monthlyPrice != null
                    ? String(currentRow.monthlyPrice)
                    : "",
            yearlyPrice:
                currentRow?.yearlyPrice != null
                    ? String(currentRow.yearlyPrice)
                    : "",
            lifetimePrice:
                currentRow?.lifetimePrice != null
                    ? String(currentRow.lifetimePrice)
                    : "",
            price: currentRow?.price != null ? String(currentRow.price) : "",
            durationDays:
                currentRow?.durationDays != null
                    ? String(currentRow.durationDays)
                    : "",
            features: (currentRow?.features ?? []).map((f) => ({ value: f })),
            active: currentRow?.active ?? true,
        },
    });

    const { fields, append, remove, move } = useFieldArray({
        control: form.control,
        name: "features",
    });

    const sensors = useSensors(
        useSensor(PointerSensor),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        })
    );

    const handleDragEnd = (event: DragEndEvent) => {
        const { active, over } = event;
        if (over && active.id !== over.id) {
            const oldIndex = fields.findIndex((f) => f.id === active.id);
            const newIndex = fields.findIndex((f) => f.id === over.id);
            move(oldIndex, newIndex);
        }
    };

    const watchedValues = useWatch({ control: form.control });

    useEffect(() => {
        form.reset({
            title: currentRow?.title ?? "",
            description: currentRow?.description ?? "",
            currencyCode: currentRow?.currencyCode ?? "",
            monthlyPrice:
                currentRow?.monthlyPrice != null
                    ? String(currentRow.monthlyPrice)
                    : "",
            yearlyPrice:
                currentRow?.yearlyPrice != null
                    ? String(currentRow.yearlyPrice)
                    : "",
            lifetimePrice:
                currentRow?.lifetimePrice != null
                    ? String(currentRow.lifetimePrice)
                    : "",
            price: currentRow?.price != null ? String(currentRow.price) : "",
            durationDays:
                currentRow?.durationDays != null
                    ? String(currentRow.durationDays)
                    : "",
            features: (currentRow?.features ?? []).map((f) => ({ value: f })),
            active: currentRow?.active ?? true,
        });
    }, [currentRow, form, open]);

    const invalidatePlans = async () => {
        await queryClient.invalidateQueries({
            queryKey: getSubscriptionPlansAsAdminQueryKey(),
        });
    };

    const createMutation = useCreateSubscriptionPlanAsAdmin({
        mutation: {
            onSuccess: async () => {
                await invalidatePlans();
                toast.success("Subscription plan created.");
                onOpenChange(false);
                form.reset();
            },
            onError: handleServerError,
        },
    });

    const updateMutation = useUpdateSubscriptionPlanAsAdmin({
        mutation: {
            onSuccess: async () => {
                await invalidatePlans();
                toast.success("Subscription plan updated.");
                onOpenChange(false);
                form.reset();
            },
            onError: handleServerError,
        },
    });

    const isPending = createMutation.isPending || updateMutation.isPending;

    const onSubmit = (values: PlanForm) => {
        const features = values.features
            .map((f) => f.value.trim())
            .filter(Boolean);

        const payload = {
            title: values.title.trim(),
            description: values.description?.trim() || undefined,
            currencyCode: values.currencyCode.trim(),
            monthlyPrice: toNumericOrUndefined(values.monthlyPrice),
            yearlyPrice: toNumericOrUndefined(values.yearlyPrice),
            lifetimePrice: toNumericOrUndefined(values.lifetimePrice),
            price: toNumericOrUndefined(values.price),
            durationDays: toIntOrUndefined(values.durationDays),
            features: features.length > 0 ? features : undefined,
            active: values.active,
        };

        if (isUpdate && currentRow?.id) {
            updateMutation.mutate({ id: currentRow.id, data: payload });
            return;
        }

        createMutation.mutate({ data: payload });
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
        <Dialog open={open} onOpenChange={handleOpenChange}>
            <DialogContent className="flex max-h-[90vh] flex-col sm:max-w-2xl">
                <DialogHeader>
                    <DialogTitle>
                        {isUpdate ? "Edit" : "Create"} Subscription Plan
                    </DialogTitle>
                    <DialogDescription>
                        {isUpdate
                            ? "Update the subscription plan details."
                            : "Create a new subscription plan."}
                    </DialogDescription>
                </DialogHeader>
                <Tabs
                    key={initialTab}
                    defaultValue={initialTab}
                    className="flex flex-1 flex-col overflow-hidden"
                >
                    <TabsList className="w-fit">
                        <TabsTrigger value="form">Form</TabsTrigger>
                        <TabsTrigger value="preview">Preview</TabsTrigger>
                    </TabsList>
                    <TabsContent value="preview" className="overflow-y-auto">
                        <PlanPreview values={watchedValues as PlanForm} />
                    </TabsContent>
                    <TabsContent
                        value="form"
                        className="flex-1 overflow-hidden"
                    >
                        <Form {...form}>
                            <form
                                id="subscription-plan-form"
                                onSubmit={form.handleSubmit(onSubmit)}
                                className="flex h-full flex-col space-y-5 overflow-y-auto px-1"
                            >
                                <FormField
                                    control={form.control}
                                    name="title"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Title</FormLabel>
                                            <FormControl>
                                                <Input
                                                    {...field}
                                                    disabled={isPending}
                                                    placeholder="Basic Plan"
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
                                                    className="min-h-20"
                                                />
                                            </FormControl>
                                            <FormMessage />
                                        </FormItem>
                                    )}
                                />

                                <FormField
                                    control={form.control}
                                    name="currencyCode"
                                    render={({ field }) => (
                                        <FormItem>
                                            <FormLabel>Currency Code</FormLabel>
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

                                <div className="space-y-2">
                                    <p className="text-sm font-medium">
                                        Pricing
                                    </p>
                                    <FormDescription>
                                        At least one price is required.
                                    </FormDescription>
                                    <div className="grid grid-cols-2 gap-4">
                                        <FormField
                                            control={form.control}
                                            name="monthlyPrice"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>
                                                        Monthly
                                                    </FormLabel>
                                                    <FormControl>
                                                        <Input
                                                            {...field}
                                                            type="number"
                                                            min={0}
                                                            step="0.01"
                                                            disabled={isPending}
                                                            placeholder="0.00"
                                                            value={
                                                                field.value ??
                                                                ""
                                                            }
                                                        />
                                                    </FormControl>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />

                                        <FormField
                                            control={form.control}
                                            name="yearlyPrice"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>
                                                        Yearly
                                                    </FormLabel>
                                                    <FormControl>
                                                        <Input
                                                            {...field}
                                                            type="number"
                                                            min={0}
                                                            step="0.01"
                                                            disabled={isPending}
                                                            placeholder="0.00"
                                                            value={
                                                                field.value ??
                                                                ""
                                                            }
                                                        />
                                                    </FormControl>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />

                                        <FormField
                                            control={form.control}
                                            name="lifetimePrice"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>
                                                        Lifetime
                                                    </FormLabel>
                                                    <FormControl>
                                                        <Input
                                                            {...field}
                                                            type="number"
                                                            min={0}
                                                            step="0.01"
                                                            disabled={isPending}
                                                            placeholder="0.00"
                                                            value={
                                                                field.value ??
                                                                ""
                                                            }
                                                        />
                                                    </FormControl>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />

                                        <FormField
                                            control={form.control}
                                            name="price"
                                            render={({ field }) => (
                                                <FormItem>
                                                    <FormLabel>
                                                        Custom Price
                                                    </FormLabel>
                                                    <FormControl>
                                                        <Input
                                                            {...field}
                                                            type="number"
                                                            min={0}
                                                            step="0.01"
                                                            disabled={isPending}
                                                            placeholder="0.00"
                                                            value={
                                                                field.value ??
                                                                ""
                                                            }
                                                        />
                                                    </FormControl>
                                                    <FormMessage />
                                                </FormItem>
                                            )}
                                        />
                                    </div>

                                    <FormField
                                        control={form.control}
                                        name="durationDays"
                                        render={({ field }) => (
                                            <FormItem>
                                                <FormLabel>
                                                    Custom Duration (days)
                                                </FormLabel>
                                                <FormControl>
                                                    <Input
                                                        {...field}
                                                        type="number"
                                                        min={1}
                                                        step={1}
                                                        disabled={isPending}
                                                        placeholder="e.g. 90"
                                                        value={
                                                            field.value ?? ""
                                                        }
                                                    />
                                                </FormControl>
                                                <FormDescription>
                                                    Required if custom price is
                                                    set.
                                                </FormDescription>
                                                <FormMessage />
                                            </FormItem>
                                        )}
                                    />
                                </div>

                                <div className="space-y-2">
                                    <div className="flex items-center justify-between">
                                        <p className="text-sm font-medium">
                                            Features
                                        </p>
                                        <Button
                                            type="button"
                                            variant="outline"
                                            size="sm"
                                            onClick={() =>
                                                append({ value: "" })
                                            }
                                            disabled={isPending}
                                        >
                                            <Plus className="size-3.5" />
                                            Add Feature
                                        </Button>
                                    </div>
                                    {fields.length === 0 && (
                                        <p className="text-sm text-muted-foreground">
                                            No features added yet.
                                        </p>
                                    )}
                                    <DndContext
                                        sensors={sensors}
                                        collisionDetection={closestCenter}
                                        onDragEnd={handleDragEnd}
                                    >
                                        <SortableContext
                                            items={fields.map((f) => f.id)}
                                            strategy={
                                                verticalListSortingStrategy
                                            }
                                        >
                                            <div className="space-y-2">
                                                {fields.map((field, index) => (
                                                    <FormField
                                                        key={field.id}
                                                        control={form.control}
                                                        name={`features.${index}.value`}
                                                        render={({
                                                            field: inputField,
                                                        }) => (
                                                            <FormItem>
                                                                <SortableFeatureItem
                                                                    id={
                                                                        field.id
                                                                    }
                                                                    index={
                                                                        index
                                                                    }
                                                                    disabled={
                                                                        isPending
                                                                    }
                                                                    onRemove={
                                                                        remove
                                                                    }
                                                                >
                                                                    <FormControl>
                                                                        <Input
                                                                            {...inputField}
                                                                            disabled={
                                                                                isPending
                                                                            }
                                                                            placeholder={`Feature ${index + 1}`}
                                                                        />
                                                                    </FormControl>
                                                                </SortableFeatureItem>
                                                                <FormMessage />
                                                            </FormItem>
                                                        )}
                                                    />
                                                ))}
                                            </div>
                                        </SortableContext>
                                    </DndContext>
                                </div>

                                <FormField
                                    control={form.control}
                                    name="active"
                                    render={({ field }) => (
                                        <FormItem className="flex items-center justify-between rounded-lg border p-4">
                                            <div className="space-y-1">
                                                <FormLabel>Active</FormLabel>
                                                <FormDescription>
                                                    Toggle whether this plan is
                                                    publicly available.
                                                </FormDescription>
                                            </div>
                                            <FormControl>
                                                <Switch
                                                    checked={field.value}
                                                    onCheckedChange={
                                                        field.onChange
                                                    }
                                                    disabled={isPending}
                                                />
                                            </FormControl>
                                        </FormItem>
                                    )}
                                />
                            </form>
                        </Form>
                    </TabsContent>
                </Tabs>
                <DialogFooter className="gap-2">
                    <Button
                        variant="outline"
                        disabled={isPending}
                        onClick={() => handleOpenChange(false)}
                    >
                        Cancel
                    </Button>
                    <Button
                        form="subscription-plan-form"
                        type="submit"
                        disabled={isPending}
                    >
                        {isUpdate ? "Save changes" : "Create plan"}
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
