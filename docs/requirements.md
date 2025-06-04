# Spring AI Airline Loyalty Assistant: Product Requirements Document

## Executive Summary

This Product Requirements Document defines the development of a comprehensive Spring AI-based Airline Loyalty Assistant demo application. The application serves as a practical showcase of Spring AI 1.0.0 stable capabilities, featuring progressive enhancement from basic OpenAI integration to advanced RAG implementations with local model support. Built with Java 21, Spring Boot 3.5.0, and modern patterns, the application demonstrates simple yet effective AI integration while providing practical value for airline loyalty program inquiries.

## Product Vision and Objectives

### Primary Goals
- **Demonstrate Spring AI 1.0.0 Features**: Showcase core Spring AI capabilities through a practical use case
- **Simple & Effective Architecture**: Implement clean, maintainable patterns for AI-powered web applications
- **Progressive Enhancement**: Build complexity incrementally from basic chat to advanced RAG systems
- **Developer Education**: Serve as a clear reference implementation for Spring AI adoption

### Success Metrics
- **Code Quality**: Clean, idiomatic code following Spring Boot best practices with minimal abstractions
- **Feature Completeness**: All five progressive enhancement phases implemented and working
- **Performance Benchmarks**: Sub-500ms response times for basic queries, sub-2s for RAG queries
- **Developer Experience**: Clear setup documentation enabling quick start within 15 minutes

## Technical Architecture

### Core Technology Stack
- **Language**: Java 21 (LTS) with modern language features
- **Framework**: Spring Boot 3.5.0 with Spring AI 1.0.0 (stable)
- **Build System**: Gradle 8.5+ with Kotlin DSL build scripts
- **Web Layer**: Spring Web MVC with embedded Tomcat, static HTML/CSS/JS frontend
- **Vector Storage**: SimpleVectorStore (in-memory) with extensibility for future database integration
- **Build Tools**: Makefile with emoji and ASCII colors for multi-step commands

### Spring AI Integration Points
- **Chat Models**: OpenAI GPT-4/GPT-4o-mini and local Ollama models (Mistral, Llama)
- **Embedding Models**: OpenAI text-embedding-3-small and Ollama nomic-embed-text
- **Vector Storage**: SimpleVectorStore for in-memory RAG with save/load capabilities
- **Memory Management**: Simple conversation memory with session management
- **Tool Integration**: Simulated external system calls for SMS and calendar demonstrations

### Architecture Principles
- **Single Controller**: All endpoints handled by one controller class
- **Single Service Layer**: Consolidated business logic in one service class
- **Minimal Configuration**: Only essential configuration via application.yml and environment variables
- **No Unnecessary Features**: No health checks, metrics, or advanced security beyond basic API key protection
- **Simple Frontend**: Static HTML/CSS/JS without frameworks - modern and responsive design

## Core Application Requirements

### Simple Web Application Structure
The application provides a clean, responsive web interface for airline loyalty assistance:

**Static Frontend (No Frameworks)**
- Single-page application with modern HTML5/CSS3 design
- Real-time chat interface with message history display using vanilla JavaScript
- Input validation and error handling with simple DOM manipulation
- Loading states and progress indicators
- Mobile-responsive design supporting tablet and phone usage
- Served as static assets from Spring Boot

**Minimal Backend Processing**
- Single REST controller handling all endpoints
- One service class containing all business logic
- Asynchronous message processing where beneficial
- Structured JSON response formatting
- Basic error handling and logging
- Environment variable-based configuration only

**Spring Boot Integration**
- Auto-configuration for Spring AI components
- Profile-based configuration management (dev, prod, local)
- Minimal dependencies - only what's needed for the demo
- Graceful shutdown and startup procedures

### Spring Profiles Configuration

**Development Profile (`dev`)**
```yaml
spring:
  profiles:
    active: dev
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
          temperature: 0.8
```

**Production Profile (`prod`)**
```yaml
spring:
  profiles:
    active: prod
  ai:
    openai:
      chat:
        options:
          model: gpt-4o
          temperature: 0.2
```

**Local Model Profile (`local`)**
```yaml
spring:
  profiles:
    active: local
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: mistral:7b
          temperature: 0.7
```

## Progressive Enhancement Implementation Phases

### Phase 1: Basic OpenAI Integration

