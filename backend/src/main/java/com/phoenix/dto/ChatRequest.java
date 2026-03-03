package com.phoenix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatRequest(

        @NotBlank(message = "Question must not be blank")
        @Size(max = 2000, message = "Question must not exceed 2000 characters")
        String question,

        /**
         * Optional document ID to scope retrieval to a single document.
         * When null, retrieval searches across all indexed documents.
         */
        String documentId
) {}
