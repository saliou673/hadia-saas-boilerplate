package com.hadiasaas.integration.controller;

import com.hadiasaas.domain.models.contact.ContactForm;
import com.hadiasaas.domain.ports.out.NotificationSenderPort;
import com.hadiasaas.infrastructure.adapter.in.rest.controller.requests.ContactFormRequest;
import com.hadiasaas.integration.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
class ContactFormControllerTest extends IntegrationTest {

    private static final String API = "/api/v1/contact";

    @MockitoBean
    private NotificationSenderPort notificationSenderPort;

    @Test
    void shouldSubmitContactFormSuccessfully() throws Exception {
        ContactFormRequest request = new ContactFormRequest(
                "John Doe",
                "john.doe@example.com",
                "Question about my stamp",
                "I would like to know the status of my order."
        );

        post(API, request, status().isNoContent());

        verify(notificationSenderPort).sendContactFormToAdmin(any(ContactForm.class));
        verify(notificationSenderPort).sendContactFormConfirmationToUser(any(ContactForm.class));
    }

    @Test
    void shouldBePubliclyAccessibleWithoutAuthentication() throws Exception {
        // No createUser() call — endpoint must be reachable without a JWT
        ContactFormRequest request = new ContactFormRequest(
                "Jane Doe",
                "jane.doe@example.com",
                "Hello",
                "Just a quick question."
        );

        post(API, request, status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        ContactFormRequest request = new ContactFormRequest(
                "",
                "john@example.com",
                "Subject",
                "A message."
        );

        post(API, request, status().isBadRequest());

        verify(notificationSenderPort, never()).sendContactFormToAdmin(any());
        verify(notificationSenderPort, never()).sendContactFormConfirmationToUser(any());
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsBlank() throws Exception {
        ContactFormRequest request = new ContactFormRequest(
                "John Doe",
                "",
                "Subject",
                "A message."
        );

        post(API, request, status().isBadRequest());

        verify(notificationSenderPort, never()).sendContactFormToAdmin(any());
        verify(notificationSenderPort, never()).sendContactFormConfirmationToUser(any());
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsInvalid() throws Exception {
        ContactFormRequest request = new ContactFormRequest(
                "John Doe",
                "not-a-valid-email",
                "Subject",
                "A message."
        );

        post(API, request, status().isBadRequest());

        verify(notificationSenderPort, never()).sendContactFormToAdmin(any());
        verify(notificationSenderPort, never()).sendContactFormConfirmationToUser(any());
    }

    @Test
    void shouldReturnBadRequestWhenSubjectIsBlank() throws Exception {
        ContactFormRequest request = new ContactFormRequest(
                "John Doe",
                "john@example.com",
                "",
                "A message."
        );

        post(API, request, status().isBadRequest());

        verify(notificationSenderPort, never()).sendContactFormToAdmin(any());
        verify(notificationSenderPort, never()).sendContactFormConfirmationToUser(any());
    }

    @Test
    void shouldReturnBadRequestWhenMessageIsBlank() throws Exception {
        ContactFormRequest request = new ContactFormRequest(
                "John Doe",
                "john@example.com",
                "Subject",
                ""
        );

        post(API, request, status().isBadRequest());

        verify(notificationSenderPort, never()).sendContactFormToAdmin(any());
        verify(notificationSenderPort, never()).sendContactFormConfirmationToUser(any());
    }

    @Test
    void shouldReturnBadRequestWhenBodyIsMissing() throws Exception {
        post(API, null, status().isBadRequest());

        verify(notificationSenderPort, never()).sendContactFormToAdmin(any());
        verify(notificationSenderPort, never()).sendContactFormConfirmationToUser(any());
    }
}
