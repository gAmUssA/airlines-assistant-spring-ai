# Memory Optimization Guide

## OutOfMemoryError Issue

The application may encounter a `java.lang.OutOfMemoryError: Java heap space` error when running with default JVM memory settings. This issue occurs during the initialization of the knowledge base, specifically in the `KnowledgeBaseService.chunkText` method, which processes and chunks the content of the knowledge base files.

## Root Causes

1. **Inefficient Chunking Algorithm**: The original chunking algorithm could potentially create too many chunks or get stuck in an infinite loop if it couldn't find suitable break points in the text.
2. **No Limit on Chunks**: There was no limit on the number of chunks that could be created, leading to excessive memory usage.
3. **Processing Unnecessary Files**: The README.md file was being processed as part of the knowledge base, even though it contains instructions rather than actual knowledge base content.

## Implemented Solutions

The following optimizations have been implemented to address the memory issues:

1. **Improved Chunking Algorithm**:
   - Added tracking of previous start positions to detect when the algorithm isn't making progress
   - Added safety checks to ensure the algorithm always makes progress in each iteration
   - Added a condition to break out of the loop if the start position goes out of bounds

2. **Limited Number of Chunks**:
   - Added a maximum limit (100) on the number of chunks that can be created from a single document
   - Added a warning log if the maximum chunk limit is reached

3. **Excluded README.md**:
   - Added a filter to exclude the README.md file from being processed as part of the knowledge base

## Running with Increased Memory

If you still encounter memory issues, you can run the application with increased JVM heap size using one of the following methods:

### Using the Makefile

```bash
make run-with-memory
```

This command runs the application with the following JVM options:
- Maximum heap size: 512MB (`-Xmx512m`)
- Initial heap size: 256MB (`-Xms256m`)

### Using the run.sh Script

```bash
./run.sh
```

This script sets the same JVM options and runs the application.

### Setting JVM Options Manually

```bash
JAVA_OPTS="-Xmx512m -Xms256m" ./gradlew bootRun
```

## Customizing Memory Settings

If you need to further customize the memory settings, you can modify the `run.sh` script or the `run-with-memory` target in the Makefile. The key JVM options are:

- `-Xmx<size>`: Maximum heap size (e.g., `-Xmx512m` for 512MB)
- `-Xms<size>`: Initial heap size (e.g., `-Xms256m` for 256MB)

For very large knowledge bases, you might need to increase these values further.