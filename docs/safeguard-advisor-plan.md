# SafeGuardAdvisor Implementation Plan

## Overview

The SafeGuardAdvisor is a Spring AI advisor component that provides content filtering and safety measures for the Airlines Assistant. It ensures professional, appropriate, and safe interactions by filtering both user input and AI responses.

## Architecture

### Core Components

1. **SafeGuardAdvisor** - Main advisor implementing `RequestResponseAdvisor`
2. **ContentFilter** - Core filtering logic interface
3. **FilterRule** - Individual filter rule implementations
4. **FilterConfiguration** - Configuration management
5. **FilterMetrics** - Monitoring and logging

### Integration Points

- **ChatClient Advisor Chain** - Integrated as a request/response advisor
- **Configuration Properties** - Spring Boot configuration for filter settings
- **Logging System** - Structured logging for filtered content
- **Admin Interface** - Management UI for filter rules
- **Safe Chat REST Endpoint** - New endpoint for filtered chat interactions
- **UI Toggle System** - Frontend controls to switch between filtered/unfiltered modes

### REST API Extensions

**New Endpoint Required:**
- `/api/v1/chat/safe` - Chat endpoint with SafeGuardAdvisor enabled
- `/api/v1/chat` - Existing endpoint (unfiltered for admin/testing)

**Frontend UI Enhancements:**
- Content filtering toggle switch
- Filter status indicator
- Safe mode visual indicators
- Admin override controls

## Implementation Details

### Phase 1: Basic Content Filtering (Tasks 6.20-6.23)

#### 6.20 Create SafeGuardAdvisor Component

```java
@Component
public class SafeGuardAdvisor implements RequestResponseAdvisor {
    
    private final ContentFilterService contentFilterService;
    private final FilterMetricsService metricsService;
    
    @Override
    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
        // Filter user input
        String filteredContent = contentFilterService.filterInput(request.userText());
        return AdvisedRequest.from(request).withUserText(filteredContent).build();
    }
    
    @Override
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> context) {
        // Filter AI response
        String filteredContent = contentFilterService.filterOutput(response.getResult().getOutput().getContent());
        // Return modified response if content was filtered
        return response;
    }
}
```

#### 6.21 Basic Profanity Detection

```java
@Service
public class ContentFilterService {
    
    private final List<FilterRule> filterRules;
    private final FilterConfiguration config;
    
    public String filterInput(String input) {
        FilterResult result = applyFilters(input, FilterType.INPUT);
        return result.isFiltered() ? result.getFilteredContent() : input;
    }
    
    public String filterOutput(String output) {
        FilterResult result = applyFilters(output, FilterType.OUTPUT);
        return result.isFiltered() ? result.getFilteredContent() : output;
    }
}
```

#### 6.22 Airline-Specific Content Rules

```java
@Component
public class AirlineContentRule implements FilterRule {
    
    @Override
    public FilterResult apply(String content, FilterContext context) {
        // Check for inappropriate airline-related content
        // Ensure professional tone
        // Validate topic appropriateness
        return FilterResult.builder()
            .filtered(isInappropriate(content))
            .filteredContent(generateAlternative(content))
            .reason("Airline policy violation")
            .build();
    }
}
```

### Phase 2: Advanced Filtering (Tasks 6.24-6.27)

#### 6.24 Configurable Sensitivity Levels

```yaml
# application.yml
airline:
  assistant:
    content-filter:
      sensitivity: moderate  # strict, moderate, lenient
      rules:
        profanity:
          enabled: true
          sensitivity: strict
        airline-policy:
          enabled: true
          sensitivity: moderate
        professional-tone:
          enabled: true
          sensitivity: lenient
```

#### 6.25 Content Warning System

```java
@Component
public class ContentWarningService {
    
    public String generateWarning(FilterResult result) {
        return switch (result.getReason()) {
            case "profanity" -> "I apologize, but I need to maintain professional language. Let me rephrase that...";
            case "inappropriate-topic" -> "I'm designed to help with airline-related questions. How can I assist you with travel today?";
            case "policy-violation" -> "I need to follow airline communication guidelines. Let me provide appropriate assistance...";
            default -> "Let me provide a more appropriate response...";
        };
    }
}
```

#### 6.26 Logging and Monitoring

