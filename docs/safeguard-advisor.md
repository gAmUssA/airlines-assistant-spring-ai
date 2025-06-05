# SafeGuardAdvisor Content Filtering System

## Overview

The SafeGuardAdvisor system provides comprehensive content filtering capabilities for the Airlines Assistant Spring AI application. It leverages Spring AI's built-in `SafeGuardAdvisor` to filter inappropriate content while maintaining a professional and safe user experience.

## Implementation Date
**Completed**: June 5, 2025

## Features

### Core Functionality
- **Content Filtering**: Automatic detection and filtering of inappropriate content
- **Dual Chat Modes**: Regular chat and safe chat with content filtering
- **Configurable Sensitivity**: LOW, MEDIUM, HIGH sensitivity levels
- **Professional Warnings**: Airline-appropriate warning messages for filtered content
- **Real-time Toggle**: Frontend UI toggle to switch between safe and regular modes
- **Audit Logging**: Comprehensive logging of filtering events for compliance

### Content Categories
1. **Profanity Detection**: Common profanity and offensive language
2. **Inappropriate Content**: Sexual, violent, or disturbing content
3. **Policy Violations**: Content violating airline policies and professional standards

## Architecture

### Backend Components

#### 1. SafeGuardConfig
**Location**: `src/main/java/com/airline/assistant/config/SafeGuardConfig.java`

Configuration properties class managing:
- Filter enable/disable flag
- Sensitivity levels (LOW, MEDIUM, HIGH)
- Custom warning messages
- Keyword lists for different content categories

```yaml
airline:
  assistant:
    safeguard:
      enabled: true
      sensitivity-level: MEDIUM
      warning-message: "I understand you're looking for information, but I need to keep our conversation professional and appropriate for an airline customer service context."
      profanity-keywords: [...]
      inappropriate-keywords: [...]
      policy-violation-keywords: [...]
```

#### 2. SafeChatService
**Location**: `src/main/java/com/airline/assistant/service/SafeChatService.java`

Core service managing dual chat clients:
- Regular chat client (no filtering)
- Safe chat client (with SafeGuardAdvisor)
- Message processing with filtering
- Filter status information

**Key Methods**:
- `processMessage()` - Regular chat without filtering
- `processSafeMessage()` - Chat with content filtering
- `getFilterStatus()` - Current filter configuration status

#### 3. FilterAuditService
**Location**: `src/main/java/com/airline/assistant/service/FilterAuditService.java`

Audit and logging service for:
- Content filtering events
- Safe chat usage statistics
- Configuration changes
- Compliance reporting

#### 4. SafeGuardAdminController
**Location**: `src/main/java/com/airline/assistant/controller/SafeGuardAdminController.java`

Admin API endpoints for:
- Configuration viewing
- Filter statistics
- Content testing
- Sensitivity level updates

### Frontend Components

#### 1. Safe Mode Toggle
**Location**: `src/main/resources/static/index.html`

Visual toggle switch in the header area:
- Clear ON/OFF states
- Professional styling
- Real-time mode switching

#### 2. Toggle Styling
**Location**: `src/main/resources/static/styles.css`

CSS styling for:
- Toggle switch appearance
- Active/inactive states
- Notification animations
- Professional color scheme

#### 3. JavaScript Integration
**Location**: `src/main/resources/static/app.js`

Frontend logic for:
- Toggle state management
- API endpoint switching
- User notifications
- Visual feedback

## API Endpoints

### Chat Endpoints
- `POST /api/v1/chat` - Regular chat without filtering
- `POST /api/v1/chat/safe` - Safe chat with content filtering
- `GET /api/v1/chat/filter-status` - Current filter status

### Admin Endpoints
- `GET /api/v1/admin/safeguard/config` - View current configuration
- `GET /api/v1/admin/safeguard/stats` - Filter usage statistics
- `PUT /api/v1/admin/safeguard/sensitivity` - Update sensitivity level
- `POST /api/v1/admin/safeguard/test` - Test content filtering

## Configuration

### Sensitivity Levels

#### LOW Sensitivity
- Basic profanity filtering
- Minimal keyword restrictions
- Suitable for general audience

#### MEDIUM Sensitivity (Default)
- Moderate profanity and inappropriate content filtering
- Airline policy enforcement
- Professional conversation standards

