package com.travelagencies.services;

import com.travelagencies.dao.ActivityDao;
import com.travelagencies.models.Activity;
import com.travelagencies.models.Destination;

/**
 * Service class responsible for managing activities within the application.
 * This class contains methods to create, retrieve, and update activity information.
 */
public class ActivityService {
    private final ActivityDao activityDao;

    public ActivityService(ActivityDao activityDao) {
        this.activityDao = activityDao;
    }

    public void createActivity(String name, String description, double cost, int capacity, Destination destination) {
        int activityId = activityDao.generateActivityId();
        Activity activity = Activity.builder()
                .activityId(activityId)
                .name(name)
                .description(description)
                .cost(cost)
                .capacity(capacity)
                .destination(destination)
                .build();

        activityDao.save(activity);
    }

    public Activity getActivityDetails(int activityId) {
        return activityDao.getById(activityId).orElseThrow(
                () -> new IllegalArgumentException(String.format("activityId: %s not found.", activityId)));
    }

    public void updateActivityCapacity(int activityId, int newCapacity) {
        Activity activity = activityDao.getById(activityId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("activityId: %s not found.", activityId)));

        activity.setCapacity(newCapacity);
        activityDao.update(activity);
    }

    public void updateActivityCost(int activityId, double newCost) {
        Activity activity = activityDao.getById(activityId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("activityId: %s not found.", activityId)));

        activity.setCost(newCost);
        activityDao.update(activity);
    }
}
