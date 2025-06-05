class AirlineAssistant {
    constructor() {
        this.chatMessages = document.getElementById('chatMessages');
        this.messageInput = document.getElementById('messageInput');
        this.sendButton = document.getElementById('sendButton');
        this.clearChatButton = document.getElementById('clearChatButton');
        this.loadingIndicator = document.getElementById('loadingIndicator');
        this.characterCount = document.querySelector('.character-count');
        
        this.initializeEventListeners();
        this.adjustTextareaHeight();
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
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new AirlineAssistant();
});
