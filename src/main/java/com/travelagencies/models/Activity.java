package com.travelagencies.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Activity {

    private Integer activityId;

    private String name;

    private String description;

    private Double cost;

    private Integer capacity;

    private Destination destination;
}
