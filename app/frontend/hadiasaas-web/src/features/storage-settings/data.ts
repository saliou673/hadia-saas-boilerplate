export const providerOptions = [
    { label: "Local", value: "LOCAL" },
    { label: "AWS S3", value: "AWS_S3" },
    { label: "Azure Blob", value: "AZURE_BLOB" },
    { label: "Google Cloud Storage", value: "GCS" },
];

export const activeOptions = [
    { label: "Active", value: "true" },
    { label: "Inactive", value: "false" },
];

export function formatProvider(provider?: string): string {
    switch (provider) {
        case "LOCAL":
            return "Local";
        case "AWS_S3":
            return "AWS S3";
        case "AZURE_BLOB":
            return "Azure Blob";
        case "GCS":
            return "Google Cloud Storage";
        default:
            return provider ?? "N/A";
    }
}
