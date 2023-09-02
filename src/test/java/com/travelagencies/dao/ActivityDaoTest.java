package com.travelagencies.dao;

import com.travelagencies.models.Activity;
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

public class ActivityDaoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private ActivityDao activityDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activityDao = new ActivityDao();
    }

    @Test
    public void testSaveActivity() throws SQLException {
        Activity activity = Activity.builder()
                .activityId(1).name("activityName").description("description").cost(50.0).capacity(10).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        activityDao.save(activity);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testUpdateActivity() throws SQLException {
        Activity activity = Activity.builder()
                .activityId(1).name("activityName").description("description").cost(50.0).capacity(10).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        activityDao.update(activity);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testDeleteActivity() throws SQLException {
        Activity activity = Activity.builder()
                .activityId(1).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        activityDao.delete(activity);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    public void testGetActivityById() throws SQLException {
        int activityId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("destination_id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test Activity");
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getDouble("cost")).thenReturn(50.0);
        when(mockResultSet.getInt("capacity")).thenReturn(100);

        Optional<Activity> optionalActivity = activityDao.getById(activityId);

        assertTrue(optionalActivity.isPresent());

        Activity activity = optionalActivity.get();
        assertEquals(activityId, activity.getActivityId());
        assertEquals("Test Activity", activity.getName());
        assertEquals("Test Description", activity.getDescription());
        assertEquals(50.0, activity.getCost(), 0.01);
        assertEquals(100, activity.getCapacity());
        assertEquals(1, activity.getDestination().getDestinationId());

        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).getInt("destination_id");
        verify(mockResultSet, times(1)).getString("name");
        verify(mockResultSet, times(1)).getString("description");
        verify(mockResultSet, times(1)).getDouble("cost");
        verify(mockResultSet, times(1)).getInt("capacity");
    }

    @Test
    public void testGetActivityByIdNotFound() throws SQLException {
        int activityId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Optional<Activity> activityOptional = activityDao.getById(activityId);

        assertTrue(activityOptional.isEmpty());

        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(1)).next();
    }

    @Test
    public void testGetActivitiesByDestinationId() throws SQLException {
        int destinationId = 101;

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("activity_id")).thenReturn(1, 2);
        when(mockResultSet.getString("name")).thenReturn("Activity 1", "Activity 2");
        when(mockResultSet.getString("description")).thenReturn("Description 1", "Description 2");
        when(mockResultSet.getDouble("cost")).thenReturn(10.0, 20.0);
        when(mockResultSet.getInt("capacity")).thenReturn(5, 10);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        List<Activity> activities = activityDao.getActivitiesByDestinationId(destinationId);

        assertFalse(activities.isEmpty());
        assertEquals(2, activities.size());

        Activity activity1 = activities.get(0);
        assertEquals(1, activity1.getActivityId());
        assertEquals("Activity 1", activity1.getName());
        assertEquals("Description 1", activity1.getDescription());
        assertEquals(10.0, activity1.getCost());
        assertEquals(5, activity1.getCapacity());

        Activity activity2 = activities.get(1);
        assertEquals(2, activity2.getActivityId());
        assertEquals("Activity 2", activity2.getName());
        assertEquals("Description 2", activity2.getDescription());
        assertEquals(20.0, activity2.getCost());
        assertEquals(10, activity2.getCapacity());
    }

    @Test
    public void testGenerateActivityId() throws SQLException {
        int expectedGeneratedActivityId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("activity_id")).thenReturn(expectedGeneratedActivityId);

        int generatedActivityId = activityDao.generateActivityId();

        assertEquals(expectedGeneratedActivityId, generatedActivityId);

        verify(mockPreparedStatement, times(1)).executeQuery();
        verify(mockResultSet, times(1)).next();
        verify(mockResultSet, times(1)).getInt("activity_id");
    }
}
