package com.example.blockchain;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class ChainHub {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();
    public static int difficulty = 5;
    private static final String BLOCKCHAIN_FILE = "blockchain.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Load blockchain from file
    public static void loadBlockchain() {
        try {
            File file = new File(BLOCKCHAIN_FILE);
            if (!file.exists()) {
                System.out.println("Blockchain file not found. Starting with empty blockchain.");
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(BLOCKCHAIN_FILE)));
            if (content.trim().isEmpty()) {
                System.out.println("Blockchain file is empty. Starting with empty blockchain.");
                return;
            }

            Type listType = new TypeToken<ArrayList<Block>>(){}.getType();
            ArrayList<Block> loaded = gson.fromJson(content, listType);
            
            if (loaded != null) {
                blockChain = loaded;
                System.out.println("Blockchain loaded successfully. Blocks: " + blockChain.size());
            } else {
                System.out.println("Failed to load blockchain. Starting with empty blockchain.");
            }
        } catch (Exception e) {
            System.err.println("Error loading blockchain: " + e.getMessage());
            System.out.println("Starting with empty blockchain.");
        }
    }

    // Save blockchain to file
    public static void saveBlockchain() {
        try {
            String json = gson.toJson(blockChain);
            Files.write(Paths.get(BLOCKCHAIN_FILE), json.getBytes());
            System.out.println("Blockchain saved successfully. Blocks: " + blockChain.size());
        } catch (Exception e) {
            System.err.println("Error saving blockchain: " + e.getMessage());
        }
    }

    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        for(int i=1; i < blockChain.size(); i ++){
            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i-1);

            //compare registered hash and calculated hash:
            if(!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("current Hashes not equal");
                return false;
            }

            //compare previous hash and registered previous hash
            if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            //check if hash is solved
            if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }
}
