package com.maitrisetcf.infrastructure.adapter.in.rest.controller.dto;

import java.time.LocalDateTime;
import java.util.Map;


public record ValidationErrorResponseDTO(LocalDateTime timestamp,
                                         int status,
                                         String message,
                                         Map<String, String> errors
) {
}
