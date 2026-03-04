package com.hadiasaas.application;

import com.hadiasaas.domain.models.contact.ContactForm;
import com.hadiasaas.domain.ports.in.ContactFormUseCase;
import com.hadiasaas.domain.ports.out.NotificationSenderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
/** Application service implementing {@link ContactFormUseCase}: dispatches contact form submissions via notifications. */
public class ContactFormService implements ContactFormUseCase {

    private final NotificationSenderPort notificationSenderPort;

    @Override
    public void submit(ContactForm contactForm) {
        notificationSenderPort.sendContactFormToAdmin(contactForm);
        notificationSenderPort.sendContactFormConfirmationToUser(contactForm);
    }
}
