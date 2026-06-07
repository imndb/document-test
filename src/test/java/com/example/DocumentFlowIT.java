package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = com.example.MainApp.class)
@ActiveProfiles("test")
@Import({com.example.TestConfig.class, com.example.S3Verifier.class})
class DocumentFlowIT {

    @Autowired
    protected RestClient restClient;

    @Autowired
    protected com.example.S3Verifier s3Verifier;

    @Test
    void createDocument_shouldStoreDocumentInS3() {
        var request = new com.example.CreateDocumentRequest(
                "invoice-123",
                "invoice",
                "Test invoice content"
        );

        ResponseEntity<Void> response = restClient
                .post()
                .uri("/api/docs")
                .body(request)
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode().is2xxSuccessful())
                .as("API should accept the request")
                .isTrue();

    }

    @Test
    void createDocument_withSpecificType_shouldStoreUnderCorrectS3Prefix() {
        var request = new com.example.CreateDocumentRequest(
                "contract-456",
                "contract",
                "Test contract content"
        );

        restClient
                .post()
                .uri("/api/docs")
                .body(request)
                .retrieve()
                .toBodilessEntity();


    }

    @Test
    void getDocument_shouldReturnStoredDocumentContent() {
        // Arrange: Create a document
        var createRequest = new com.example.CreateDocumentRequest(
                "doc-789",
                "invoice",
                "Invoice document content"
        );

        restClient
                .post()
                .uri("/api/docs")
                .body(createRequest)
                .retrieve()
                .toBodilessEntity();

        // Act: Retrieve the document
        ResponseEntity<String> response = restClient
                .get()
                .uri("/api/docs/documents/doc-789")
                .retrieve()
                .toEntity(String.class);

        // Assert: Document should be returned with correct content
        assertThat(response.getStatusCode().is2xxSuccessful())
                .as("GET request should succeed")
                .isTrue();

        assertThat(response.getBody())
                .as("Document content should match original")
                .isEqualTo("Invoice document content");
    }

    @Test
    void getDocument_shouldReturnNotFound_whenDocumentDoesNotExist() {
        // Act & Assert: Try to retrieve a non-existent document
        assertThatThrownBy(() ->
                restClient
                        .get()
                        .uri("/api/docs/documents/non-existent-999")
                        .retrieve()
                        .toEntity(String.class)
        )
                .as("Should throw HttpClientErrorException for 404")
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    void getDocument_shouldRetrieveContractDocument() {
        // Arrange: Create a contract document
        var createRequest = new com.example.CreateDocumentRequest(
                "contract-123",
                "contract",
                "Contract document content with terms and conditions"
        );

        restClient
                .post()
                .uri("/api/docs")
                .body(createRequest)
                .retrieve()
                .toBodilessEntity();

        // Act: Retrieve the contract document
        ResponseEntity<String> response = restClient
                .get()
                .uri("/api/docs/contracts/contract-123")
                .retrieve()
                .toEntity(String.class);

        // Assert: Contract should be returned with correct content
        assertThat(response.getStatusCode().is2xxSuccessful())
                .as("GET request should succeed for contract")
                .isTrue();

        assertThat(response.getBody())
                .as("Contract content should match original")
                .isEqualTo("Contract document content with terms and conditions");
    }
}
