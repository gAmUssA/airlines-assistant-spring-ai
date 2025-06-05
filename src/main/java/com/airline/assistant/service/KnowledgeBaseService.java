package com.airline.assistant.service;

import com.airline.assistant.rag.AirlineDocument;
import com.airline.assistant.rag.SimpleVectorStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing the knowledge base for the airline loyalty program assistant.
 * Handles document loading, processing, and vector store operations.
 */
@Service
public class KnowledgeBaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeBaseService.class);
  private static final String KNOWLEDGE_BASE_PATH = "classpath:knowledge-base/";
  private static final int CHUNK_SIZE = 1000; // Characters per chunk
  private static final int CHUNK_OVERLAP = 200; // Overlap between chunks

  private final ResourceLoader resourceLoader;
  private final SimpleVectorStore vectorStore;

  @Autowired
  public KnowledgeBaseService(ResourceLoader resourceLoader, SimpleVectorStore vectorStore) {
    this.resourceLoader = resourceLoader;
    this.vectorStore = vectorStore;

    // Initialize the knowledge base
    try {
      initializeKnowledgeBase();
    } catch (IOException e) {
      LOGGER.error("Failed to initialize knowledge base", e);
    }
  }

  /**
   * Initializes the knowledge base by loading and processing documents.
   */
  public void initializeKnowledgeBase() throws IOException {
    LOGGER.info("Initializing knowledge base...");

    List<AirlineDocument> documents = loadDocuments();
    List<AirlineDocument> processedDocuments = processDocuments(documents);

    // Add documents to vector store
    vectorStore.addDocuments(processedDocuments);

    LOGGER.info("Knowledge base initialized with {} documents", processedDocuments.size());
  }

  /**
   * Loads documents from the knowledge base directory.
   */
  private List<AirlineDocument> loadDocuments() throws IOException {
    LOGGER.info("Loading documents from knowledge base...");
    List<AirlineDocument> documents = new ArrayList<>();

    Resource resource = resourceLoader.getResource(KNOWLEDGE_BASE_PATH);
    Path knowledgeBasePath = Paths.get(resource.getURI());

    // List all markdown files in the knowledge base directory
    List<Path> markdownFiles = Files.list(knowledgeBasePath)
        .filter(path -> path.toString().endsWith(".md"))
        // Skip README.md as it's not part of the actual knowledge base content
        .filter(path -> !path.getFileName().toString().equals("README.md"))
        .collect(Collectors.toList());

    for (Path file : markdownFiles) {
      String content = Files.readString(file);
      String filename = file.getFileName().toString();

      // Create metadata for the document
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("source", filename);
      metadata.put("filename", filename);
      metadata.put("type", "markdown");

      // Create a document with the content and metadata
      AirlineDocument document = new AirlineDocument(content, metadata);
      documents.add(document);

      LOGGER.info("Loaded document: {}", filename);
    }

    return documents;
  }

  /**
   * Processes documents by chunking them into smaller pieces.
   */
  private List<AirlineDocument> processDocuments(List<AirlineDocument> documents) {
    LOGGER.info("Processing {} documents...", documents.size());
    List<AirlineDocument> processedDocuments = new ArrayList<>();

    for (AirlineDocument document : documents) {
      String content = document.getContent();
      Map<String, Object> metadata = document.getMetadata();

      // Split content into chunks
      List<String> chunks = chunkText(content);

      for (int i = 0; i < chunks.size(); i++) {
        String chunk = chunks.get(i);

        // Create metadata for the chunk
        Map<String, Object> chunkMetadata = new HashMap<>(metadata);
        chunkMetadata.put("chunk", i);

        // Create a document with the chunk and metadata
        AirlineDocument chunkDocument = new AirlineDocument(chunk, chunkMetadata);
        processedDocuments.add(chunkDocument);
      }
    }

    LOGGER.info("Processed documents into {} chunks", processedDocuments.size());
    return processedDocuments;
  }

  /**
   * Chunks text into smaller pieces with overlap.
   */
  private List<String> chunkText(String text) {
    List<String> chunks = new ArrayList<>();

    // Simple chunking by character count with overlap
    int textLength = text.length();
    int start = 0;
    int previousStart = -1; // Track previous start position to detect lack of progress

    // Limit the maximum number of chunks to prevent excessive memory usage
    int maxChunks = 100;

    while (start < textLength && chunks.size() < maxChunks) {
      // Ensure we're making progress
      if (start == previousStart) {
        // If we're stuck at the same position, force progress
        start += 1;
        continue;
      }
      previousStart = start;

      int end = Math.min(start + CHUNK_SIZE, textLength);

      // If we're not at the end of the text, try to find a natural break point
      if (end < textLength) {
        // Look for a paragraph break or period within the overlap window
        int breakPoint = text.lastIndexOf("\n\n", end);
        if (breakPoint > start && breakPoint > end - CHUNK_OVERLAP) {
          end = breakPoint;
        } else {
          breakPoint = text.lastIndexOf(". ", end);
          if (breakPoint > start && breakPoint > end - CHUNK_OVERLAP) {
            end = breakPoint + 1; // Include the period
          }
        }
      }

      // Ensure we're making progress - if end <= start, force progress
      if (end <= start) {
        end = Math.min(start + 1, textLength);
      }

      chunks.add(text.substring(start, end).trim());

      // Move start position forward, accounting for overlap
      start = end - CHUNK_OVERLAP;
      if (start < 0 || start >= textLength) {
        // Ensure we don't go out of bounds or get stuck
        break;
      }
    }

    // Log warning if we hit the chunk limit
    if (chunks.size() >= maxChunks) {
      LOGGER.warn("Hit maximum chunk limit ({}) for text of length {}. Some content may be truncated.",
                  maxChunks, textLength);
    }

    return chunks;
  }

  /**
   * Performs a similarity search in the vector store.
   */
  public List<AirlineDocument> similaritySearch(String query, int k) {
    LOGGER.info("Performing similarity search for query: {}", query);
    return vectorStore.similaritySearch(query, k);
  }

  /**
   * Returns the vector store.
   */
  public SimpleVectorStore getVectorStore() {
    return vectorStore;
  }

  /**
   * Retrieves relevant context for a given query.
   *
   * @param query The query to retrieve context for
   * @return A string containing the relevant context
   */
  public String getRelevantContext(String query) {
    List<AirlineDocument> relevantDocuments = similaritySearch(query, 3);

    if (relevantDocuments.isEmpty()) {
      return "";
    }

    StringBuilder contextBuilder = new StringBuilder();
    contextBuilder.append("Here is some relevant information that might help answer the question:\n\n");

    for (AirlineDocument doc : relevantDocuments) {
      String source = (String) doc.getMetadataValue("source");
      contextBuilder.append("From ").append(source).append(":\n");
      contextBuilder.append(doc.getContent()).append("\n\n");
    }

    return contextBuilder.toString();
  }
}
