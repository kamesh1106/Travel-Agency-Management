package com.travelagencies.services;

import com.travelagencies.dao.ActivityDao;
import com.travelagencies.models.Activity;
import com.travelagencies.models.Destination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ActivityServiceTest {

    @InjectMocks
    private ActivityService activityService;

    @Mock
    private ActivityDao activityDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateActivity_Success() {
        Destination destination = Destination.builder().build();
        when(activityDao.generateActivityId()).thenReturn(101);

        activityService.createActivity("aName", "description", 50.0, 20, destination);

        verify(activityDao, times(1)).save(any());
    }

    @Test
    public void testGetActivityDetails_Success() {
        int activityId = 101;

        Activity mockActivity = Activity.builder().activityId(activityId).name("aName").build();
        mockActivity.setActivityId(activityId);

        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.of(mockActivity));

        Activity result = activityService.getActivityDetails(activityId);

        assertNotNull(result);
        assertEquals(activityId, result.getActivityId());
        assertEquals("aName", result.getName());
    }

    @Test
    public void testGetActivityDetails_ActivityNotFound() {
        int activityId = 101;
        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> activityService.getActivityDetails(activityId));
    }

    @Test
    public void testUpdateActivityCapacity_Success() {
        int activityId = 101;
        int newCapacity = 30;

        Activity mockActivity = Activity.builder().activityId(activityId).capacity(newCapacity).build();
        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.of(mockActivity));

        activityService.updateActivityCapacity(activityId, newCapacity);

        assertEquals(newCapacity, mockActivity.getCapacity());
    }

    @Test
    public void testUpdateActivityCapacity_ActivityNotFound() {
        int activityId = 101;
        int newCapacity = 30;
        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> activityService.updateActivityCapacity(activityId, newCapacity));
    }

    @Test
    public void testUpdateActivityCost_Success() {
        int activityId = 101;
        double newCost = 60.0;
        Activity mockActivity = Activity.builder().activityId(activityId).cost(newCost).build();
        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.of(mockActivity));

        activityService.updateActivityCost(activityId, newCost);

        assertEquals(newCost, mockActivity.getCost());
    }

    @Test
    public void testUpdateActivityCost_ActivityNotFound() {
        int activityId = 101;
        double newCost = 60.0;
        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> activityService.updateActivityCost(activityId, newCost));
    }
}

