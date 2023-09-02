package com.travelagencies.services;

import com.travelagencies.dao.DestinationDao;
import com.travelagencies.models.Destination;

/**
 * Service class for managing destination-related operations.
 */
public class DestinationService {
    private final DestinationDao destinationDao;

    public DestinationService(DestinationDao destinationDao) {
        this.destinationDao = destinationDao;
    }

    public void createDestination(String name) {
        int destinationId = destinationDao.generateDestinationId();
        Destination destination = Destination.builder()
                .destinationId(destinationId)
                .name(name)
                .build();

        destinationDao.save(destination);
    }

    public Destination getDestinationDetails(int destinationId) {
        return destinationDao.getById(destinationId).orElseThrow(
                () -> new IllegalArgumentException(String.format("destinationId: %s not found", destinationId)));
    }

    public void updateDestinationName(int destinationId, String newName) {
        Destination destination = destinationDao.getById(destinationId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("destinationId: %s not found", destinationId)));

        destination.setName(newName);
        destinationDao.update(destination);
    }
}