```java
@Component
public class FilterMetricsService {
    
    private final MeterRegistry meterRegistry;
    private final Logger logger = LoggerFactory.getLogger(FilterMetricsService.class);
    
    public void recordFilterEvent(FilterEvent event) {
        // Metrics
        meterRegistry.counter("content.filter.triggered", 
            "rule", event.getRuleName(),
            "severity", event.getSeverity())
            .increment();
            
        // Structured logging
        logger.warn("Content filtered: rule={}, severity={}, userId={}", 
            event.getRuleName(), event.getSeverity(), event.getUserId());
    }
}
```

### Phase 3: Management & Testing (Tasks 6.27-6.30)

#### 6.27 Admin Interface

```java
@RestController
@RequestMapping("/api/admin/content-filter")
public class ContentFilterAdminController {
    
    @GetMapping("/rules")
    public List<FilterRuleConfig> getRules() {
        return filterConfigService.getAllRules();
    }
    
    @PostMapping("/rules")
    public FilterRuleConfig createRule(@RequestBody FilterRuleConfig rule) {
        return filterConfigService.createRule(rule);
    }
    
    @PutMapping("/rules/{id}")
    public FilterRuleConfig updateRule(@PathVariable String id, @RequestBody FilterRuleConfig rule) {
        return filterConfigService.updateRule(id, rule);
    }
}
```

## Configuration Integration

### ChatClient Integration

```java
@Configuration
public class AirlineAssistantConfig {
    
    @Bean
    @Primary
    public ChatClient chatClient(
            ChatClient.Builder chatClientBuilder, 
            ChatMemory chatMemory, 
            AirlineTools airlineTools,
            SafeGuardAdvisor safeGuardAdvisor,
            Environment environment) {
        
        return chatClientBuilder
            .defaultSystem(getSystemMessage(environment))
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder()
                    .chatMemoryRepository(chatMemory)
                    .build(),
                safeGuardAdvisor  // Add SafeGuard advisor to chain
            )
            .defaultTools(airlineTools)
            .build();
    }
}
```

### Properties Configuration

```yaml
airline:
  assistant:
    content-filter:
      enabled: true
      sensitivity: moderate
      log-filtered-content: true
      generate-alternatives: true
      rules:
        profanity:
          enabled: true
          sensitivity: strict
          words: ["list", "of", "words"]
        airline-policy:
          enabled: true
          sensitivity: moderate
          topics: ["complaints", "safety", "regulations"]
        professional-tone:
          enabled: true
          sensitivity: lenient
          patterns: ["informal", "slang", "inappropriate"]
```

## Testing Strategy

### Unit Tests
- Individual filter rule testing
- Content filtering logic validation
- Configuration loading tests

### Integration Tests
- End-to-end filtering in ChatClient chain
- Profile-specific filtering (OpenAI vs Ollama)
- Admin interface functionality

### Test Scenarios
- Profanity detection and replacement
- Inappropriate topic redirection
- Professional tone enforcement
- Edge cases and false positives

## Monitoring & Metrics

### Key Metrics
- Filter trigger frequency by rule
- Response time impact
- False positive rates
- User satisfaction after filtering

### Alerts
- High filter trigger rates
- System performance degradation
- Configuration errors

## Deployment Considerations

### Performance
- Minimal latency impact on chat responses
- Efficient rule evaluation
- Caching for frequently used patterns

### Scalability
- Rule engine performance with large rule sets
- Memory usage optimization
- Concurrent request handling

### Security
- Secure admin interface access
- Audit logging for rule changes
- Data privacy for filtered content

## Future Enhancements

1. **Machine Learning Integration**
   - AI-powered content classification
   - Adaptive filtering based on context
   - Sentiment analysis integration

2. **Advanced Rule Engine**
   - Complex rule combinations
   - Context-aware filtering
   - Dynamic rule learning

3. **Multi-language Support**
   - Internationalization for filter rules
   - Language-specific content policies
   - Cultural sensitivity considerations

4. **Integration Extensions**
   - External content moderation APIs
   - Industry-specific compliance rules
   - Real-time threat detection

## Safe Chat REST Endpoint

