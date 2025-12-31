package com.example.blockchain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BlockTest {

    @BeforeEach
    void setUp() {
        // Clear blockchain before each test
        ChainHub.blockChain.clear();
    }

    @Test
    void testBlockCreation() {
        String data = "Test data";
        String previousHash = "0";
        
        Block block = new Block(data, previousHash);
        
        assertNotNull(block);
        assertNotNull(block.hash);
        assertNotNull(block.previousHash);
        assertEquals(previousHash, block.previousHash);
        assertEquals(data, block.getData());
        assertTrue(block.getTimeStamp() > 0);
        assertEquals(0, block.getNonce()); // Initial nonce should be 0
    }

    @Test
    void testHashCalculation() {
        Block block1 = new Block("Data 1", "0");
        Block block2 = new Block("Data 1", "0");
        
        // Same data and previous hash should produce same initial hash
        // (before mining changes the nonce)
        String hash1 = block1.calculateHash();
        String hash2 = block2.calculateHash();
        
        // Note: Hashes might differ slightly due to timestamp, but structure should be valid
        assertNotNull(hash1);
        assertNotNull(hash2);
        assertEquals(64, hash1.length()); // SHA-256 produces 64 char hex string
    }

    @Test
    void testMining() {
        Block block = new Block("Test data", "0");
        int difficulty = 3;
        
        // Store original hash
        String originalHash = block.hash;
        
        // Mine the block
        block.mineBlock(difficulty);
        
        // Hash should start with difficulty number of zeros
        assertTrue(block.hash.startsWith("000"));
        // Hash should have changed after mining
        assertNotEquals(originalHash, block.hash);
        // Nonce should have been incremented
        assertTrue(block.getNonce() > 0);
    }

    @Test
    void testBlockHashStartsWithZerosAfterMining() {
        Block block = new Block("Mining test", "0");
        int difficulty = 2;
        
        block.mineBlock(difficulty);
        
        String target = "00";
        assertTrue(block.hash.startsWith(target), 
                   "Hash should start with " + difficulty + " zeros");
    }

    @Test
    void testDifferentDataProducesDifferentHash() {
        Block block1 = new Block("Data 1", "0");
        Block block2 = new Block("Data 2", "0");
        
        // Even if they have different timestamps, they should have different hashes
        assertNotEquals(block1.hash, block2.hash);
    }
}

