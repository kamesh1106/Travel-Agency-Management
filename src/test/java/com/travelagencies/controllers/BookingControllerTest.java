package com.travelagencies.controllers;

import com.travelagencies.enums.BookingStatus;
import com.travelagencies.models.Booking;
import com.travelagencies.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class BookingControllerTest {

    @InjectMocks
    private BookingController bookingController;

    @Mock
    private BookingService bookingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBooking() {
        bookingController.createBooking(1, 2, 3);

        verify(bookingService, times(1)).createBooking(1, 2, 3);
    }

    @Test
    public void testUpdateBookingStatus() {
        bookingController.updateBookingStatus(1, BookingStatus.CONFIRMED);

        verify(bookingService, times(1)).updateBookingStatus(1, BookingStatus.CONFIRMED);
    }

    @Test
    public void testGetBookingById() {
        int bookingId = 123;

        Booking mockBooking = Booking.builder().bookingId(123).passengerId(1).destinationId(2)
                .activityId(3).bookingStatus(BookingStatus.CONFIRMED).build();

        when(bookingService.getBookingDetails(bookingId)).thenReturn(mockBooking);

        Booking result = bookingController.getBookingDetails(bookingId);

        verify(bookingService, times(1)).getBookingDetails(bookingId);
        assertEquals(mockBooking, result);
    }
}
