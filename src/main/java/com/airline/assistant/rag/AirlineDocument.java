package com.airline.assistant.rag;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a document in the knowledge base.
 */
public class AirlineDocument {
    private String content;
    private Map<String, Object> metadata;

    /**
     * Creates a new document with the given content and metadata.
     *
     * @param content  The document content
     * @param metadata The document metadata
     */
    public AirlineDocument(String content, Map<String, Object> metadata) {
        this.content = content;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }

    /**
     * Creates a new document with the given content.
     *
     * @param content The document content
     */
    public AirlineDocument(String content) {
        this(content, new HashMap<>());
    }

    /**
     * Returns the document content.
     *
     * @return The document content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the document content.
     *
     * @param content The document content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the document metadata.
     *
     * @return The document metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Sets the document metadata.
     *
     * @param metadata The document metadata
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Adds a metadata entry.
     *
     * @param key   The metadata key
     * @param value The metadata value
     */
    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }

    /**
     * Returns a metadata value.
     *
     * @param key The metadata key
     * @return The metadata value
     */
    public Object getMetadataValue(String key) {
        return this.metadata.get(key);
    }

    @Override
    public String toString() {
        return "AirlineDocument{" +
                "content='" + (content.length() > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}