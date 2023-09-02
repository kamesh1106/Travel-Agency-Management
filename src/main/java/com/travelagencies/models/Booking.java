package com.travelagencies.models;

import com.travelagencies.enums.BookingStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Booking {

    private Integer bookingId;

    private Integer passengerId;

    private Integer destinationId;

    private Integer activityId;

    private BookingStatus bookingStatus;
}
