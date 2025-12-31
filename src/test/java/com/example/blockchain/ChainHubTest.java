package com.example.blockchain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class ChainHubTest {

    @BeforeEach
    void setUp() {
        // Clear blockchain before each test
        ChainHub.blockChain.clear();
        ChainHub.difficulty = 2; // Lower difficulty for faster tests
    }

    @Test
    void testEmptyBlockchainIsValid() {
        assertTrue(ChainHub.isChainValid(), 
                   "An empty blockchain should be considered valid");
    }

    @Test
    void testSingleBlockChainIsValid() {
        Block genesisBlock = new Block("Genesis", "0");
        genesisBlock.mineBlock(ChainHub.difficulty);
        ChainHub.blockChain.add(genesisBlock);
        
        assertTrue(ChainHub.isChainValid(), 
                   "A single mined block should be valid");
    }

    @Test
    void testValidChain() {
        // Create genesis block
        Block genesisBlock = new Block("Genesis", "0");
        genesisBlock.mineBlock(ChainHub.difficulty);
        ChainHub.blockChain.add(genesisBlock);
        
        // Create second block
        Block secondBlock = new Block("Second", genesisBlock.hash);
        secondBlock.mineBlock(ChainHub.difficulty);
        ChainHub.blockChain.add(secondBlock);
        
        assertTrue(ChainHub.isChainValid(), 
                   "A valid chain of blocks should pass validation");
    }

    @Test
    void testInvalidChainWithTamperedData() {
        // Create valid chain
        Block genesisBlock = new Block("Genesis", "0");
        genesisBlock.mineBlock(ChainHub.difficulty);
        ChainHub.blockChain.add(genesisBlock);
        
        Block secondBlock = new Block("Second", genesisBlock.hash);
        secondBlock.mineBlock(ChainHub.difficulty);
        ChainHub.blockChain.add(secondBlock);
        
        // Tamper with the data
        secondBlock.setData("Tampered data");
        
        assertFalse(ChainHub.isChainValid(), 
                    "A chain with tampered data should be invalid");
    }
}

