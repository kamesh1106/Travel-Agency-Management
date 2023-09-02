package com.travelagencies.dao;

import com.travelagencies.models.Destination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class DestinationDaoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private DestinationDao destinationDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        destinationDao = new DestinationDao();
    }

    @Test
    public void testSave() throws SQLException {
        Destination destination = Destination.builder().destinationId(1).name("Destination").build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).setInt(anyInt(), anyInt());
        doNothing().when(mockPreparedStatement).setString(anyInt(), anyString());
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        destinationDao.save(destination);

        verify(mockPreparedStatement).setInt(1, destination.getDestinationId());
        verify(mockPreparedStatement).setString(2, destination.getName());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testUpdate() throws SQLException {
        Destination destination = Destination.builder().destinationId(1).name("Updated destination").build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).setString(anyInt(), anyString());
        doNothing().when(mockPreparedStatement).setInt(anyInt(), anyInt());
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        destinationDao.update(destination);

        verify(mockPreparedStatement).setString(1, destination.getName());
        verify(mockPreparedStatement).setInt(2, destination.getDestinationId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDelete() throws SQLException {
        Destination destination = Destination.builder().destinationId(1).name("Updated destination").build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).setInt(anyInt(), anyInt());
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        destinationDao.delete(destination);

        verify(mockPreparedStatement).setInt(1, destination.getDestinationId());
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetById() throws SQLException {
        int destinationId = 1;
        String destinationName = "Test Destination";
        Destination expectedDestination = Destination.builder().destinationId(1).name("Updated destination").build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).setInt(anyInt(), anyInt());
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("name")).thenReturn(destinationName);

        Optional<Destination> result = destinationDao.getById(destinationId);

        assertTrue(result.isPresent());
        assertEquals(expectedDestination, result.get());
    }

    @Test
    public void testGetByIdNotFound() throws SQLException {
        int destinationId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).setInt(anyInt(), anyInt());
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Optional<Destination> result = destinationDao.getById(destinationId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGenerateDestinationId() throws SQLException {
        int expectedGeneratedId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        doNothing().when(mockPreparedStatement).executeQuery();
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("destination_id")).thenReturn(expectedGeneratedId);

        int generatedId = destinationDao.generateDestinationId();

        assertEquals(expectedGeneratedId, generatedId);
    }
}
