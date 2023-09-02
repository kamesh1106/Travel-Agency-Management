package com.travelagencies.controllers;

import com.travelagencies.models.Activity;
import com.travelagencies.models.Destination;
import com.travelagencies.services.ActivityService;

/**
 * Controller class for managing activities within the application.
 * This class acts as an interface between the web layer and the service layer
 * for activities-related operations.
 */
public class ActivityController {
    private final ActivityService activityService;

    /**
     * Constructs an ActivityController with the provided ActivityService.
     *
     * @param activityService The service responsible for handling activity-related operations.
     */
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /**
     * Creates a new activity with the specified details and associates it with a destination.
     *
     * @param name        The name of the activity.
     * @param description The description of the activity.
     * @param cost        The cost of participating in the activity.
     * @param capacity    The capacity or maximum number of participants for the activity.
     * @param destination The destination to which the activity belongs.
     */
    public void createActivity(String name, String description, double cost, int capacity, Destination destination) {
        activityService.createActivity(name, description, cost, capacity, destination);
    }

    /**
     * Retrieves details of an activity by its unique identifier.
     *
     * @param activityId The unique identifier of the activity to retrieve.
     * @return The Activity object containing details of the specified activity.
     */
    public Activity getActivityDetails(int activityId) {
        return activityService.getActivityDetails(activityId);
    }

    /**
     * Updates the maximum capacity of an activity.
     *
     * @param activityId  The unique identifier of the activity to update.
     * @param newCapacity The new maximum capacity of the activity.
     */
    public void updateActivityCapacity(int activityId, int newCapacity) {
        activityService.updateActivityCapacity(activityId, newCapacity);
    }

    /**
     * Updates the cost of participating in an activity.
     *
     * @param activityId The unique identifier of the activity to update.
     * @param newCost    The new cost of participating in the activity.
     */
    public void updateActivityCost(int activityId, double newCost) {
        activityService.updateActivityCost(activityId, newCost);
    }
}
