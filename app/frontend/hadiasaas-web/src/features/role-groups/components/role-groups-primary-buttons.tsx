import { ShieldPlus } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useRoleGroups } from "./role-groups-provider";

type RoleGroupsPrimaryButtonsProps = {
    canManageRoleGroups: boolean;
};

export function RoleGroupsPrimaryButtons({
    canManageRoleGroups,
}: RoleGroupsPrimaryButtonsProps) {
    const { setOpen } = useRoleGroups();

    if (!canManageRoleGroups) {
        return null;
    }

    return (
        <div className="flex gap-2">
            <Button className="space-x-1" onClick={() => setOpen("add")}>
                <span>Add role group</span> <ShieldPlus size={18} />
            </Button>
        </div>
    );
}
