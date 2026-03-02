package com.maitrisetcf.infrastructure.adapter.in.rest.controller;

import com.maitrisetcf.domain.models.contact.ContactForm;
import com.maitrisetcf.domain.ports.in.ContactFormUseCase;
import com.maitrisetcf.infrastructure.adapter.in.rest.controller.requests.ContactFormRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact")
@RequiredArgsConstructor
public class ContactFormController {

    private final ContactFormUseCase contactFormUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void submitContactForm(@Valid @RequestBody ContactFormRequest request) {
        contactFormUseCase.submit(new ContactForm(
                request.name(),
                request.email(),
                request.subject(),
                request.message()
        ));
    }
}
