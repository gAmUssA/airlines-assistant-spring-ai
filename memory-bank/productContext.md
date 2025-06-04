# Product Context - Airlines Assistant Spring AI

## Project Overview
Spring AI-based Airline Loyalty Assistant demo application showcasing Spring AI 1.0.0 stable capabilities. The application demonstrates progressive enhancement from basic OpenAI integration to advanced RAG implementations with local model support.

## Core Components
- **Technology Stack**: Java 21, Spring Boot 3.5.0, Spring AI 1.0.0, Gradle with Kotlin DSL
- **Architecture**: Single controller, single service layer, minimal configuration approach
- **Frontend**: Static HTML/CSS/JS without frameworks, modern responsive design
- **AI Integration**: OpenAI GPT-4/GPT-4o-mini, local Ollama models (Mistral, Llama)
- **Vector Storage**: SimpleVectorStore for in-memory RAG with save/load capabilities

## Project Organization
```
/Users/vikgamov/projects/ai/airlines-assistant-spring-ai/
├── docs/requirements.md (comprehensive PRD)
├── .windsurfrules (memory system configuration)
└── memory-bank/ (project memory system)
```

## Repository Information
- **GitHub Repository**: https://github.com/gAmUssA/airlines-assistant-spring-ai
- **License**: MIT License
- **Version Control**: Git with main branch
- **CI/CD**: GitHub Actions with smoke testing
- **Dependency Management**: Renovate bot configured
- **Initial Commit**: 40e3441 - Complete Phase 1 Week 1 foundation

[2025-06-05 01:22:30] - Repository created and initial codebase committed to GitHub

## Development Standards
- Use Gradle with Kotlin for build scripts (per user preferences)
- Docker Compose without version attribute for container management
- Makefiles with emoji and ASCII colors for multi-command execution
- Avoid deprecated Java APIs
- Follow Spring Boot best practices with minimal abstractions

## Progressive Enhancement Phases
1. **Phase 1**: Basic OpenAI Chat Integration
2. **Phase 2**: Conversation Memory & Context
3. **Phase 3**: RAG Implementation with Vector Storage
4. **Phase 4**: Function Calling & Tool Integration
5. **Phase 5**: Local Model Support (Ollama)

[2025-06-05 00:54:05] - Initial project context established from requirements.md and user preferences
