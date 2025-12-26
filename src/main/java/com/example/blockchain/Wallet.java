package com.example.blockchain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Wallet {

    private String address;
    private List<Pet> pets;

    public Wallet() {
        /*
        / Generate unique address from UUID
        / Hashes it to a 64-char hex string
        */
        this.address = StringUtil.applySha256(UUID.randomUUID().toString())
                .substring(0, 16);
        this.pets = new ArrayList<>();
    }

    // Returns the wallet address
    public String getAddress() {
        return address;
    }

    // Returns the list of pets
    public List<Pet> getPets() {
        return pets;
    }

    // Adds a pet to the wallet’s list
    public void addPet(Pet pet) {
        pets.add(pet);
    }

    // Removes a pet from the wallet’s list
    public void removePet(Pet pet) {
        pets.remove(pet);
    }
}