**Objective**: Establish foundational AI chat capabilities with OpenAI models

**Technical Implementation**
- Spring AI ChatClient configuration with OpenAI backend
- Simple prompt-response pattern without context preservation
- Basic error handling for API failures and rate limits
- Token usage monitoring and cost tracking

**Acceptance Criteria**
- Users can submit airline loyalty questions and receive AI-generated responses
- Application handles OpenAI API errors gracefully with user-friendly messages
- Response times average under 500ms for typical queries
- Token usage is logged for cost monitoring

**User Stories**
- As a traveler, I can ask basic questions about airline loyalty programs and receive helpful responses
- As a developer, I can see clear examples of Spring AI OpenAI integration patterns

### Phase 2: Conversation Memory Implementation

**Objective**: Add contextual awareness across user interactions

**Technical Implementation**
- MessageWindowChatMemory with JDBC persistence
- Session-based conversation ID management
- MessageChatMemoryAdvisor integration with ChatClient
- Visual conversation flow indicators in the UI

**Memory Architecture**
```java
@Bean
public ChatMemory chatMemory(ChatMemoryRepository repository) {
    return MessageWindowChatMemory.builder()
        .chatMemoryRepository(repository)
        .maxMessages(50)
        .build();
}
```

**Acceptance Criteria**
- Users can reference previous messages in ongoing conversations
- Conversation context is maintained across browser sessions
- Memory storage scales to support multiple concurrent users
- Visual indicators show when context is being used

**User Stories**
- As a frequent flyer, I can ask follow-up questions that reference my previous inquiries
- As a user, I can return to my conversation and continue where I left off

### Phase 3: RAG Implementation

**Objective**: Integrate airline loyalty program knowledge through simple RAG with in-memory storage

**Technical Implementation**
- Document loading from Delta and United loyalty program websites
- SimpleVectorStore for in-memory vector storage with save/load capabilities
- QuestionAnswerAdvisor for context-aware response generation
- Text chunking and embedding generation for knowledge base

**Simple RAG Architecture**
```java
@Bean
public VectorStore vectorStore(EmbeddingModel embeddingModel) {
    return new SimpleVectorStore(embeddingModel);
}

@Bean
public QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vectorStore) {
    return new QuestionAnswerAdvisor(vectorStore);
}
```

**Document Processing Pipeline**
- **Source Loading**: Basic text readers for airline loyalty program content
- **Text Splitting**: Simple token-based splitting with reasonable chunk sizes
- **Embedding Generation**: OpenAI text-embedding-3-small for vector representation
- **Storage**: SimpleVectorStore with optional persistence to JSON file

**Knowledge Base Content**
- Delta SkyMiles program structure, earning rates, and redemption options
- United MileagePlus tier benefits and qualification requirements
- Common customer questions from official airline documentation
- Extensible design for adding additional airlines in the future

**Acceptance Criteria**
- Assistant provides accurate answers to loyalty program questions with retrieved context
- SimpleVectorStore loads and saves state correctly for persistence across restarts
- RAG retrieval finds relevant context efficiently from in-memory store
- Answers include clear indications when context from knowledge base is used
- Design allows easy migration to database-backed vector stores in the future

### Phase 4: Tool Integration

**Objective**: Demonstrate external system integration capabilities

**Tool Implementations**
- **SMS Service**: Simulated SMS notifications for flight alerts and status updates
- **Calendar API**: Mock calendar integration for trip planning and reminder setting
- **Flight Status**: Real-time flight information lookup (demonstration mode)
- **Points Calculator**: Interactive tools for calculating earning and redemption rates

**Technical Patterns**
```java
@Component
public class AirlineTools {
    
    @Tool(description = "Send SMS notification for flight updates")
    public void sendFlightAlert(
        @ToolParam(description = "Phone number") String phone,
        @ToolParam(description = "Flight number") String flightNumber) {
        // Simulated SMS service call
        smsService.sendAlert(phone, "Flight " + flightNumber + " is on time!");
    }
    
    @Tool(description = "Add event to calendar")
    public void addCalendarEvent(
        @ToolParam(description = "Event title") String title,
        @ToolParam(description = "Event date") String date) {
        // Mock calendar API integration
        calendarService.createEvent(title, date);
    }
}
```

