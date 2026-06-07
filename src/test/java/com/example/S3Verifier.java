package com.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Polls in-memory DocumentStore until a given key appears or the timeout is reached.
 *
 * Uses Awaitility so tests don't need manual Thread.sleep() calls.
 * Timeout and polling interval are configurable via application-test.yaml.
 */
@Component
public class S3Verifier {

    private final DocumentStore documentStore;
    private final long timeoutSeconds;
    private final long intervalSeconds;

    public S3Verifier(
            DocumentStore documentStore,
            @Value("${s3.poll.timeout-seconds:30}") long timeoutSeconds,
            @Value("${s3.poll.interval-seconds:2}") long intervalSeconds) {
        this.documentStore = documentStore;
        this.timeoutSeconds = timeoutSeconds;
        this.intervalSeconds = intervalSeconds;
    }

    /**
     * Blocks until the given document key exists in the in-memory store,
     * or fails the test with a clear message if the timeout is exceeded.
     *
     * @param s3Key e.g. "documents/invoice-123"
     */
    public void assertObjectExistsEventually(String s3Key) {
        await()
                .atMost(Duration.ofSeconds(timeoutSeconds))
                .pollInterval(Duration.ofSeconds(intervalSeconds))
                .untilAsserted(() -> assertObjectExists(s3Key));
    }

    /**
     * Immediately checks if the key exists in the in-memory store (no polling).
     * Useful for asserting the object is NOT there, or after a known delay.
     *
     * @param s3Key e.g. "documents/invoice-123"
     */
    public void assertObjectExists(String s3Key) {
        assertThat(documentStore.exists(s3Key))
                .as("Document key '%s' should exist in storage", s3Key)
                .isTrue();
    }

    /**
     * Asserts the key does NOT exist in storage (e.g. for negative test cases).
     *
     * @param s3Key e.g. "documents/invoice-123"
     */
    public void assertObjectDoesNotExist(String s3Key) {
        assertThat(documentStore.exists(s3Key))
                .as("Document key '%s' should NOT exist in storage", s3Key)
                .isFalse();
    }
}
