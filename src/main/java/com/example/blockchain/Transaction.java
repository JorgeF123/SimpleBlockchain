package com.example.blockchain;

/**
 * Represents a transaction stored in the blockchain.
 * Transactions can be CREATE_PET or TRADE_PET operations.
 */
public class Transaction {
    private String type;  // "CREATE_PET" or "TRADE_PET"
    private String petId;
    private String ownerAddress;  // For CREATE_PET
    private String fromOwner;     // For TRADE_PET
    private String toOwner;       // For TRADE_PET
    private String petName;       // For CREATE_PET
    private long timestamp;
    private String blockHash;     // Hash of the block containing this transaction
    private long blockTimestamp;  // Timestamp of the block

    // Default constructor for Gson
    public Transaction() {
    }

    // Full constructor - all fields can be set, but only relevant ones are used based on type
    public Transaction(String type, String petId, String ownerAddress, String fromOwner, 
                      String toOwner, String petName, long timestamp, String blockHash, 
                      long blockTimestamp) {
        this.type = type;
        this.petId = petId;
        this.ownerAddress = ownerAddress;
        this.fromOwner = fromOwner;
        this.toOwner = toOwner;
        this.petName = petName;
        this.timestamp = timestamp;
        this.blockHash = blockHash;
        this.blockTimestamp = blockTimestamp;
    }

    // Getters
    public String getType() { return type; }
    public String getPetId() { return petId; }
    public String getOwnerAddress() { return ownerAddress; }
    public String getFromOwner() { return fromOwner; }
    public String getToOwner() { return toOwner; }
    public String getPetName() { return petName; }
    public long getTimestamp() { return timestamp; }
    public String getBlockHash() { return blockHash; }
    public long getBlockTimestamp() { return blockTimestamp; }

    // Setters (for Gson deserialization)
    public void setType(String type) { this.type = type; }
    public void setPetId(String petId) { this.petId = petId; }
    public void setOwnerAddress(String ownerAddress) { this.ownerAddress = ownerAddress; }
    public void setFromOwner(String fromOwner) { this.fromOwner = fromOwner; }
    public void setToOwner(String toOwner) { this.toOwner = toOwner; }
    public void setPetName(String petName) { this.petName = petName; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setBlockHash(String blockHash) { this.blockHash = blockHash; }
    public void setBlockTimestamp(long blockTimestamp) { this.blockTimestamp = blockTimestamp; }
}