```java
@RestController
@RequestMapping("/api/v1/chat")
public class ChatController {
    
    private final ChatClient safeChatClient;
    private final ChatClient regularChatClient;
    
    @PostMapping("/safe")
    public ResponseEntity<ChatResponse> safeChatEndpoint(@RequestBody ChatRequest request) {
        // Use ChatClient with SafeGuardAdvisor enabled
        ChatResponse response = safeChatClient.prompt()
            .user(request.getMessage())
            .call()
            .chatResponse();
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    public ResponseEntity<ChatResponse> regularChatEndpoint(@RequestBody ChatRequest request) {
        // Use ChatClient without SafeGuardAdvisor (admin/testing)
        ChatResponse response = regularChatClient.prompt()
            .user(request.getMessage())
            .call()
            .chatResponse();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/filter-status")
    public ResponseEntity<FilterStatus> getFilterStatus() {
        return ResponseEntity.ok(FilterStatus.builder()
            .enabled(contentFilterService.isEnabled())
            .sensitivity(contentFilterService.getSensitivity())
            .activeRules(contentFilterService.getActiveRules())
            .build());
    }
}
```

## Frontend UI Toggle System

```javascript
// app.js - Add filter toggle functionality
class ChatInterface {
    constructor() {
        this.safeMode = true; // Default to safe mode
        this.initializeFilterToggle();
    }
    
    initializeFilterToggle() {
        const toggleSwitch = document.getElementById('safe-mode-toggle');
        const statusIndicator = document.getElementById('filter-status');
        
        toggleSwitch.addEventListener('change', (e) => {
            this.safeMode = e.target.checked;
            this.updateFilterStatus();
            this.updateChatEndpoint();
        });
        
        this.updateFilterStatus();
    }
    
    updateFilterStatus() {
        const statusElement = document.getElementById('filter-status');
        const chatContainer = document.getElementById('chat-container');
        
        if (this.safeMode) {
            statusElement.innerHTML = '🛡️ Safe Mode: ON';
            statusElement.className = 'filter-status safe-mode';
            chatContainer.classList.add('safe-mode');
        } else {
            statusElement.innerHTML = '⚠️ Safe Mode: OFF';
            statusElement.className = 'filter-status admin-mode';
            chatContainer.classList.add('admin-mode');
        }
    }
    
    updateChatEndpoint() {
        this.chatEndpoint = this.safeMode ? '/api/v1/chat/safe' : '/api/v1/chat';
    }
    
    async sendMessage(message) {
        const response = await fetch(this.chatEndpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ message: message })
        });
        
        const data = await response.json();
        this.displayMessage(data, this.safeMode);
    }
    
    displayMessage(response, filtered) {
        const messageElement = document.createElement('div');
        messageElement.className = `message ai-message ${filtered ? 'filtered' : 'unfiltered'}`;
        
        if (filtered && response.wasFiltered) {
            messageElement.innerHTML = `
                <div class="filter-warning">🛡️ Content was filtered for safety</div>
                <div class="message-content">${response.content}</div>
            `;
        } else {
            messageElement.innerHTML = `<div class="message-content">${response.content}</div>`;
        }
        
        document.getElementById('chat-messages').appendChild(messageElement);
    }
}
```

## CSS Styling for Filter States

```css
/* styles.css - Add filter mode styling */
.filter-status {
    padding: 8px 12px;
    border-radius: 4px;
    font-weight: bold;
    margin-bottom: 10px;
    text-align: center;
}

.filter-status.safe-mode {
    background-color: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
}

.filter-status.admin-mode {
    background-color: #fff3cd;
    color: #856404;
    border: 1px solid #ffeaa7;
}

.chat-container.safe-mode {
    border-left: 4px solid #28a745;
}

.chat-container.admin-mode {
    border-left: 4px solid #ffc107;
}

.message.filtered .filter-warning {
    background-color: #f8f9fa;
    padding: 4px 8px;
    border-radius: 3px;
    font-size: 0.85em;
    color: #6c757d;
    margin-bottom: 5px;
}

.safe-mode-toggle {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 15px;
}

.toggle-switch {
    position: relative;
    width: 50px;
    height: 25px;
    background-color: #ccc;
    border-radius: 25px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.toggle-switch.active {
    background-color: #28a745;
}

.toggle-slider {
    position: absolute;
    top: 2px;
    left: 2px;
    width: 21px;
    height: 21px;
    background-color: white;
    border-radius: 50%;
    transition: transform 0.3s;
}

.toggle-switch.active .toggle-slider {
    transform: translateX(25px);
}
