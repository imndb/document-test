package com.example;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage for documents.
 * Stores document content by S3-like keys (e.g., "documents/invoice-123").
 */
@Component
public class DocumentStore {

    private final Map<String, String> documents = new ConcurrentHashMap<>();

    /**
     * Store a document in memory.
     *
     * @param key the document key (e.g., "documents/invoice-123")
     * @param content the document content
     */
    public void store(String key, String content) {
        documents.put(key, content);
    }

    /**
     * Check if a document exists.
     *
     * @param key the document key
     * @return true if the document exists
     */
    public boolean exists(String key) {
        return documents.containsKey(key);
    }

    /**
     * Retrieve a document by key.
     *
     * @param key the document key
     * @return the document content, or null if not found
     */
    public String retrieve(String key) {
        return documents.get(key);
    }

    /**
     * Clear all stored documents (useful for test cleanup).
     */
    public void clear() {
        documents.clear();
    }

    /**
     * Get the total number of stored documents.
     *
     * @return the number of documents in storage
     */
    public int size() {
        return documents.size();
    }
}

