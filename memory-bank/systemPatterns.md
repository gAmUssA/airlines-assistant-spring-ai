# System Patterns - Airlines Assistant Spring AI

## Coding Patterns

### Spring Boot Configuration
- **Pattern**: Minimal configuration via application.yml and environment variables
- **Usage**: Only essential configuration, no unnecessary features like health checks or metrics
- **Example**: Single configuration class for Spring AI ChatClient setup

### Build Script Standards
- **Pattern**: Gradle with Kotlin DSL for build scripts
- **Usage**: All build.gradle.kts files use Kotlin syntax per user preferences
- **Example**: Modern Gradle configuration with version catalogs

### Error Handling
- **Pattern**: Graceful degradation with clear error messages
- **Usage**: API failures handled with user-friendly responses
- **Example**: OpenAI API rate limit handling with retry logic

## Architecture Patterns

### Single Responsibility Controllers
- **Pattern**: One controller handling all endpoints
- **Usage**: AirlineAssistantController manages all chat and RAG endpoints
- **Rationale**: Simplicity over separation for demo application

### Service Layer Consolidation
- **Pattern**: Single service class for business logic
- **Usage**: AirlineAssistantService handles all AI interactions
- **Rationale**: Avoid over-engineering for straightforward use case

### Progressive Enhancement
- **Pattern**: Incremental feature addition across 5 phases
- **Usage**: Each phase builds upon previous without breaking changes
- **Benefits**: Clear development milestones and learning progression

## Testing Patterns

### Integration Testing Focus
- **Pattern**: Emphasize integration tests over unit tests
- **Usage**: Test Spring AI integration points and API interactions
- **Rationale**: AI applications benefit more from integration testing

### Mock External Services
- **Pattern**: Mock OpenAI API calls for testing
- **Usage**: Use Spring Boot test slices with mocked AI responses
- **Benefits**: Reliable tests without external API dependencies

## Docker and Deployment Patterns

### Docker Compose Configuration
- **Pattern**: Docker Compose without version attribute
- **Usage**: docker-compose.yaml files follow user preference
- **Services**: Application, potential future database, local AI models

### Makefile Automation
- **Pattern**: Makefile with emoji and ASCII colors for multi-step commands
- **Usage**: Build, test, run, and deployment commands
- **Benefits**: Improved developer experience with visual feedback

[2025-06-05 00:54:05] - Initial system patterns established based on project requirements and user preferences
