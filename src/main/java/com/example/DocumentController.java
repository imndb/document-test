package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API controller for document operations.
 * Handles document creation and stores documents in memory.
 */
@RestController
@RequestMapping("/api/docs")
public class DocumentController {

    private final DocumentStore documentStore;

    @Autowired
    public DocumentController(DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    /**
     * Create a new document and store it in memory.
     * 
     * The storage key is determined by the document type:
     * - "invoice" → stored under "documents/{documentId}"
     * - "contract" → stored under "contracts/{documentId}"
     * - Other types → stored under "documents/{documentId}"
     *
     * @param request the document creation request
     * @return 201 Created if successful
     */
    @PostMapping
    public ResponseEntity<Void> createDocument(@RequestBody CreateDocumentRequest request) {
        // Determine storage key prefix based on document type
        String keyPrefix = switch (request.type()) {
            case "contract" -> "contracts";
            default -> "documents";
        };

        String storageKey = keyPrefix + "/" + request.documentId();

        // Store the document content in memory
        documentStore.store(storageKey, request.content());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Retrieve a document by type and document ID.
     *
     * @param type the document type (e.g., "invoice", "contract")
     * @param documentId the document ID
     * @return 200 OK with document content, or 404 Not Found
     */
    @GetMapping("/{type}/{documentId}")
    public ResponseEntity<String> getDocument(
            @PathVariable String type,
            @PathVariable String documentId) {
        
        String storageKey = type + "/" + documentId;
        String content = documentStore.retrieve(storageKey);
        
        if (content == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(content);
    }
}
