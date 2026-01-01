package com.example.blockchain;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PetController {

    // A new wallet is created, Its address is generated, the address is returned as JSON
    @PostMapping("/wallet/create")
    public ResponseEntity<Map<String, String>> createWallet() {
        Wallet wallet = new Wallet();
        Map<String, String> response = new HashMap<>();
        response.put("address", wallet.getAddress());
        return ResponseEntity.ok(response);
    }

    // Takes an owner address + pet name, creates a new pet for that owner, returns the created pet
    @PostMapping("/pet/create")
    public ResponseEntity<?> createPet(@RequestBody Map<String, String> request) {
        String ownerAddress = request.get("ownerAddress");
        String petName = request.get("petName");

        // Input validation
        if (ownerAddress == null || ownerAddress.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "ownerAddress is required");
            return ResponseEntity.badRequest().body(error);
        }

        if (petName == null || petName.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "petName is required");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            Pet pet = PetService.createPet(ownerAddress, petName);
            return ResponseEntity.ok(pet);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Searches the database, finds all pets with that owner address, returns a list of Pet objects
    @GetMapping("/pets/owner/{address}")
    public ResponseEntity<List<Pet>> getPetsByOwner(@PathVariable String address) {
        List<Pet> pets = PetService.getPetsByOwner(address);
        return ResponseEntity.ok(pets);
    }

    // gets all the pets in the system
    @GetMapping("/pets/all")
    public ResponseEntity<List<Pet>> getAllPets() {
        List<Pet> pets = PetService.getAllPets();
        return ResponseEntity.ok(pets);
    }

    // Gets a single pet by its ID
    @GetMapping("/pet/{petId}")
    public ResponseEntity<Pet> getPetById(@PathVariable String petId) {
        Pet pet = PetService.getPetById(petId);
        if (pet == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pet);
    }

    // get the complete blockchain stored in ChainHub
    @GetMapping("/blockchain")
    public ResponseEntity<List<Block>> getBlockchain() {
        return ResponseEntity.ok(ChainHub.blockChain);
    }

    // Returns whether the blockchain is valid as a JSON object with "valid": true/false
    @GetMapping("/blockchain/validate")
    public ResponseEntity<Map<String, Boolean>> validateBlockchain() {
        boolean isValid = ChainHub.isChainValid();
        Map<String, Boolean> response = new HashMap<>();
        response.put("valid", isValid);
        return ResponseEntity.ok(response);
    }

    // Trades a pet from one owner to another and returns success or error status
    @PostMapping("/pet/trade")
    public ResponseEntity<Map<String, String>> tradePet(@RequestBody Map<String, String> request) {
        String petId = request.get("petId");
        String fromOwner = request.get("fromOwner");
        String toOwner = request.get("toOwner");

        // Input validation
        if (petId == null || petId.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "petId is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (fromOwner == null || fromOwner.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "fromOwner is required");
            return ResponseEntity.badRequest().body(response);
        }

        if (toOwner == null || toOwner.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "toOwner is required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            PetService.tradePet(petId, fromOwner, toOwner);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Pet traded successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Gets the complete transaction history for a specific pet
    @GetMapping("/pet/{petId}/history")
    public ResponseEntity<List<Transaction>> getPetTransactionHistory(@PathVariable String petId) {
        List<Transaction> transactions = PetService.getPetTransactionHistory(petId);
        return ResponseEntity.ok(transactions);
    }

    // Gets all transactions involving a specific owner address
    @GetMapping("/owner/{address}/transactions")
    public ResponseEntity<List<Transaction>> getOwnerTransactionHistory(@PathVariable String address) {
        List<Transaction> transactions = PetService.getOwnerTransactionHistory(address);
        return ResponseEntity.ok(transactions);
    }

    // Gets system statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(PetService.getStats());
    }
}
