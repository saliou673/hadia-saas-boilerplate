import {
    userDetailsGenderEnum,
    userDetailsStatusEnum,
    type UserDetailsGenderEnumKey,
    type UserDetailsStatusEnumKey,
} from "@api-client";
import { type UserGender, type UserStatus } from "./schema";

export const userStatusLabels: Record<UserStatus, string> = {
    [userDetailsStatusEnum.NOT_ACTIVATED]: "Not activated",
    [userDetailsStatusEnum.ACTIVATED]: "Activated",
    [userDetailsStatusEnum.DEACTIVATED]: "Deactivated",
    [userDetailsStatusEnum.LOCKED]: "Locked",
    [userDetailsStatusEnum.BANNED]: "Banned",
};

export const userStatusBadgeClassNames: Record<UserStatus, string> = {
    [userDetailsStatusEnum.NOT_ACTIVATED]:
        "bg-sky-200/40 text-sky-900 border-sky-300 dark:text-sky-100",
    [userDetailsStatusEnum.ACTIVATED]:
        "bg-teal-100/30 text-teal-900 border-teal-200 dark:text-teal-200",
    [userDetailsStatusEnum.DEACTIVATED]: "bg-neutral-300/40 border-neutral-300",
    [userDetailsStatusEnum.LOCKED]:
        "bg-amber-100/50 text-amber-900 border-amber-300 dark:text-amber-200",
    [userDetailsStatusEnum.BANNED]:
        "bg-destructive/10 text-destructive border-destructive/20 dark:bg-destructive/50 dark:text-primary",
};

export const userStatusOptions: {
    label: string;
    value: UserDetailsStatusEnumKey;
}[] = Object.values(userDetailsStatusEnum).map((value) => ({
    label: userStatusLabels[value],
    value,
}));

export const genderLabels: Record<UserGender, string> = {
    [userDetailsGenderEnum.MALE]: "Male",
    [userDetailsGenderEnum.FEMALE]: "Female",
};

export const genderOptions: {
    label: string;
    value: UserDetailsGenderEnumKey;
}[] = Object.values(userDetailsGenderEnum).map((value) => ({
    label: genderLabels[value],
    value,
}));
