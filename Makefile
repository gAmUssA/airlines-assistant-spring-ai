# 🛫 Airline Assistant Spring AI - Development Makefile
# Colors for better readability
GREEN := \033[0;32m
YELLOW := \033[1;33m
BLUE := \033[0;34m
RED := \033[0;31m
NC := \033[0m # No Color

# Load environment variables from .env file if it exists
ifneq (,$(wildcard ./.env))
    include .env
    export
endif

.PHONY: help build test run run-with-memory run-local run-local-with-memory clean dev-run check format env-check docs ci-local

help: ## 📋 Show this help message
	@echo "$(BLUE)🛫 Airline Assistant Spring AI$(NC)"
	@echo "$(YELLOW)Available commands:$(NC)"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  $(GREEN)%-20s$(NC) %s\n", $$1, $$2}' $(MAKEFILE_LIST)

env-check: ## 🔍 Check environment setup
	@echo "$(BLUE)🔍 Checking environment setup...$(NC)"
	@if [ -f .env ]; then \
		echo "$(GREEN)✅ .env file found$(NC)"; \
		if [ -n "$$OPENAI_API_KEY" ]; then \
			echo "$(GREEN)✅ OPENAI_API_KEY is set$(NC)"; \
			echo "$(YELLOW)🔑 API Key: $${OPENAI_API_KEY:0:20}...$(NC)"; \
		else \
			echo "$(RED)❌ OPENAI_API_KEY is not set in .env file$(NC)"; \
		fi; \
	else \
		echo "$(RED)❌ .env file not found$(NC)"; \
		echo "$(YELLOW)💡 Run 'make setup-env' to create template$(NC)"; \
	fi
	@echo "$(BLUE)🌐 Application will run on: http://localhost:9080$(NC)"

build: ## 🔨 Build the application
	@echo "$(BLUE)🔨 Building application...$(NC)"
	./gradlew build

test: ## 🧪 Run tests
	@echo "$(BLUE)🧪 Running tests...$(NC)"
	./gradlew test

run: env-check ## 🚀 Run the application
	@echo "$(BLUE)🚀 Starting application on port 9080...$(NC)"
	@if [ -z "$$OPENAI_API_KEY" ]; then \
		echo "$(RED)❌ OPENAI_API_KEY not found! Please check your .env file$(NC)"; \
		exit 1; \
	fi
	./gradlew bootRun

run-with-memory: env-check ## 🚀 Run the application with increased memory settings
	@echo "$(BLUE)🚀 Starting application with increased memory settings on port 9080...$(NC)"
	@if [ -z "$$OPENAI_API_KEY" ]; then \
		echo "$(RED)❌ OPENAI_API_KEY not found! Please check your .env file$(NC)"; \
		exit 1; \
	fi
	@echo "$(YELLOW)💡 Using JVM options: -Xmx512m -Xms256m$(NC)"
	JAVA_OPTS="-Xmx512m -Xms256m" ./gradlew bootRun

run-local: env-check ## 🚀 Run the application with local profile (Ollama)
	@echo "$(BLUE)🚀 Starting application with local profile (Ollama) on port 9080...$(NC)"
	@echo "$(YELLOW)🏠 Using Ollama for local AI processing$(NC)"
	./gradlew bootRun --args='--spring.profiles.active=local'

run-local-with-memory: env-check ## 🚀 Run the application with local profile and increased memory settings
	@echo "$(BLUE)🚀 Starting application with local profile (Ollama) and increased memory settings on port 9080...$(NC)"
	@echo "$(YELLOW)🏠 Using Ollama for local AI processing$(NC)"
	@echo "$(YELLOW)💡 Using JVM options: -Xmx512m -Xms256m$(NC)"
	JAVA_OPTS="-Xmx512m -Xms256m" ./gradlew bootRun --args='--spring.profiles.active=local'

dev-run: env-check ## 🔧 Run in development mode with hot reload
	@echo "$(BLUE)🔧 Starting in development mode on port 9080...$(NC)"
	@if [ -z "$$OPENAI_API_KEY" ]; then \
		echo "$(RED)❌ OPENAI_API_KEY not found! Please check your .env file$(NC)"; \
		exit 1; \
	fi
	./gradlew bootRun --continuous

clean: ## 🧹 Clean build artifacts
	@echo "$(BLUE)🧹 Cleaning build artifacts...$(NC)"
	./gradlew clean

check: ## ✅ Run all checks (build + test)
	@echo "$(BLUE)✅ Running all checks...$(NC)"
	./gradlew check

format: ## 🎨 Format code (placeholder for future formatting tools)
	@echo "$(BLUE)🎨 Code formatting...$(NC)"
	@echo "$(YELLOW)💡 Consider adding spotless or similar formatting plugin$(NC)"

setup-env: ## 🔧 Setup environment file template
	@echo "$(BLUE)🔧 Creating environment template...$(NC)"
	@if [ ! -f .env.template ]; then \
		echo "OPENAI_API_KEY=your-openai-api-key-here" > .env.template; \
		echo "$(GREEN)✅ Created .env.template$(NC)"; \
		echo "$(YELLOW)💡 Copy .env.template to .env and add your API key$(NC)"; \
	else \
		echo "$(YELLOW)⚠️  .env.template already exists$(NC)"; \
	fi

docker-build: ## 🐳 Build Docker image (future enhancement)
	@echo "$(BLUE)🐳 Docker build...$(NC)"
	@echo "$(YELLOW)💡 Docker support will be added in future phases$(NC)"

status: ## 📊 Show project status
	@echo "$(BLUE)📊 Project Status$(NC)"
	@echo "$(GREEN)✅ Java Version:$(NC) $$(java -version 2>&1 | head -n 1)"
	@echo "$(GREEN)✅ Gradle Version:$(NC) $$(./gradlew --version | grep Gradle | head -n 1)"
	@echo "$(GREEN)✅ Spring Boot:$(NC) 3.5.0"
	@echo "$(GREEN)✅ Spring AI:$(NC) 1.0.0"

docs: ## 📚 Generate documentation
	@echo "$(BLUE)📚 Documentation available:$(NC)"
	@echo "$(GREEN)📖 README:$(NC) README.adoc"
	@echo "$(GREEN)📋 Plan:$(NC) docs/plan.md"
	@echo "$(GREEN)✅ Tasks:$(NC) docs/tasks.md"
	@echo "$(GREEN)🧠 Memory Bank:$(NC) memory-bank/"

ci-local: ## 🔄 Run CI checks locally
	@echo "$(BLUE)🔄 Running CI checks locally...$(NC)"
	@echo "$(YELLOW)🔍 Checking Gradle wrapper...$(NC)"
	@if [ -f gradlew ]; then echo "$(GREEN)✅ Gradle wrapper found$(NC)"; else echo "$(RED)❌ Gradle wrapper missing$(NC)"; exit 1; fi
	@echo "$(YELLOW)🏗️ Building project...$(NC)"
	./gradlew build --no-daemon
	@echo "$(YELLOW)🧪 Running tests...$(NC)"
	./gradlew test --no-daemon
	@echo "$(GREEN)✅ All CI checks passed!$(NC)"
