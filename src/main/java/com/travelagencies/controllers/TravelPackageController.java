package com.travelagencies.controllers;

import com.travelagencies.models.TravelPackage;
import com.travelagencies.services.TravelPackageService;

/**
 * Controller class responsible for managing travel package-related operations.
 * This class delegates travel package-related actions to the TravelPackageService.
 */
public class TravelPackageController {
    private final TravelPackageService travelPackageService;

    /**
     * Constructs a TravelPackageController with the provided TravelPackageService.
     *
     * @param travelPackageService The service responsible for managing travel packages.
     */
    public TravelPackageController(TravelPackageService travelPackageService) {
        this.travelPackageService = travelPackageService;
    }

    /**
     * Creates a new travel package with the specified details.
     *
     * @param name             The name of the new travel package.
     * @param description      The description of the new travel package.
     * @param passengerCapacity The passenger capacity of the new travel package.
     */
    public void createTravelPackage(String name, String description, int passengerCapacity) {
        travelPackageService.createTravelPackage(name, description, passengerCapacity);
    }

    /**
     * Retrieves details of a travel package by its unique identifier.
     *
     * @param packageId The unique identifier of the travel package to retrieve.
     * @return The TravelPackage object containing details of the specified travel package.
     */
    public TravelPackage getTravelPackageDetails(int packageId) {
        return travelPackageService.getTravelPackageDetails(packageId);
    }

    /**
     * Updates the name of an existing travel package.
     *
     * @param packageId The unique identifier of the travel package to update.
     * @param newName   The new name to set for the travel package.
     */
    public void updateTravelPackageName(int packageId, String newName) {
        travelPackageService.updateTravelPackageName(packageId, newName);
    }

    /**
     * Updates the passenger capacity of an existing travel package.
     *
     * @param packageId   The unique identifier of the travel package to update.
     * @param newCapacity The new passenger capacity to set for the travel package.
     */
    public void updateTravelPackagePassengerCapacity(int packageId, int newCapacity) {
        travelPackageService.updateTravelPackagePassengerCapacity(packageId, newCapacity);
    }
}
