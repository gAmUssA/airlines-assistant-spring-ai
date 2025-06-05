package com.airline.assistant.rag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple vector store implementation for storing and retrieving documents.
 * This is a simplified version that uses basic text similarity instead of actual vector embeddings.
 */
@Component
public class SimpleVectorStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleVectorStore.class);
    
    private final List<AirlineDocument> documents = new ArrayList<>();
    
    /**
     * Adds a document to the vector store.
     *
     * @param document The document to add
     */
    public void addDocument(AirlineDocument document) {
        documents.add(document);
        LOGGER.debug("Added document to vector store: {}", document);
    }
    
    /**
     * Adds multiple documents to the vector store.
     *
     * @param documents The documents to add
     */
    public void addDocuments(List<AirlineDocument> documents) {
        this.documents.addAll(documents);
        LOGGER.debug("Added {} documents to vector store", documents.size());
    }
    
    /**
     * Performs a similarity search for the given query.
     *
     * @param query The query to search for
     * @param k     The number of results to return
     * @return The top k most similar documents
     */
    public List<AirlineDocument> similaritySearch(String query, int k) {
        LOGGER.debug("Performing similarity search for query: {}", query);
        
        // This is a simplified implementation that uses basic text similarity
        // In a real implementation, this would use vector embeddings and cosine similarity
        return documents.stream()
                .map(doc -> new DocumentWithScore(doc, calculateSimilarity(query, doc.getContent())))
                .sorted(Comparator.comparing(DocumentWithScore::score).reversed())
                .limit(k)
                .map(DocumentWithScore::document)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates a simple similarity score between the query and the document content.
     * This is a very basic implementation that counts the number of query terms in the document.
     *
     * @param query   The query
     * @param content The document content
     * @return A similarity score
     */
    private double calculateSimilarity(String query, String content) {
        // Convert to lowercase for case-insensitive matching
        String lowerQuery = query.toLowerCase();
        String lowerContent = content.toLowerCase();
        
        // Split query into terms
        String[] queryTerms = lowerQuery.split("\\s+");
        
        // Count how many query terms appear in the content
        int matchCount = 0;
        for (String term : queryTerms) {
            if (term.length() > 3 && lowerContent.contains(term)) { // Only consider terms longer than 3 characters
                matchCount++;
            }
        }
        
        // Calculate a simple similarity score
        return (double) matchCount / queryTerms.length;
    }
    
    /**
     * Returns all documents in the vector store.
     *
     * @return All documents
     */
    public List<AirlineDocument> getAllDocuments() {
        return new ArrayList<>(documents);
    }
    
    /**
     * Clears all documents from the vector store.
     */
    public void clear() {
        documents.clear();
        LOGGER.debug("Cleared all documents from vector store");
    }
    
    /**
     * Returns the number of documents in the vector store.
     *
     * @return The number of documents
     */
    public int size() {
        return documents.size();
    }
    
    /**
     * A record to hold a document and its similarity score.
     */
    private record DocumentWithScore(AirlineDocument document, double score) {}
}