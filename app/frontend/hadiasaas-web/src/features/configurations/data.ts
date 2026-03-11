import { appConfigurationCategoryEnum } from "@api-client";

export const configurationCategories = [
    { label: "Currency", value: appConfigurationCategoryEnum.CURRENCY },
    { label: "Two-Factor", value: appConfigurationCategoryEnum.TWO_FACTOR },
    {
        label: "Payment Mode",
        value: appConfigurationCategoryEnum.PAYMENT_MODE,
    },
    { label: "Storage", value: appConfigurationCategoryEnum.STORAGE },
    { label: "Tax", value: appConfigurationCategoryEnum.TAX },
    { label: "Enterprise", value: appConfigurationCategoryEnum.ENTERPRISE },
] as const;

export const activeOptions = [
    { label: "Active", value: "true" },
    { label: "Inactive", value: "false" },
] as const;
