package com.example;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the request body sent to the downstream API.
 * Adjust fields to match your actual API contract.
 */
public record CreateDocumentRequest(

        @JsonProperty("documentId")
        String documentId,

        @JsonProperty("type")
        String type,

        @JsonProperty("content")
        String content
) {}

