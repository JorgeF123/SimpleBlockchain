# Simple Blockchain Implementation

A learning project implementing blockchain fundamentals in Java. This project started as an educational exercise to understand core blockchain concepts including block creation, cryptographic hashing, proof-of-work mining, and chain validation. The project has evolved into a Spring Boot application with REST API capabilities.

## Learning Journey

This project began as a tutorial-based implementation to learn blockchain fundamentals. I've fixed bugs (nonce calculation) and extended it with a Spring Boot web application framework to provide REST API endpoints for blockchain interactions.

## Current Features

- **Block Creation**: Create blocks with data, timestamp, and cryptographic hashing
- **SHA-256 Hashing**: Uses SHA-256 algorithm to generate unique block hashes
- **Proof of Work**: Mining algorithm with configurable difficulty
- **Chain Validation**: Validates the integrity of the blockchain by checking hash consistency and mining status
- **JSON Serialization**: Converts blockchain to JSON format using Gson
- **Spring Boot Web Application**: RESTful API framework for blockchain interactions
- **REST API Endpoints**: REST controller for wallet and pet management (in development)

## Planned Features

- Transaction system (replace simple string data)
- File persistence (save/load blockchain)
- CLI interface for interactive use
- Unit tests with JUnit
- Complete Wallet and Pet management system implementation

## Project Structure

```
src/main/java/com/example/blockchain/
├── Block.java                  # Block class with hash calculation and mining
├── BlockchainApplication.java  # Spring Boot main application class
├── ChainHub.java              # Blockchain management and validation
├── Main.java                  # Simple demonstration of block creation
├── PetController.java         # REST API controller for wallet/pet operations
└── StringUtil.java            # SHA-256 hash utility
```

## Requirements

- Java 24
- Maven 3.x
- Spring Boot 3.2.0
- Gson 2.10.1 (managed by Maven)

## Building the Project

```bash
mvn clean compile
```

To build a JAR file:

```bash
mvn clean package
```

## Running the Project

### Option 1: Run Spring Boot Application (Recommended)
Starts the Spring Boot web server with REST API endpoints:

```bash
mvn spring-boot:run
```

Or using the compiled JAR:

```bash
java -jar target/blockChain-1.0-SNAPSHOT.jar
```

The application will start on `http://localhost:8080` by default.

### Option 2: Run Main.java
Demonstrates basic block creation without mining:

```bash
mvn exec:java -Dexec.mainClass="com.example.blockchain.Main"
```

### Option 3: Run ChainHub.java
Demonstrates blockchain management with mining and JSON output:

```bash
mvn exec:java -Dexec.mainClass="com.example.blockchain.ChainHub"
```

## API Endpoints

The Spring Boot application provides REST API endpoints:

- `POST /api/wallet/create` - Create a new wallet (returns wallet address)
- `POST /api/pet/create` - Create a new pet for an owner
  - Request body: `{"ownerAddress": "string", "petName": "string"}`
- `GET /api/pets/owner/{address}` - Get all pets owned by a specific address

*Note: Some API endpoints may reference classes that are still in development.*

## How It Works

1. **Block Structure**: Each block contains:
   - `data`: The information stored in the block (currently a simple string)
   - `previousHash`: Hash of the previous block (creates the chain linkage)
   - `timeStamp`: Timestamp when the block was created (milliseconds since epoch)
   - `hash`: SHA-256 hash of the block's contents
   - `nonce`: Number used during mining to find a valid hash

2. **Hash Calculation**: The hash is calculated using:
   ```
   SHA-256(previousHash + timestamp + nonce + data)
   ```
   The nonce is incremented during mining to find a hash that meets the difficulty requirement (a hash starting with a certain number of zeros).

3. **Proof of Work Mining**: The `mineBlock(difficulty)` method:
   - Sets a target hash pattern (e.g., "00000..." for difficulty 5)
   - Incrementally tries different nonce values
   - Recalculates the hash until it matches the target pattern
   - This computationally expensive process secures the blockchain

4. **Chain Validation**: The `isChainValid()` method checks:
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

## Technology Stack

- **Java 24**: Programming language
- **Spring Boot 3.2.0**: Application framework for building REST APIs
- **Maven**: Build tool and dependency management
- **Gson 2.10.1**: JSON serialization/deserialization library

## Learning Resources

This implementation is based on blockchain tutorial concepts. The goal is to understand the fundamentals and extend the project with original features, including web API integration and real-world use cases.

## License

This is an educational project for learning purposes.

## Author

Jorge Flores



