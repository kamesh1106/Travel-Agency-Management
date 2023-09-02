package com.travelagencies.controllers;

import com.travelagencies.models.Destination;
import com.travelagencies.services.DestinationService;

/**
 * Controller class responsible for managing destination-related operations.
 * This class delegates destination-related actions to the DestinationService.
 */
public class DestinationController {
    private final DestinationService destinationService;

    /**
     * Constructs a DestinationController with the provided DestinationService.
     *
     * @param destinationService The service responsible for managing destinations.
     */
    public DestinationController(DestinationService destinationService) {
        this.destinationService = destinationService;
    }

    /**
     * Creates a new destination with the specified name.
     *
     * @param name The name of the new destination.
     */
    public void createDestination(String name) {
        destinationService.createDestination(name);
    }

    /**
     * Retrieves details of a destination by its unique identifier.
     *
     * @param destinationId The unique identifier of the destination to retrieve.
     * @return The Destination object containing details of the specified destination.
     */
    public Destination getDestinationDetails(int destinationId) {
        return destinationService.getDestinationDetails(destinationId);
    }

    /**
     * Updates the name of a destination with the specified identifier.
     *
     * @param destinationId The unique identifier of the destination to update.
     * @param newName       The new name to set for the destination.
     */
    public void updateDestinationName(int destinationId, String newName) {
        destinationService.updateDestinationName(destinationId, newName);
    }
}
