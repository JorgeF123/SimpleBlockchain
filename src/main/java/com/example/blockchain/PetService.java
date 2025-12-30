package com.example.blockchain;

import java.util.*;
import java.util.stream.Collectors;
import com.google.gson.Gson;

public class PetService {

    // Registry to store all pets by their ID for quick lookup
    private static final Map<String, Pet> petRegistry = new HashMap<>();

    // Map to track which block hash created which pet (for deriving attributes)
    private static final Map<String, String> petIdToBlockHash = new HashMap<>();

    private static final Gson gson = new Gson();

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