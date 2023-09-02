package com.travelagencies.services;

import com.travelagencies.dao.TravelPackageDao;
import com.travelagencies.models.TravelPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class TravelPackageServiceTest {

    @InjectMocks
    private TravelPackageService travelPackageService;

    @Mock
    private TravelPackageDao travelPackageDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateTravelPackage_Success() {
        String name = "Travel Package";
        String description = "Description";
        int passengerCapacity = 50;
        TravelPackage mockPackage = TravelPackage.builder().packageId(1).name(name)
                .description(description).capacity(passengerCapacity).build();

        when(travelPackageDao.generatePackageId()).thenReturn(1);

        travelPackageService.createTravelPackage(name, description, passengerCapacity);

        verify(travelPackageDao).save(mockPackage);
    }

    @Test
    public void testGetTravelPackageDetails_Success() {
        int packageId = 1;
        String name = "Package A";
        String description = "Description";
        int passengerCapacity = 50;

        TravelPackage mockPackage = TravelPackage.builder().packageId(1).name(name)
                .description(description).capacity(passengerCapacity).build();

        when(travelPackageDao.getById(packageId)).thenReturn(java.util.Optional.of(mockPackage));

        TravelPackage result = travelPackageService.getTravelPackageDetails(packageId);

        assertEquals(packageId, result.getPackageId());
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(passengerCapacity, result.getCapacity());
    }

    @Test
    public void testGetTravelPackageDetails_PackageNotFound() {
        int packageId = 1;
        when(travelPackageDao.getById(packageId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> travelPackageService.getTravelPackageDetails(packageId));
    }

    @Test
    public void testUpdateTravelPackageName_Success() {
        int packageId = 1;
        String newName = "Updated Travel Package Name";
        TravelPackage mockPackage = TravelPackage.builder().packageId(1).name(newName)
                .description("description").capacity(20).build();
        when(travelPackageDao.getById(packageId)).thenReturn(java.util.Optional.of(mockPackage));

        travelPackageService.updateTravelPackageName(packageId, newName);

        assertEquals(newName, mockPackage.getName());
    }

    @Test
    public void testUpdateTravelPackageName_PackageNotFound() {
        int packageId = 1;
        String newName = "Updated Travel Package Name";
        when(travelPackageDao.getById(packageId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> travelPackageService.updateTravelPackageName(packageId, newName));
    }

    @Test
    public void testUpdateTravelPackagePassengerCapacity_Success() {
        int packageId = 1;
        int newCapacity = 60;
        TravelPackage mockPackage = TravelPackage.builder().packageId(1).name("name")
                .description("description").capacity(newCapacity).build();
        when(travelPackageDao.getById(packageId)).thenReturn(java.util.Optional.of(mockPackage));

        travelPackageService.updateTravelPackagePassengerCapacity(packageId, newCapacity);

        assertEquals(newCapacity, mockPackage.getCapacity());
    }

    @Test
    public void testUpdateTravelPackagePassengerCapacity_PackageNotFound() {
        int packageId = 1;
        int newCapacity = 60;
        when(travelPackageDao.getById(packageId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> travelPackageService.updateTravelPackagePassengerCapacity(packageId, newCapacity));
    }
}

