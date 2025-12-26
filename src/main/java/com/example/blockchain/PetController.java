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

}
