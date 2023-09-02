package com.travelagencies.controllers;

import com.travelagencies.models.TravelPackage;
import com.travelagencies.services.TravelPackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TravelPackageControllerTest {

    @InjectMocks
    private TravelPackageController travelPackageController;

    @Mock
    private TravelPackageService travelPackageService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateTravelPackage() {
        String name = "package1";
        String description = "description1";
        int passengerCapacity = 50;

        doNothing().when(travelPackageService).createTravelPackage(name, description, passengerCapacity);

        travelPackageController.createTravelPackage(name, description, passengerCapacity);

        verify(travelPackageService, times(1)).createTravelPackage(name, description, passengerCapacity);
    }

    @Test
    public void testGetTravelPackageDetails() {
        int packageId = 123;

        TravelPackage mockTravelPackage = TravelPackage.builder().packageId(packageId).name("name")
                .description("description").capacity(50).build();

        when(travelPackageService.getTravelPackageDetails(packageId)).thenReturn(mockTravelPackage);

        TravelPackage result = travelPackageController.getTravelPackageDetails(packageId);

        verify(travelPackageService, times(1)).getTravelPackageDetails(packageId);
        assertEquals(mockTravelPackage, result);
    }

    @Test
    public void testUpdateTravelPackageName() {
        int packageId = 123;
        String newName = "newPackageName";

        doNothing().when(travelPackageService).updateTravelPackageName(packageId, newName);

        travelPackageController.updateTravelPackageName(packageId, newName);

        verify(travelPackageService, times(1)).updateTravelPackageName(packageId, newName);
    }

    @Test
    public void testUpdateTravelPackagePassengerCapacity() {
        int packageId = 123;
        int newCapacity = 60;

        doNothing().when(travelPackageService).updateTravelPackagePassengerCapacity(packageId, newCapacity);

        travelPackageController.updateTravelPackagePassengerCapacity(packageId, newCapacity);

        verify(travelPackageService, times(1))
                .updateTravelPackagePassengerCapacity(packageId, newCapacity);
    }
}
