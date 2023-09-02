package com.travelagencies.dao;

import com.travelagencies.models.Destination;
import com.travelagencies.models.TravelPackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TravelPackageDaoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private TravelPackageDao travelPackageDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        travelPackageDao = new TravelPackageDao();
    }

    @Test
    public void testSaveTravelPackage() throws SQLException {
        TravelPackage travelPackage = createExpectedTravelPackage(1);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).executeUpdate();

        travelPackageDao.save(travelPackage);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testUpdateTravelPackage() throws SQLException {
        TravelPackage travelPackage = createExpectedTravelPackage(1);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).executeUpdate();

        travelPackageDao.update(travelPackage);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteTravelPackage() throws SQLException {
        TravelPackage travelPackage = createExpectedTravelPackage(1);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).executeUpdate();

        travelPackageDao.delete(travelPackage);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetByIdTravelPackage() throws SQLException {
        int packageId = 1;
        TravelPackage expectedTravelPackage = createExpectedTravelPackage(packageId);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getInt("package_id")).thenReturn(packageId);
        when(mockResultSet.getString("package_name")).thenReturn(expectedTravelPackage.getName());
        when(mockResultSet.getString("package_description")).thenReturn(expectedTravelPackage.getDescription());
        when(mockResultSet.getInt("capacity")).thenReturn(expectedTravelPackage.getCapacity());
        when(mockResultSet.getInt("destination_id")).thenReturn(1).thenReturn(2);
        when(mockResultSet.getString("destination_name")).thenReturn("Destination 1").thenReturn("Destination 2");

        Optional<TravelPackage> result = travelPackageDao.getById(packageId);

        assertTrue(result.isPresent());
        assertEquals(expectedTravelPackage, result.get());
    }

    @Test
    public void testGetByIdFalseCase() throws SQLException {
        int packageIdToRetrieve = 123;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Optional<TravelPackage> travelPackage = travelPackageDao.getById(packageIdToRetrieve);

        assertFalse(travelPackage.isPresent());
    }

    @Test
    public void testGeneratePackageId() throws SQLException {
        int expectedGeneratedPackageId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("package_id")).thenReturn(expectedGeneratedPackageId);

        int generatedPackageId = travelPackageDao.generatePackageId();

        assertEquals(expectedGeneratedPackageId, generatedPackageId);
    }

    private TravelPackage createExpectedTravelPackage(int packageId) {
        TravelPackage travelPackage = TravelPackage.builder()
                .packageId(packageId)
                .name("Test Package")
                .description("Test Description")
                .capacity(10)
                .build();

        List<Destination> destinations = List.of(
                Destination.builder().destinationId(1).name("Destination 1").build(),
                Destination.builder().destinationId(2).name("Destination 2").build()
        );
        travelPackage.setDestinations(destinations);

        return travelPackage;
    }
}
