import { ContentSection } from "../components/content-section";
import { ProfileForm } from "./profile-form";

export function SettingsProfile() {
    return (
        <ContentSection
            title="Profile"
            desc="Review the profile information currently attached to your account."
        >
            <ProfileForm />
        </ContentSection>
    );
}
