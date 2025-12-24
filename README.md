# Simple Blockchain Implementation

A learning project implementing blockchain fundamentals in Java. This project started as an educational exercise to understand core blockchain concepts including block creation, cryptographic hashing, proof-of-work mining, and chain validation.

## Learning Journey

This project began as a tutorial-based implementation to learn blockchain fundamentals. I've fixed bugs (nonce calculation) and plan to extend it with additional features to deepen my understanding.

## Current Features

- **Block Creation**: Create blocks with data, timestamp, and cryptographic hashing
- **SHA-256 Hashing**: Uses SHA-256 algorithm to generate unique block hashes
- **Proof of Work**: Mining algorithm with configurable difficulty
- **Chain Validation**: Validates the integrity of the blockchain by checking hash consistency and mining status
- **JSON Serialization**: Converts blockchain to JSON format using Gson

## Planned Features

- Transaction system (replace simple string data)
- File persistence (save/load blockchain)
- CLI interface for interactive use
- Unit tests with JUnit
- REST API endpoints

## Project Structure

```
src/main/java/com/example/blockchain/
├── Block.java          # Block class with hash calculation
├── ChainHub.java       # Blockchain management and validation
├── Main.java           # Simple demonstration of block creation
└── StringUtil.java     # SHA-256 hash utility
```

## Requirements

- Java 24
- Maven 3.x
- Gson 2.10.1 (managed by Maven)

## Building the Project

```bash
mvn clean compile
```

## Running the Project

### Option 1: Run Main.java
Demonstrates basic block creation:

```bash
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

### Option 2: Run ChainHub.java
Demonstrates blockchain management with JSON output:

```bash
mvn exec:java -Dexec.mainClass="com.example.blockchain.ChainHub"
```

## How It Works

1. **Block Structure**: Each block contains:
   - `data`: The information stored in the block
   - `previousHash`: Hash of the previous block (creates the chain)
   - `timeStamp`: Timestamp when the block was created
   - `hash`: SHA-256 hash of the block's contents

2. **Hash Calculation**: The hash is calculated using:
   ```
   SHA-256(previousHash + timestamp + nonce + data)
   ```
   The nonce is incremented during mining to find a hash that meets the difficulty requirement.

3. **Chain Validation**: The `isChainValid()` method checks:
   - Each block's hash matches its calculated hash
   - Each block's `previousHash` matches the previous block's hash
   - Each block has been properly mined (hash meets difficulty requirement)

## Example Output

When running `ChainHub.java`, you'll see the blockchain serialized as JSON:

```json
[
  {
    "hash": "...",
    "previousHash": "0",
    "timeStamp": 1234567890
  },
  {
    "hash": "...",
    "previousHash": "...",
    "timeStamp": 1234567891
  }
]
```

## Learning Resources

This implementation is based on blockchain tutorial concepts. The goal is to understand the fundamentals and extend the project with original features.

## License

This is an educational project for learning purposes.

## Author

Jorge Flores



