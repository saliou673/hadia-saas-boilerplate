package com.maitrisetcf.application;

import com.maitrisetcf.domain.models.contact.ContactForm;
import com.maitrisetcf.domain.ports.in.ContactFormUseCase;
import com.maitrisetcf.domain.ports.out.NotificationSenderPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactFormService implements ContactFormUseCase {

    private final NotificationSenderPort notificationSenderPort;

    @Override
    public void submit(ContactForm contactForm) {
        notificationSenderPort.sendContactFormToAdmin(contactForm);
        notificationSenderPort.sendContactFormConfirmationToUser(contactForm);
    }
}
