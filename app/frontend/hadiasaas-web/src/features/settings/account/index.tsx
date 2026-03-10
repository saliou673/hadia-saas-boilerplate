import { ContentSection } from "../components/content-section";
import { AccountForm } from "./account-form";

export function SettingsAccount() {
    return (
        <ContentSection
            title="Account"
            desc="Update the personal and account information stored for your user."
        >
            <AccountForm />
        </ContentSection>
    );
}
