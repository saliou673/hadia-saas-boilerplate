import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { showSubmittedData } from "@/lib/show-submitted-data";
import { Button } from "@/components/ui/button";
import {
    Dialog,
    DialogClose,
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
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { SelectDropdown } from "@/components/select-dropdown";
import { type Task } from "../data/schema";

type TasksFormDialogProps = {
    open: boolean;
    onOpenChange: (open: boolean) => void;
    currentRow?: Task;
};

const formSchema = z.object({
    title: z.string().min(1, "Title is required."),
    status: z.string().min(1, "Please select a status."),
    label: z.string().min(1, "Please select a label."),
    priority: z.string().min(1, "Please choose a priority."),
});
type TaskForm = z.infer<typeof formSchema>;

export function TasksFormDialog({
    open,
    onOpenChange,
    currentRow,
}: TasksFormDialogProps) {
    const isUpdate = !!currentRow;

    const form = useForm<TaskForm>({
        resolver: zodResolver(formSchema),
        defaultValues: currentRow ?? {
            title: "",
            status: "",
            label: "",
            priority: "",
        },
    });

    const onSubmit = (data: TaskForm) => {
        onOpenChange(false);
        form.reset();
        showSubmittedData(data);
    };

    return (
        <Dialog
            open={open}
            onOpenChange={(v) => {
                onOpenChange(v);
                form.reset();
            }}
        >
            <DialogContent className="sm:max-w-lg">
                <DialogHeader>
                    <DialogTitle>
                        {isUpdate ? "Update" : "Create"} Task
                    </DialogTitle>
                    <DialogDescription>
                        {isUpdate
                            ? "Update the task by providing necessary info."
                            : "Add a new task by providing necessary info."}{" "}
                        Click save when you&apos;re done.
                    </DialogDescription>
                </DialogHeader>
                <Form {...form}>
                    <form
                        id="tasks-form"
                        onSubmit={form.handleSubmit(onSubmit)}
                        className="max-h-[60vh] space-y-6 overflow-y-auto px-1"
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
                                            placeholder="Enter a title"
                                        />
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="status"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Status</FormLabel>
                                    <SelectDropdown
                                        defaultValue={field.value}
                                        onValueChange={field.onChange}
                                        placeholder="Select dropdown"
                                        items={[
                                            {
                                                label: "In Progress",
                                                value: "in progress",
                                            },
                                            {
                                                label: "Backlog",
                                                value: "backlog",
                                            },
                                            { label: "Todo", value: "todo" },
                                            {
                                                label: "Canceled",
                                                value: "canceled",
                                            },
                                            { label: "Done", value: "done" },
                                        ]}
                                    />
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="label"
                            render={({ field }) => (
                                <FormItem className="relative">
                                    <FormLabel>Label</FormLabel>
                                    <FormControl>
                                        <RadioGroup
                                            onValueChange={field.onChange}
                                            defaultValue={field.value}
                                            className="flex flex-col space-y-1"
                                        >
                                            <FormItem className="flex items-center">
                                                <FormControl>
                                                    <RadioGroupItem value="documentation" />
                                                </FormControl>
                                                <FormLabel className="font-normal">
                                                    Documentation
                                                </FormLabel>
                                            </FormItem>
                                            <FormItem className="flex items-center">
                                                <FormControl>
                                                    <RadioGroupItem value="feature" />
                                                </FormControl>
                                                <FormLabel className="font-normal">
                                                    Feature
                                                </FormLabel>
                                            </FormItem>
                                            <FormItem className="flex items-center">
                                                <FormControl>
                                                    <RadioGroupItem value="bug" />
                                                </FormControl>
                                                <FormLabel className="font-normal">
                                                    Bug
                                                </FormLabel>
                                            </FormItem>
                                        </RadioGroup>
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                        <FormField
                            control={form.control}
                            name="priority"
                            render={({ field }) => (
                                <FormItem className="relative">
                                    <FormLabel>Priority</FormLabel>
                                    <FormControl>
                                        <RadioGroup
                                            onValueChange={field.onChange}
                                            defaultValue={field.value}
                                            className="flex flex-col space-y-1"
                                        >
                                            <FormItem className="flex items-center">
                                                <FormControl>
                                                    <RadioGroupItem value="high" />
                                                </FormControl>
                                                <FormLabel className="font-normal">
                                                    High
                                                </FormLabel>
                                            </FormItem>
                                            <FormItem className="flex items-center">
                                                <FormControl>
                                                    <RadioGroupItem value="medium" />
                                                </FormControl>
                                                <FormLabel className="font-normal">
                                                    Medium
                                                </FormLabel>
                                            </FormItem>
                                            <FormItem className="flex items-center">
                                                <FormControl>
                                                    <RadioGroupItem value="low" />
                                                </FormControl>
                                                <FormLabel className="font-normal">
                                                    Low
                                                </FormLabel>
                                            </FormItem>
                                        </RadioGroup>
                                    </FormControl>
                                    <FormMessage />
                                </FormItem>
                            )}
                        />
                    </form>
                </Form>
                <DialogFooter className="gap-2">
                    <DialogClose asChild>
                        <Button variant="outline">Close</Button>
                    </DialogClose>
                    <Button form="tasks-form" type="submit">
                        Save changes
                    </Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
