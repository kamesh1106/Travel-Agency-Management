package com.travelagencies.models;

import com.travelagencies.enums.PassengerType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Passenger {

    private Integer passengerId;

    private String passengerName;

    private String passengerMobile;

    private PassengerType passengerType;

    private Double balance;

    private List<Booking> bookingList;
}
