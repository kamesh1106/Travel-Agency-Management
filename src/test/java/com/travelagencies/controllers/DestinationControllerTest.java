package com.travelagencies.controllers;

import com.travelagencies.models.Destination;
import com.travelagencies.services.DestinationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class DestinationControllerTest {

    @InjectMocks
    private DestinationController destinationController;

    @Mock
    private DestinationService destinationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateDestination() {
        String name = "destination";

        doNothing().when(destinationService).createDestination(name);

        destinationController.createDestination(name);

        verify(destinationService, times(1)).createDestination(name);
    }

    @Test
    public void testGetDestinationDetails() {
        int destinationId = 123;

        Destination mockDestination = Destination.builder().destinationId(destinationId).name("destination").build();

        when(destinationService.getDestinationDetails(destinationId)).thenReturn(mockDestination);

        Destination result = destinationController.getDestinationDetails(destinationId);

        verify(destinationService, times(1)).getDestinationDetails(destinationId);
        assertEquals(mockDestination, result);
    }

    @Test
    public void testUpdateDestinationName() {
        int destinationId = 123;
        String newName = "new destination";

        doNothing().when(destinationService).updateDestinationName(destinationId, newName);

        destinationController.updateDestinationName(destinationId, newName);

        verify(destinationService, times(1)).updateDestinationName(destinationId, newName);
    }
}
