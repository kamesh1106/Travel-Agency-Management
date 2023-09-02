package com.travelagencies.services;

import com.travelagencies.dao.DestinationDao;
import com.travelagencies.models.Destination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DestinationServiceTest {

    @InjectMocks
    private DestinationService destinationService;

    @Mock
    private DestinationDao destinationDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateDestination_Success() {
        Destination mockDestination = Destination.builder().destinationId(1).name("destination").build();
        when(destinationDao.generateDestinationId()).thenReturn(1);

        destinationService.createDestination("destination");

        verify(destinationDao).save(mockDestination);
    }

    @Test
    public void testGetDestinationDetails_Success() {
        int destinationId = 1;
        String name = "Destination A";
        Destination mockDestination = Destination.builder().destinationId(destinationId).name(name).build();

        when(destinationDao.getById(destinationId)).thenReturn(java.util.Optional.of(mockDestination));

        Destination result = destinationService.getDestinationDetails(destinationId);

        assertEquals(destinationId, result.getDestinationId());
        assertEquals(name, result.getName());
    }

    @Test
    public void testUpdateDestinationName_Success() {
        int destinationId = 1;
        String newName = "Updated Destination Name";
        Destination mockDestination = Destination.builder().destinationId(destinationId).name(newName).build();

        when(destinationDao.getById(destinationId)).thenReturn(java.util.Optional.of(mockDestination));

        destinationService.updateDestinationName(destinationId, newName);

        assertEquals(newName, mockDestination.getName());
    }

    @Test
    public void testGetDestinationDetails_DestinationNotFound() {
        int destinationId = 1;
        when(destinationDao.getById(destinationId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> destinationService.getDestinationDetails(destinationId));
    }

    @Test
    public void testUpdateDestinationName_DestinationNotFound() {
        int destinationId = 1;
        String newName = "Updated Destination Name";
        when(destinationDao.getById(destinationId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> destinationService.updateDestinationName(destinationId, newName));
    }
}

