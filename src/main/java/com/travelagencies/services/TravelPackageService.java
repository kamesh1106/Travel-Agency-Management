package com.travelagencies.services;

import com.travelagencies.dao.TravelPackageDao;
import com.travelagencies.models.TravelPackage;

/**
 * Service class for managing travel package-related operations.
 */
public class TravelPackageService {
    private final TravelPackageDao travelPackageDao;

    public TravelPackageService(TravelPackageDao travelPackageDao) {
        this.travelPackageDao = travelPackageDao;
    }

    public void createTravelPackage(String name, String description, int passengerCapacity) {
        int packageId = travelPackageDao.generatePackageId();
        TravelPackage newPackage = TravelPackage.builder()
                .packageId(packageId)
                .name(name)
                .description(description)
                .capacity(passengerCapacity)
                .build();

        travelPackageDao.save(newPackage);
    }

    public TravelPackage getTravelPackageDetails(int packageId) {
        return travelPackageDao.getById(packageId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Travel packageId: %s not found.", packageId)));
    }

    public void updateTravelPackageName(int packageId, String newName) {
        TravelPackage travelPackage = travelPackageDao.getById(packageId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Travel packageId: %s not found.", packageId)));

        travelPackage.setName(newName);
        travelPackageDao.update(travelPackage);
    }

    public void updateTravelPackagePassengerCapacity(int packageId, int newCapacity) {
        TravelPackage travelPackage = travelPackageDao.getById(packageId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Travel packageId: %s not found.", packageId)));

        travelPackage.setCapacity(newCapacity);
        travelPackageDao.update(travelPackage);
    }
}
