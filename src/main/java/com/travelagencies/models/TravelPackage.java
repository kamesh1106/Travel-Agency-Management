package com.travelagencies.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TravelPackage {

    private Integer packageId;

    private String name;

    private String description;

    private Integer capacity;

    private List<Destination> destinations;
}
