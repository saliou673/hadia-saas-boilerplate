package com.maitrisetcf.domain.ports.out;

import com.maitrisetcf.domain.models.contact.ContactForm;
import com.maitrisetcf.domain.models.user.User;

/**
 * Outbound port for sending notifications to users.
 * <p>
 * This interface defines operations that the domain can invoke to notify a user,
 * without knowing the delivery method (Email, SMS, Push, etc.).
 * <p>
 * Concrete implementations (adapters) are responsible for actually sending the notifications.
 * <p>
 * Example implementations: {@link EmailNotificationAdapterPort},
 * SmsNotificationAdapter, PushNotificationAdapter.
 */
public interface NotificationSenderPort {

    /**
     * Sends an account activation notification to the user.
     * <p>
     * This notification is triggered when a user is created or needs to activate their account.
     *
     * @param user the user to notify
     */
    void sendActivationNotification(User user);

    /**
     * Sends a welcome/creation notification to the user.
     * <p>
     * This notification can include a welcome message, initial instructions,
     * or any other relevant information after account creation.
     *
     * @param user the user to notify
     */
    void sendCreationNotification(User user);

    /**
     * Sends a password reset notification to the user.
     * <p>
     * This notification is triggered when a user requests to reset their password.
     *
     * @param user the user to notify
     */
    void sendPasswordResetNotification(User user);

    /**
     * Sends an account deletion notification explaining the recovery grace period.
     *
     * @param user the user to notify
     */
    void sendAccountDeletionNotification(User user);

    /**
     * Sends an invitation notification to a managed user created by an administrator.
     * <p>
     * This notification contains a link the user must follow to set their password
     * and activate their account.
     *
     * @param user the managed user to invite
     */
    void sendManagedUserInvitationNotification(User user);

    /**
     * Sends a notification to the user when a subscription payment fails.
     *
     * @param user      the user to notify
     * @param planTitle the title of the subscription plan whose payment failed
     */
    void sendSubscriptionPaymentFailedNotification(User user, String planTitle);

    /**
     * Sends the contact form content to the configured support/admin email address.
     *
     * @param contactForm the submitted contact form
     */
    void sendContactFormToAdmin(ContactForm contactForm);

    /**
     * Sends an acknowledgement email to the person who submitted the contact form,
     * including a copy of their submission.
     *
     * @param contactForm the submitted contact form
     */
    void sendContactFormConfirmationToUser(ContactForm contactForm);
}