**Acceptance Criteria**
- AI assistant can trigger external actions based on user requests
- Tool integration provides clear feedback on action completion
- Error handling gracefully manages tool failures
- Demonstration mode clearly indicates simulated vs. real integrations

### Phase 5: Local Model Support

**Objective**: Enable complete local deployment with Ollama integration

**Docker Compose Architecture**
```yaml
services:
  ollama:
    image: ollama/ollama:latest
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama
    environment:
      - OLLAMA_KEEP_ALIVE=24h
  
  spring-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=local
      - SPRING_AI_OLLAMA_BASE_URL=http://ollama:11434
    depends_on:
      - ollama
```

**Model Configuration**
- **Primary Model**: Mistral 7B for general conversation
- **Embedding Model**: nomic-embed-text for RAG similarity search
- **Alternative Models**: Llama 3.2 and CodeLlama for specialized tasks
- **Model Management**: Automatic pulling and health monitoring

**Acceptance Criteria**
- Application runs entirely offline without external API dependencies
- Local model performance meets minimum requirements (5+ tokens/second)
- Profile switching enables seamless cloud-to-local model transitions
- Resource usage remains within acceptable bounds (8GB RAM maximum)

## Implementation Phases and Timeline

### Phase 1: Foundation (Weeks 1-2)
- **Week 1**: Project setup, Spring Boot configuration, basic web interface
- **Week 2**: OpenAI integration, error handling, initial testing

### Phase 2: Memory Integration (Week 3)
- Database setup and migration scripts
- Conversation memory implementation
- Session management and UI updates

### Phase 3: RAG Implementation (Weeks 4-5)
- **Week 4**: Document processing pipeline and vector storage
- **Week 5**: RAG advisor integration and knowledge base population

### Phase 4: Tool Integration (Week 6)
- External tool implementations
- Function calling configuration
- Integration testing and refinement

### Phase 5: Local Model Support (Week 7)
- Ollama Docker configuration
- Profile-based model switching
- Performance optimization and testing

### Phase 6: Polish and Documentation (Week 8)
- Code cleanup and documentation
- Performance optimization
- Deployment documentation

## User Stories and Acceptance Criteria

### Core Functionality
**As a frequent flyer, I want to ask questions about airline loyalty programs so that I can maximize my benefits and understand my options.**

*Acceptance Criteria:*
- I can ask questions in natural language about Delta SkyMiles or United MileagePlus
- The assistant provides accurate, helpful responses within 2 seconds
- Complex questions are answered with appropriate context and source attribution
- I can ask follow-up questions that reference previous parts of our conversation

### Advanced Features
**As a traveler, I want the assistant to help me take action on my loyalty account so that I can manage my travel efficiently.**

*Acceptance Criteria:*
- The assistant can simulate sending me flight alerts via SMS
- I can request calendar events for important loyalty program dates
- Tool integration provides clear feedback on action completion
- Failed actions are handled gracefully with alternative suggestions

### Developer Experience
**As a developer, I want to understand how to implement Spring AI features so that I can apply these patterns in my own projects.**

*Acceptance Criteria:*
- Code is well-documented with clear examples of each Spring AI feature
- Different implementation approaches are demonstrated and explained
- Configuration options are clearly documented with use case examples
- Project can be set up and running within 15 minutes

## Technical Specifications

### Build Configuration (Gradle + Kotlin DSL)
```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.graalvm.buildtools.native") version "0.9.28"
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

extra["springAiVersion"] = "1.0.0-M5"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-pgvector-store-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-advisors-vector-store")
    
    // Profile-specific dependencies
    implementation("org.springframework.ai:spring-ai-starter-model-ollama") {
        isOptional = true
    }
}
```

### Database Schema
- **PostgreSQL 15** with pgvector extension
- **Conversation Memory Tables**: Automatically created via Spring AI
- **Vector Storage**: Optimized indexes for similarity search
- **Migration Strategy**: Flyway for version control

### Performance Requirements
- **Response Time**: \u003c200ms for basic queries, \u003c2s for RAG queries
- **Throughput**: 100+ concurrent users supported
- **Memory Usage**: \u003c4GB heap space for cloud deployment, \u003c8GB for local
- **Storage**: \u003c1GB for application, \u003c5GB for vector data

