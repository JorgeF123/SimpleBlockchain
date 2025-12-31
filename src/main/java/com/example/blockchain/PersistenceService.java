package com.example.blockchain;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Service to handle persistence operations on application startup and shutdown.
 * Loads blockchain and pet registry on startup, saves them on shutdown.
 */
@Component
public class PersistenceService {

    /**
     * Loads blockchain and pet registry from disk when application starts.
     * This method is called automatically by Spring after dependency injection.
     */
    @PostConstruct
    public void loadData() {
        System.out.println("Loading persisted data...");
        ChainHub.loadBlockchain();
        PetService.loadPetRegistry();
        System.out.println("Data loading complete.");
    }

    /**
     * Saves blockchain and pet registry to disk when application shuts down.
     * This method is called automatically by Spring before the application context is destroyed.
     */
    @PreDestroy
    public void saveData() {
        System.out.println("Saving data before shutdown...");
        ChainHub.saveBlockchain();
        PetService.savePetRegistry();
        System.out.println("Data saving complete.");
    }
}

