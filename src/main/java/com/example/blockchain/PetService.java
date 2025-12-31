package com.example.blockchain;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

public class PetService {

    // Registry to store all pets by their ID for quick lookup
    private static final Map<String, Pet> petRegistry = new HashMap<>();

    // Map to track which block hash created which pet (for deriving attributes)
    private static final Map<String, String> petIdToBlockHash = new HashMap<>();

    private static final Gson gson = new Gson();
    private static final String PETS_FILE = "pets.json";
    private static final String PET_BLOCKHASH_FILE = "pet_blockhash.json";
    private static final Gson fileGson = new GsonBuilder().setPrettyPrinting().create();

    // Load pet registry from file
    public static void loadPetRegistry() {
        try {
            File petsFile = new File(PETS_FILE);
            if (petsFile.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(PETS_FILE)));
                if (!content.trim().isEmpty()) {
                    Type mapType = new TypeToken<Map<String, Pet>>(){}.getType();
                    Map<String, Pet> loaded = fileGson.fromJson(content, mapType);
                    if (loaded != null) {
                        petRegistry.putAll(loaded);
                        System.out.println("Pet registry loaded successfully. Pets: " + petRegistry.size());
                    }
                }
            }

            File blockHashFile = new File(PET_BLOCKHASH_FILE);
            if (blockHashFile.exists()) {
                String content = new String(Files.readAllBytes(Paths.get(PET_BLOCKHASH_FILE)));
                if (!content.trim().isEmpty()) {
                    Type mapType = new TypeToken<Map<String, String>>(){}.getType();
                    Map<String, String> loaded = fileGson.fromJson(content, mapType);
                    if (loaded != null) {
                        petIdToBlockHash.putAll(loaded);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading pet registry: " + e.getMessage());
        }
    }

    // Save pet registry to file
    public static void savePetRegistry() {
        try {
            String petsJson = fileGson.toJson(petRegistry);
            Files.write(Paths.get(PETS_FILE), petsJson.getBytes());
            
            String blockHashJson = fileGson.toJson(petIdToBlockHash);
            Files.write(Paths.get(PET_BLOCKHASH_FILE), blockHashJson.getBytes());
            
            System.out.println("Pet registry saved successfully. Pets: " + petRegistry.size());
        } catch (Exception e) {
            System.err.println("Error saving pet registry: " + e.getMessage());
        }
    }

    // Pet types based on hash values
    private static final String[] PET_TYPES = {
            "Dragon", "Cat", "Dog", "Bird", "Fish",
            "Tiger", "Lion", "Wolf", "Eagle", "Shark",
            "Fox", "Bear", "Rabbit", "Turtle", "Snake"
    };

    // Pet colors based on hash values
    private static final String[] PET_COLORS = {
            "Red", "Blue", "Green", "Yellow", "Purple",
            "Orange", "Pink", "Black", "White", "Gold",
            "Silver", "Brown", "Gray", "Cyan", "Magenta"
    };

    // Creates a new pet for an owner and records it on the blockchain
    public static Pet createPet(String ownerAddress, String petName) {
        // Generate unique pet ID
        String petId = StringUtil.applySha256(ownerAddress + petName + System.currentTimeMillis())
                .substring(0, 16);

        // Get previous hash for the new block
        String previousHash = ChainHub.blockChain.isEmpty()
                ? "0"
                : ChainHub.blockChain.get(ChainHub.blockChain.size() - 1).hash;

        // Create transaction data as JSON
        String transactionData = gson.toJson(Map.of(
                "type", "CREATE_PET",
                "petId", petId,
                "ownerAddress", ownerAddress,
                "petName", petName,
                "timestamp", System.currentTimeMillis()
        ));

        // Create and mine the block
        Block newBlock = new Block(transactionData, previousHash);
        newBlock.mineBlock(ChainHub.difficulty);
        ChainHub.blockChain.add(newBlock);
        ChainHub.saveBlockchain(); // Save blockchain after adding block

        // Derive pet attributes from the block hash
        String blockHash = newBlock.hash;
        String petType = derivePetType(blockHash);
        String petColor = derivePetColor(blockHash);
        int rarity = deriveRarity(blockHash);

        // Create the pet
        Pet pet = new Pet(
                petId,
                petName,
                petType,
                petColor,
                rarity,
                ownerAddress,
                System.currentTimeMillis()
        );

        // Store in registry
        petRegistry.put(petId, pet);
        petIdToBlockHash.put(petId, blockHash);

        // Save to disk
        savePetRegistry();

        return pet;
    }

    // Retrieves all pets owned by a specific address

    public static List<Pet> getPetsByOwner(String address) {
        return petRegistry.values().stream()
                .filter(pet -> pet.getOwner().equals(address))
                .collect(Collectors.toList());
    }

    // Retrieves all pets in the system
    public static List<Pet> getAllPets() {
        return new ArrayList<>(petRegistry.values());
    }

    // Trades a pet from one owner to another and records it on the blockchain
    public static void tradePet(String petId, String fromOwner, String toOwner) {
        // Validate pet exists
        Pet pet = petRegistry.get(petId);
        if (pet == null) {
            throw new IllegalArgumentException("Pet with ID " + petId + " does not exist");
        }

        // Validate current ownership
        if (!pet.getOwner().equals(fromOwner)) {
            throw new IllegalArgumentException("Pet is not owned by " + fromOwner);
        }

        // Validate different owners
        if (fromOwner.equals(toOwner)) {
            throw new IllegalArgumentException("Cannot trade pet to the same owner");
        }

        // Get previous hash for the new block
        String previousHash = ChainHub.blockChain.isEmpty()
                ? "0"
                : ChainHub.blockChain.get(ChainHub.blockChain.size() - 1).hash;

        // Create trade transaction data as JSON
        String transactionData = gson.toJson(Map.of(
                "type", "TRADE_PET",
                "petId", petId,
                "fromOwner", fromOwner,
                "toOwner", toOwner,
                "timestamp", System.currentTimeMillis()
        ));

        // Create and mine the block
        Block newBlock = new Block(transactionData, previousHash);
        newBlock.mineBlock(ChainHub.difficulty);
        ChainHub.blockChain.add(newBlock);
        ChainHub.saveBlockchain(); // Save blockchain after adding block

        // Update pet ownership
        // Create new pet with updated owner (Pet is immutable, so we need to replace it)
        Pet updatedPet = new Pet(
                pet.getId(),
                pet.getName(),
                pet.getType(),
                pet.getColor(),
                pet.getRarity(),
                toOwner,
                System.currentTimeMillis()
        );

        petRegistry.put(petId, updatedPet);

        // Save to disk
        savePetRegistry();
    }

    // Derives pet type from block hash
    private static String derivePetType(String hash) {
        // Use first character of hash to determine type
        int index = Integer.parseInt(String.valueOf(hash.charAt(0)), 16) % PET_TYPES.length;
        return PET_TYPES[index];
    }

    // Derives pet color from block hash
    private static String derivePetColor(String hash) {
        // Use second character of hash to determine color
        int index = Integer.parseInt(String.valueOf(hash.charAt(1)), 16) % PET_COLORS.length;
        return PET_COLORS[index];
    }

    // Derives rarity from block hash (1-5 scale, where 5 is most rare)
    private static int deriveRarity(String hash) {
        // Use third character to determine rarity
        // Values closer to 'f' (15) are rarer
        int hexValue = Integer.parseInt(String.valueOf(hash.charAt(2)), 16);
        if (hexValue >= 12) return 5; // Legendary
        if (hexValue >= 9) return 4;  // Epic
        if (hexValue >= 6) return 3;  // Rare
        if (hexValue >= 3) return 2;  // Uncommon
        return 1; // Common
    }
}