### Security Considerations
- **API Key Management**: Environment variables and Spring Cloud Config
- **Input Validation**: Comprehensive sanitization of user inputs
- **Rate Limiting**: Per-user and per-IP request throttling
- **Data Privacy**: Conversation data retention policies and user consent

## Deployment Architecture

### Local Development with Makefile
```makefile
# 🚀 Spring AI Airline Loyalty Assistant
# Makefile with emoji and ASCII colors

GREEN := \033[0;32m
YELLOW := \033[1;33m
RED := \033[0;31m
NC := \033[0m # No Color

.PHONY: help dev local clean test build

help: ## 📖 Show this help message
	@echo "🚀 Spring AI Airline Loyalty Assistant"
	@echo ""
	@echo "Available commands:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(YELLOW)%-15s$(NC) %s\n", $1, $2}'

dev: ## 🔧 Run in development mode with OpenAI
	@echo "$(GREEN)🔧 Starting development server...$(NC)"
	SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun

local: ## 🏠 Run with local Ollama (starts Ollama in Docker)
	@echo "$(GREEN)🏠 Starting Ollama and local server...$(NC)"
	docker-compose up -d ollama
	@echo "$(YELLOW)⏳ Waiting for Ollama to start...$(NC)"
	sleep 10
	@echo "$(GREEN)📥 Pulling Mistral model...$(NC)"
	docker exec $(docker-compose ps -q ollama) ollama pull mistral:7b
	SPRING_PROFILES_ACTIVE=local ./gradlew bootRun

clean: ## 🧹 Clean build and stop all services
	@echo "$(GREEN)🧹 Cleaning up...$(NC)"
	./gradlew clean
	docker-compose down

test: ## 🧪 Run tests
	@echo "$(GREEN)🧪 Running tests...$(NC)"
	./gradlew test

build: ## 🏗️ Build the application
	@echo "$(GREEN)🏗️ Building application...$(NC)"
	./gradlew build
```

### Docker Compose for Local Models
```yaml
services:
  ollama:
    image: ollama/ollama:latest
    ports:
      - "11434:11434"
    volumes:
      - ollama_data:/root/.ollama
    environment:
      - OLLAMA_KEEP_ALIVE=24h

volumes:
  ollama_data:
```

### Simple Deployment
- **Local Development**: Run with `make dev` or `make local`
- **Production**: Standard Spring Boot JAR deployment
- **Environment**: Configure via environment variables only
- **No Complex Infrastructure**: Docker Compose only for local Ollama

## Risk Mitigation

### Technical Risks
- **API Rate Limits**: Circuit breaker pattern with fallback to local models
- **Model Availability**: Health checks and automatic failover mechanisms
- **Data Quality**: Automated validation of RAG knowledge base content
- **Performance Degradation**: Load testing and optimization strategies

### Operational Risks
- **Cost Management**: Token usage monitoring and budget alerts
- **Security Vulnerabilities**: Regular dependency updates and security scanning
- **Data Compliance**: GDPR-compliant data handling and retention policies

## Success Criteria

### Functional Requirements
- All five progressive enhancement phases implemented and tested
- Comprehensive test coverage (\u003e80%) for all critical paths
- Performance benchmarks met across all deployment scenarios
- Documentation enables independent setup and customization

### Non-Functional Requirements
- **Reliability**: 99.9% uptime for production deployments
- **Scalability**: Linear performance scaling to 1000+ concurrent users
- **Maintainability**: Modular architecture supporting future enhancements
- **Usability**: Intuitive interface requiring no training for basic usage

## Future Enhancement Opportunities

### Advanced AI Integration
- **Multi-modal Support**: Image and voice processing capabilities
- **Advanced RAG**: Hybrid search combining semantic and keyword matching
- **Model Fine-tuning**: Custom models trained on airline-specific data
- **Agentic Workflows**: Complex multi-step task automation

### Enterprise Features
- **Multi-tenant Support**: Separate knowledge bases per airline
- **Advanced Analytics**: Usage patterns and optimization insights
- **Integration APIs**: REST APIs for third-party system integration
- **Advanced Security**: Enterprise SSO and audit logging

This comprehensive PRD provides a clear roadmap for developing a production-ready Spring AI demonstration application that showcases the full spectrum of framework capabilities while delivering practical value for airline loyalty program assistance.