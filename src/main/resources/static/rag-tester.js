class RagTester {
    constructor() {
        this.queryInput = document.getElementById('queryInput');
        this.resultLimit = document.getElementById('resultLimit');
        this.queryButton = document.getElementById('queryButton');
        this.documentResults = document.getElementById('documentResults');
        this.contextResult = document.getElementById('contextResult');
        this.documentCount = document.getElementById('documentCount');
        this.loadingIndicator = document.getElementById('loadingIndicator');
        this.tabButtons = document.querySelectorAll('.tab-button');
        this.tabContents = document.querySelectorAll('.tab-content');
        
        this.initializeEventListeners();
        this.loadKnowledgeBaseInfo();
    }

    initializeEventListeners() {
        // Query button click
        this.queryButton.addEventListener('click', () => this.performQuery());
        
        // Enter key to send query
        this.queryInput.addEventListener('keydown', (e) => {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                this.performQuery();
            }
        });
        
        // Tab switching
        this.tabButtons.forEach(button => {
            button.addEventListener('click', () => {
                const tabName = button.getAttribute('data-tab');
                this.switchTab(tabName);
            });
        });
    }

    switchTab(tabName) {
        // Update active tab button
        this.tabButtons.forEach(button => {
            if (button.getAttribute('data-tab') === tabName) {
                button.classList.add('active');
            } else {
                button.classList.remove('active');
            }
        });
        
        // Show selected tab content
        this.tabContents.forEach(content => {
            if (content.id === `${tabName}Tab`) {
                content.classList.remove('hidden');
            } else {
                content.classList.add('hidden');
            }
        });
    }

    async loadKnowledgeBaseInfo() {
        try {
            const response = await fetch('/api/v1/knowledge/info');
            
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            
            const data = await response.json();
            this.documentCount.textContent = `Knowledge Base: ${data.documentCount} documents`;
            
        } catch (error) {
            console.error('Error loading knowledge base info:', error);
            this.documentCount.textContent = 'Error loading knowledge base info';
        }
    }

    async performQuery() {
        const query = this.queryInput.value.trim();
        const limit = parseInt(this.resultLimit.value);
        
        if (!query || this.queryButton.disabled) return;
        
        // Show loading indicator
        this.setLoading(true);
        
        try {
            const response = await this.callQueryAPI(query, limit);
            this.displayResults(response);
        } catch (error) {
            console.error('Error querying knowledge base:', error);
            this.displayError('Sorry, an error occurred while querying the knowledge base. Please try again.');
        } finally {
            this.setLoading(false);
        }
    }

    async callQueryAPI(query, limit) {
        const response = await fetch('/api/v1/knowledge/query', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ query: query, limit: limit })
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    displayResults(data) {
        // Display documents
        if (data.documents && data.documents.length > 0) {
            this.documentResults.innerHTML = '';
            
            data.documents.forEach(doc => {
                const documentCard = document.createElement('div');
                documentCard.className = 'document-card';
                
                const source = doc.metadata.source || 'Unknown source';
                const chunk = doc.metadata.chunk !== undefined ? ` (Chunk ${doc.metadata.chunk})` : '';
                
                documentCard.innerHTML = `
                    <div class="document-header">
                        <div class="document-source">${source}${chunk}</div>
                        <div class="document-metadata">${this.formatMetadata(doc.metadata)}</div>
                    </div>
                    <div class="document-content">${doc.content}</div>
                `;
                
                this.documentResults.appendChild(documentCard);
            });
        } else {
            this.documentResults.innerHTML = '<div class="placeholder-message">No matching documents found.</div>';
        }
        
        // Display formatted context
        if (data.formattedContext && data.formattedContext.trim()) {
            this.contextResult.innerHTML = `<pre>${this.escapeHtml(data.formattedContext)}</pre>`;
        } else {
            this.contextResult.innerHTML = '<div class="placeholder-message">No context generated for this query.</div>';
        }
    }

    displayError(message) {
        this.documentResults.innerHTML = `<div class="placeholder-message">${message}</div>`;
        this.contextResult.innerHTML = `<div class="placeholder-message">${message}</div>`;
    }

    formatMetadata(metadata) {
        // Filter out some metadata fields for cleaner display
        const filtered = { ...metadata };
        delete filtered.source;
        delete filtered.filename;
        delete filtered.type;
        
        return Object.keys(filtered).length > 0 
            ? JSON.stringify(filtered)
            : '';
    }

    escapeHtml(text) {
        return text
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    setLoading(isLoading) {
        this.queryButton.disabled = isLoading;
        this.queryInput.disabled = isLoading;
        
        if (isLoading) {
            this.loadingIndicator.classList.remove('hidden');
        } else {
            this.loadingIndicator.classList.add('hidden');
        }
    }
}

// Initialize the application when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new RagTester();
});