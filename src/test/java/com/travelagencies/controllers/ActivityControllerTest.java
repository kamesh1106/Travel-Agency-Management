package com.travelagencies.controllers;

import com.travelagencies.models.Activity;
import com.travelagencies.models.Destination;
import com.travelagencies.services.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ActivityControllerTest {

    @InjectMocks
    private ActivityController activityController;

    @Mock
    private ActivityService activityService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateActivity() {
        String name = "activityName";
        String description = "description";
        double cost = 50.0;
        int capacity = 20;
        Destination destination = Destination.builder().destinationId(1).name("destination").build();

        doNothing().when(activityService).createActivity(name, description, cost, capacity, destination);

        activityController.createActivity(name, description, cost, capacity, destination);

        verify(activityService, times(1))
                .createActivity(name, description, cost, capacity, destination);
    }

    @Test
    public void testGetActivityDetails() {
        int activityId = 123;
        Activity mockActivity = Activity.builder().activityId(activityId).build();

        when(activityService.getActivityDetails(activityId)).thenReturn(mockActivity);

        Activity result = activityController.getActivityDetails(activityId);

        verify(activityService, times(1)).getActivityDetails(activityId);
        verify(activityService, times(0)).createActivity(any(), any(), any(), any(), any());
        verify(activityService, times(0)).updateActivityCapacity(anyInt(), anyInt());
        assertEquals(mockActivity, result);
    }

    @Test
    public void testUpdateActivityCapacity() {
        int activityId = 123;
        int newCapacity = 30;

        doNothing().when(activityService).updateActivityCapacity(activityId, newCapacity);

        activityController.updateActivityCapacity(activityId, newCapacity);

        verify(activityService, times(1)).updateActivityCapacity(activityId, newCapacity);
    }

    @Test
    public void testUpdateActivityCost() {
        int activityId = 123;
        double newCost = 60.0;

        doNothing().when(activityService).updateActivityCost(activityId, newCost);

        activityController.updateActivityCost(activityId, newCost);

        verify(activityService, times(1)).updateActivityCost(activityId, newCost);
    }
}
