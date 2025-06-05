class AirlineAssistant {
    constructor() {
        this.chatMessages = document.getElementById('chatMessages');
        this.messageInput = document.getElementById('messageInput');
        this.sendButton = document.getElementById('sendButton');
        this.clearChatButton = document.getElementById('clearChatButton');
        this.loadingIndicator = document.getElementById('loadingIndicator');
        this.characterCount = document.querySelector('.character-count');
        this.aiProviderStatus = document.getElementById('aiProviderStatus');
        
        // User profile elements
        this.profileSidebar = document.getElementById('userProfileSidebar');
        this.profileToggleBtn = document.getElementById('profileToggleBtn');
        this.closeProfileBtn = document.getElementById('closeProfileBtn');
        this.userSearchInput = document.getElementById('userSearchInput');
        this.searchResults = document.getElementById('searchResults');
        this.profileContent = document.getElementById('profileContent');
        this.container = document.querySelector('.container');
        
        this.initializeEventListeners();
        this.adjustTextareaHeight();
        this.loadAiProviderInfo();
    }

    initializeEventListeners() {
        // Send button click
        this.sendButton.addEventListener('click', () => this.sendMessage());
        
        // Enter key to send (Shift+Enter for new line)
        this.messageInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.sendMessage();
            }
        });
        
        // Auto-resize textarea and update character count
        this.messageInput.addEventListener('input', () => {
            this.adjustTextareaHeight();
            this.updateCharacterCount();
        });
        
        // Clear chat button click
        this.clearChatButton.addEventListener('click', () => this.clearChat());
        
        // Profile sidebar toggle
        this.profileToggleBtn.addEventListener('click', () => this.toggleProfileSidebar());
        this.closeProfileBtn.addEventListener('click', () => this.closeProfileSidebar());
        
        // User search functionality
        this.userSearchInput.addEventListener('input', (e) => this.searchUsers(e.target.value));
        
        // Initial character count
        this.updateCharacterCount();
    }

    adjustTextareaHeight() {
        this.messageInput.style.height = 'auto';
        this.messageInput.style.height = Math.min(this.messageInput.scrollHeight, 120) + 'px';
    }

    updateCharacterCount() {
        const count = this.messageInput.value.length;
        this.characterCount.textContent = `${count}/1000`;
        
        if (count > 900) {
            this.characterCount.style.color = '#e74c3c';
        } else if (count > 800) {
            this.characterCount.style.color = '#f39c12';
        } else {
            this.characterCount.style.color = '#999';
        }
    }

    async sendMessage() {
        const message = this.messageInput.value.trim();
        if (!message || this.sendButton.disabled) return;

        // Add user message to chat
        this.addMessage(message, 'user');
        
        // Clear input and reset height
        this.messageInput.value = '';
        this.adjustTextareaHeight();
        this.updateCharacterCount();
        
        // Disable send button and show loading
        this.setLoading(true);
        
        try {
            const response = await this.callChatAPI(message);
            this.addMessage(response, 'assistant');
        } catch (error) {
            console.error('Error calling chat API:', error);
            this.addMessage(
                'Sorry, I encountered an error while processing your request. Please try again.',
                'assistant'
            );
        } finally {
            this.setLoading(false);
        }
    }

    async callChatAPI(message) {
        const response = await fetch('/api/v1/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ message: message })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data.response || 'Sorry, I could not generate a response.';
    }

    addMessage(content, type) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `message ${type}-message`;
        
        const avatar = document.createElement('div');
        avatar.className = 'message-avatar';
        avatar.textContent = type === 'user' ? '👤' : '🤖';
        
        const messageContent = document.createElement('div');
        messageContent.className = 'message-content';
        
        // Create message header with timestamp
        const messageHeader = document.createElement('div');
        messageHeader.className = 'message-header';
        
        const timestamp = document.createElement('span');
        timestamp.className = 'message-timestamp';
        timestamp.textContent = this.formatTimestamp(new Date());
        messageHeader.appendChild(timestamp);
        messageContent.appendChild(messageHeader);
        
        // Check if we're adding an assistant message (might contain formatted HTML for lists)
        if (type === 'assistant') {
            // Handle potentially HTML-formatted lists by using innerHTML
            messageContent.innerHTML += `<p>${content}</p>`;
        } else {
            // For user messages, use textContent as before
            const paragraph = document.createElement('p');
            paragraph.textContent = content;
            messageContent.appendChild(paragraph);
        }
        
        messageDiv.appendChild(avatar);
        messageDiv.appendChild(messageContent);
        
        this.chatMessages.appendChild(messageDiv);
        this.scrollToBottom();
    }

    scrollToBottom() {
        this.chatMessages.scrollTop = this.chatMessages.scrollHeight;
    }

    setLoading(isLoading) {
        this.sendButton.disabled = isLoading;
        this.messageInput.disabled = isLoading;
        
        if (isLoading) {
            this.loadingIndicator.classList.remove('hidden');
        } else {
            this.loadingIndicator.classList.add('hidden');
        }
    }

    // Format timestamp to a readable string
    formatTimestamp(date) {
        const now = new Date();
        const diffMs = now - date;
        const diffSec = Math.round(diffMs / 1000);
        const diffMin = Math.round(diffSec / 60);
        const diffHour = Math.round(diffMin / 60);
        
        // If less than a minute ago
        if (diffSec < 60) {
            return 'Just now';
        }
        // If less than an hour ago
        else if (diffMin < 60) {
            return `${diffMin} ${diffMin === 1 ? 'minute' : 'minutes'} ago`;
        }
        // If less than a day ago
        else if (diffHour < 24) {
            return `${diffHour} ${diffHour === 1 ? 'hour' : 'hours'} ago`;
        }
        // Otherwise show the date
        else {
            return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' ' + 
                   date.toLocaleDateString();
        }
    }
    
    // Clear chat history/memory and UI
    async clearChat() {
        try {
            this.setLoading(true);
            
            // Call the API to clear memory
            const response = await fetch('/api/v1/chat/memory', {
                method: 'DELETE',
                credentials: 'include'
            });
            
            if (response.ok) {
                // Clear UI
                const initialMessage = this.chatMessages.firstElementChild;
                this.chatMessages.innerHTML = '';
                if (initialMessage) {
                    this.chatMessages.appendChild(initialMessage);
                }
                
                // Add confirmation message
                this.addMessage('Chat history has been cleared. How else can I assist you?', 'assistant');
            } else {
                console.error('Failed to clear chat memory');
                this.addMessage('Sorry, I was unable to clear the chat history. Please try again.', 'assistant');
            }
        } catch (error) {
            console.error('Error clearing chat memory:', error);
            this.addMessage('Sorry, I encountered an error while clearing the chat history.', 'assistant');
        } finally {
            this.setLoading(false);
        }
    }
    
    // Method to interact with memory API
    clearChatMemory() {
        return fetch('/api/v1/chat/memory', {
            method: 'DELETE',
            credentials: 'include'
        });
    }

    toggleProfileSidebar() {
        this.profileSidebar.classList.toggle('open');
        this.container.classList.toggle('sidebar-open');
    }

    closeProfileSidebar() {
        this.profileSidebar.classList.remove('open');
        this.container.classList.remove('sidebar-open');
    }

    async searchUsers(query) {
        if (!query || query.trim().length < 2) {
            this.searchResults.innerHTML = '';
            return;
        }

        try {
            const response = await fetch(`/api/v1/users/search?query=${encodeURIComponent(query.trim())}`);
            if (response.ok) {
                const users = await response.json();
                this.displaySearchResults(users);
            } else {
                console.error('Failed to search users');
                this.searchResults.innerHTML = '<div class="search-error">Failed to search users</div>';
            }
        } catch (error) {
            console.error('Error searching users:', error);
            this.searchResults.innerHTML = '<div class="search-error">Error searching users</div>';
        }
    }

    displaySearchResults(users) {
        if (users.length === 0) {
            this.searchResults.innerHTML = '<div class="search-no-results">No users found</div>';
            return;
        }

        const resultsHtml = users.map(user => `
            <div class="search-result-item" data-username="${user.username}">
                <div class="search-result-name">${user.lastName || user.username}</div>
                <div class="search-result-details">@${user.username} • ${user.loyaltyStatus} • ${user.airline}</div>
            </div>
        `).join('');

        this.searchResults.innerHTML = resultsHtml;
        
        // Add click event listeners to search results
        this.searchResults.querySelectorAll('.search-result-item').forEach(item => {
            item.addEventListener('click', () => {
                const username = item.getAttribute('data-username');
                this.loadUserProfile(username);
            });
        });
    }

    async loadUserProfile(username) {
        console.log('Loading user profile for:', username);
        try {
            const response = await fetch(`/api/v1/users/${encodeURIComponent(username)}`);
            console.log('Profile API response status:', response.status);
            if (response.ok) {
                const userProfile = await response.json();
                console.log('User profile data:', userProfile);
                this.displayUserProfile(userProfile);
                this.searchResults.innerHTML = '';
                this.userSearchInput.value = '';
            } else {
                console.error('Failed to load user profile');
                this.profileContent.innerHTML = '<div class="profile-error">Failed to load user profile</div>';
            }
        } catch (error) {
            console.error('Error loading user profile:', error);
            this.profileContent.innerHTML = '<div class="profile-error">Error loading user profile</div>';
        }
    }

    displayUserProfile(user) {
        const loyaltyStatusClass = user.loyaltyStatus.toLowerCase().replace(/\s+/g, '-');
        const displayName = user.lastName || user.username;
        const initials = user.lastName ? user.lastName.substring(0, 2).toUpperCase() : user.username.substring(0, 2).toUpperCase();
        
        const profileHtml = `
            <div class="user-profile-card">
                <div class="profile-card-header">
                    <div class="profile-avatar">${initials}</div>
                    <div class="profile-card-info">
                        <h4>${displayName}</h4>
                        <div class="username">@${user.username}</div>
                    </div>
                </div>
                
                <div class="profile-details">
                    <div class="profile-field">
                        <div class="profile-field-label">Loyalty Status</div>
                        <div class="profile-field-value">
                            <span class="loyalty-status ${loyaltyStatusClass}">
                                ${this.getLoyaltyIcon(user.loyaltyStatus)} ${user.loyaltyStatus}
                            </span>
                        </div>
                    </div>
                    
                    <div class="profile-field">
                        <div class="profile-field-label">Loyalty Number</div>
                        <div class="profile-field-value">${user.loyaltyNumber}</div>
                    </div>
                    
                    <div class="profile-field">
                        <div class="profile-field-label">Airline</div>
                        <div class="profile-field-value">${user.airline}</div>
                    </div>
                    
                    <div class="profile-field">
                        <div class="profile-field-label">Home Airport</div>
                        <div class="profile-field-value">${user.preferredAirport}</div>
                    </div>
                </div>
            </div>
        `;
        
        this.profileContent.innerHTML = profileHtml;
    }

    getLoyaltyIcon(status) {
        const icons = {
            'Gold': '🥇',
            'Silver': '🥈',
            'Platinum': '💎',
            'Diamond': '💎',
            'Basic': '🎫',
            'Bronze': '🥉'
        };
        return icons[status] || '🎫';
    }

    async loadAiProviderInfo() {
        try {
            const response = await fetch('/api/v1/ai-provider');
            if (response.ok) {
                const data = await response.json();
                const providerIndicator = this.aiProviderStatus.querySelector('.provider-indicator');
                
                // Determine icon and class based on provider
                let icon = '🤖';
                let cssClass = '';
                
                if (data.provider.toLowerCase().includes('openai')) {
                    icon = '☁️';
                    cssClass = 'openai';
                } else if (data.provider.toLowerCase().includes('ollama')) {
                    icon = '🏠';
                    cssClass = 'ollama';
                }
                
                providerIndicator.textContent = `${icon} ${data.provider} - ${data.model}`;
                providerIndicator.className = `provider-indicator ${cssClass}`;
            } else {
                console.error('Failed to load AI provider info');
                const providerIndicator = this.aiProviderStatus.querySelector('.provider-indicator');
                providerIndicator.textContent = '❌ Failed to load AI provider info';
            }
        } catch (error) {
            console.error('Error loading AI provider info:', error);
            const providerIndicator = this.aiProviderStatus.querySelector('.provider-indicator');
            providerIndicator.textContent = '❌ Error loading AI provider info';
        }
    }
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new AirlineAssistant();
});
