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
    public ResponseEntity<Pet> createPet(@RequestBody Map<String, String> request) {
        String ownerAddress = request.get("ownerAddress");
        String petName = request.get("petName");

        Pet pet = PetService.createPet(ownerAddress, petName);
        return ResponseEntity.ok(pet);
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
}
