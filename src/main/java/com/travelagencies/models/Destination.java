package com.travelagencies.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Destination {

    private Integer destinationId;

    private String name;

    private List<Activity> activities;
}
