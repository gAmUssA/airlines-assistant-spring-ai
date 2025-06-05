# Testing the RAG (Retrieval-Augmented Generation) Functionality

This document provides instructions on how to test the RAG functionality in the Airline Assistant application.

## What is RAG?

RAG (Retrieval-Augmented Generation) is a technique that enhances large language model responses by retrieving relevant information from a knowledge base before generating a response. This helps the model provide more accurate and contextually relevant answers.

In our application, RAG is implemented using:
1. A knowledge base of airline loyalty program information
2. A vector store for efficient retrieval
3. A service that retrieves relevant context based on user queries

## Testing the RAG Functionality

### Prerequisites

1. Make sure you have set up your OpenAI API key in the environment variables:
   ```
   export OPENAI_API_KEY=your-api-key-here
   ```

2. Ensure the application is running:
   ```
   ./gradlew bootRun
   ```

### Manual Testing via the Web Interface

#### Using the Chat Interface

1. Open your browser and navigate to `http://localhost:9080`
2. Use the chat interface to ask questions about airline loyalty programs
3. The application will automatically retrieve relevant information from the knowledge base and use it to enhance the AI's response

#### Using the RAG Testing Interface

For direct testing of the RAG functionality:

1. Open your browser and navigate to `http://localhost:9080/rag-tester.html`
2. Enter your query in the input field
3. Select the number of results you want to see
4. Click the "Search" button
5. View the raw documents that match your query in the "Raw Documents" tab
6. View the formatted context that would be sent to the AI in the "Formatted Context" tab

### Example Queries to Test

Try the following queries to test the RAG functionality:

#### Delta SkyMiles Questions
- "Do Delta SkyMiles expire?"
- "What are the benefits of Delta Gold Medallion status?"
- "How many miles do I need to earn Delta Platinum status?"
- "What is the difference between Delta Silver and Gold status?"

#### United MileagePlus Questions
- "Do United MileagePlus miles expire?"
- "What are PlusPoints in United MileagePlus?"
- "How do I earn Premier status with United?"
- "What benefits do United Premier Gold members get?"

#### General Loyalty Program Questions
- "Which airline has better elite status benefits, Delta or United?"
- "How can I earn miles without flying?"
- "What's the best way to use airline miles for maximum value?"
- "Can I transfer miles between Delta and United programs?"

### Verifying RAG is Working

You can verify that RAG is working by:

1. **Accuracy of Responses**: The responses should contain specific details about the loyalty programs that match the information in the knowledge base files.

2. **Specificity**: The responses should be tailored to the specific airline program you're asking about, not generic information.

3. **Source Attribution**: In some cases, the AI might mention where it got the information (e.g., "According to the Delta SkyMiles program...").

4. **Comparing with and without RAG**: If you disable the KnowledgeBaseService (by commenting out the relevant code in AirlineAssistantService), you'll notice that the responses become more generic and potentially less accurate.

## Debugging RAG

If you want to see what's happening behind the scenes:

1. Check the application logs for debug information about the RAG process:
   ```
   tail -f logs/application.log
   ```

2. Look for log entries that show:
   - The user query being processed
   - The relevant context being retrieved from the knowledge base
   - The enhanced prompt being sent to the AI
   - The final response being returned

## Adding to the Knowledge Base

You can extend the knowledge base by adding more markdown files to the `src/main/resources/knowledge-base` directory. The application will automatically process these files and make them available for retrieval.

When adding new files, follow these guidelines:
1. Use clear, structured markdown
2. Organize information with headings and subheadings
3. Keep chunks of related information together
4. Restart the application after adding new files to rebuild the vector store
