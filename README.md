# Simple Blockchain Implementation

A learning project implementing blockchain fundamentals in Java. This project started as an educational exercise to understand core blockchain concepts including block creation, cryptographic hashing, proof-of-work mining, and chain validation. The project has evolved into a Spring Boot application with REST API capabilities.

## Learning Journey

This project began as a tutorial-based implementation to learn blockchain fundamentals. I've fixed bugs and extended it with a Spring Boot web application framework to provide REST API endpoints for blockchain interactions.

## Current Features

- **Block Creation**: Create blocks with data, timestamp, and cryptographic hashing
- **SHA-256 Hashing**: Uses SHA-256 algorithm to generate unique block hashes
- **Proof of Work**: Mining algorithm with configurable difficulty
- **Chain Validation**: Validates the integrity of the blockchain by checking hash consistency and mining status
- **JSON Serialization**: Converts blockchain to JSON format using Gson
- **Spring Boot Web Application**: RESTful API framework for blockchain interactions
- **REST API Endpoints**: Comprehensive REST controller for wallet and pet management
- **Wallet System**: Generate unique wallet addresses for players
- **Pet Data Model**: Pet class with unique ID, type, color, rarity, and ownership
- **PetService**: Complete game logic for creating pets, managing ownership, and trading
- **Transaction System**: Transaction model and history tracking for all pet operations
- **File Persistence**: Automatic save/load of blockchain and pet registry to JSON files
- **Transaction History API**: Query transaction history for pets and owners
- **System Statistics**: API endpoint for blockchain and pet statistics
- **Unit Tests**: JUnit tests for core blockchain functionality

## Planned Features

- **BlockPets Game**: Enhanced features for blockchain-based collectible pet game
- CLI interface for interactive use
- Frontend web interface
- Enhanced transaction system with signatures

## Project Structure

```
src/main/java/com/example/blockchain/
├── Block.java                  # Block class with hash calculation and mining
├── BlockchainApplication.java  # Spring Boot main application class
├── ChainHub.java              # Blockchain management and validation with persistence
├── Main.java                  # Simple demonstration of block creation
├── Pet.java                   # Pet data model (id, name, type, color, rarity, owner)
├── PetController.java         # REST API controller for wallet/pet operations
├── PetService.java            # Pet management logic, trading, and transaction parsing
├── PersistenceService.java    # Spring component for loading/saving data on startup/shutdown
├── StringUtil.java            # SHA-256 hash utility
├── Transaction.java           # Transaction model for blockchain operations
└── Wallet.java                # Wallet class for generating unique addresses

src/test/java/com/example/blockchain/
├── BlockTest.java             # Unit tests for Block class
└── ChainHubTest.java          # Unit tests for blockchain validation
```

## Requirements

- Java 24
- Maven 3.x
- Spring Boot 3.2.0
- Gson 2.10.1 (managed by Maven)
- JUnit 5 (included in spring-boot-starter-test)

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

**Note**: On startup, the application automatically loads persisted data from JSON files (if they exist). On shutdown, all data is automatically saved to disk.

### Running Tests

Run all unit tests:

```bash
mvn test
```

Run tests with verbose output:

```bash
mvn test -X
```

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

### Wallet Management
- `POST /api/wallet/create` - Create a new wallet
  - Returns: `{"address": "unique_wallet_address"}`

### Pet Management
- `POST /api/pet/create` - Create a new pet for an owner
  - Request body: `{"ownerAddress": "string", "petName": "string"}`
  - Returns: Pet object with id, name, type, color, rarity, owner, timestamp
- `GET /api/pet/{petId}` - Get a single pet by its ID
  - Returns: Pet object (404 if not found)
- `GET /api/pets/owner/{address}` - Get all pets owned by a specific address
  - Returns: Array of Pet objects
- `GET /api/pets/all` - Get all pets in the system
  - Returns: Array of all Pet objects
- `POST /api/pet/trade` - Trade a pet between owners
  - Request body: `{"petId": "string", "fromOwner": "string", "toOwner": "string"}`
  - Returns: `{"status": "success/error", "message": "string"}`

### Transaction History
- `GET /api/pet/{petId}/history` - Get complete transaction history for a specific pet
  - Returns: Array of Transaction objects (CREATE_PET and TRADE_PET operations)
- `GET /api/owner/{address}/transactions` - Get all transactions involving a specific owner
  - Returns: Array of Transaction objects where the owner is involved

### Blockchain Operations
- `GET /api/blockchain` - Get the complete blockchain
  - Returns: Array of Block objects
- `GET /api/blockchain/validate` - Validate the blockchain integrity
  - Returns: `{"valid": true/false}`

### Statistics
- `GET /api/stats` - Get system statistics
  - Returns: Object with totalPets, totalOwners, totalTransactions, blockchainSize, difficulty, blockchainValid

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

5. **Transaction System**: Transactions are stored in blocks as JSON strings:
   - `CREATE_PET`: Records pet creation with owner address and pet name
   - `TRADE_PET`: Records ownership transfer between addresses
   - Transaction history can be queried via API endpoints

6. **Data Persistence**: 
   - Blockchain and pet registry are automatically saved to JSON files
   - Data persists across application restarts
   - Files are created automatically on first save

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

## File Persistence

The application automatically persists data to the following JSON files in the project root:

- `blockchain.json` - Complete blockchain data
- `pets.json` - Pet registry with all pet data
- `pet_blockhash.json` - Mapping of pet IDs to block hashes

**Data persistence behavior:**
- Data is automatically loaded on application startup
- Blockchain is saved after each pet creation or trade operation
- All data is saved on application shutdown
- If files don't exist, the application starts with empty data

**Note**: These files are gitignored and won't be committed to version control.

## Technology Stack

- **Java 24**: Programming language
- **Spring Boot 3.2.0**: Application framework for building REST APIs
- **Maven**: Build tool and dependency management
- **Gson 2.10.1**: JSON serialization/deserialization library
- **JUnit 5**: Unit testing framework (via spring-boot-starter-test)

## Project Concept: BlockPets Game

This project is evolving into a blockchain-based collectible pet game where:
- Each pet is stored as a block on the blockchain
- Pets have unique traits (type, color, rarity) determined by their block hash
- Players have wallets with unique addresses
- Pets can be traded between players (recorded on blockchain)
- All pet ownership and trades are permanently stored on the blockchain

## Learning Resources

This implementation is based on blockchain tutorial concepts. The goal is to understand the fundamentals and extend the project with original features, including web API integration and real-world use cases like digital collectibles.

## License

This is an educational project for learning purposes.

## Author

Jorge Flores