#### HIGH Sensitivity
- Strict content filtering
- Comprehensive keyword blocking
- Maximum safety for sensitive environments

### Keyword Categories

#### Profanity Keywords
Common offensive language and profanity terms filtered across all sensitivity levels.

#### Inappropriate Keywords
Sexual, violent, or disturbing content inappropriate for professional airline customer service.

#### Policy Violation Keywords
Content violating airline policies, including security-related terms and unprofessional language.

## Usage

### For Users
1. **Toggle Safe Mode**: Use the toggle switch in the header to enable/disable content filtering
2. **Visual Feedback**: Toggle shows current state with clear ON/OFF indicators
3. **Notifications**: Brief notifications confirm mode changes
4. **Seamless Experience**: Automatic endpoint switching based on toggle state

### For Administrators
1. **Configuration Management**: View and update filter settings via admin endpoints
2. **Content Testing**: Test content against current filter rules
3. **Statistics Monitoring**: Track filter usage and effectiveness
4. **Audit Logging**: Review filtering events for compliance

## Integration

### Spring AI Integration
- Uses Spring AI's built-in `SafeGuardAdvisor`
- Integrated with `ChatClient` advisor chain
- Compatible with both OpenAI and Ollama profiles

### Configuration Integration
- Externalized configuration via `application.yml`
- Environment-specific settings support
- Runtime configuration updates (with restart)

### Logging Integration
- Structured logging with SLF4J
- Audit trail for compliance
- Performance monitoring

## Testing

### Functional Testing
- ✅ Content filtering with various input scenarios
- ✅ Toggle functionality and UI responsiveness
- ✅ API endpoint validation
- ✅ Configuration loading and validation

### Integration Testing
- ✅ SafeGuardAdvisor integration with ChatClient
- ✅ Frontend-backend communication
- ✅ Dual chat client configuration
- [ ] OpenAI and Ollama profile compatibility (pending)

### Performance Testing
- ✅ Application startup with SafeGuardAdvisor
- ✅ Memory usage and initialization
- [ ] Response time impact analysis (pending)

## Security Considerations

### Content Privacy
- No sensitive user content stored in logs
- Filter events logged without exposing full message content
- Audit trails for compliance without privacy violations

### Configuration Security
- Keyword lists externalized from code
- Admin endpoints require appropriate access controls
- Configuration changes logged for audit

## Future Enhancements

### Planned Features
- [ ] Machine learning-based content classification
- [ ] Custom keyword management UI
- [ ] Real-time filter rule updates
- [ ] Advanced reporting and analytics
- [ ] Integration with external content moderation services

### Performance Optimizations
- [ ] Keyword matching optimization
- [ ] Caching for frequently used filter rules
- [ ] Asynchronous audit logging
- [ ] Filter rule compilation

## Troubleshooting

### Common Issues

#### SafeGuardAdvisor Not Initializing
- Verify `airline.assistant.safeguard.enabled=true` in configuration
- Check keyword lists are properly formatted
- Ensure Spring AI dependencies are correctly configured

#### Toggle Not Working
- Verify JavaScript is enabled
- Check browser console for errors
- Ensure API endpoints are accessible

#### Content Not Being Filtered
- Verify safe mode is enabled
- Check keyword lists contain appropriate terms
- Review sensitivity level configuration

### Debug Logging
Enable debug logging for detailed troubleshooting:
```yaml
logging:
  level:
    com.airline.assistant.service.SafeChatService: DEBUG
    com.airline.assistant.service.FilterAuditService: DEBUG
```

## Compliance

### Audit Requirements
- All filter events logged with timestamps
- Configuration changes tracked
- User interaction patterns recorded (anonymized)
- Compliance reports available via admin endpoints

### Data Retention
- Filter event logs retained for compliance periods
- User message content not stored
- Configuration history maintained
- Audit trails preserved

## Support

For technical support or questions about the SafeGuardAdvisor system:
1. Review this documentation
2. Check application logs for error details
3. Use admin endpoints for configuration validation
4. Contact development team for advanced troubleshooting

---

**Last Updated**: June 5, 2025  
**Version**: 1.0  
**Status**: Production Ready
