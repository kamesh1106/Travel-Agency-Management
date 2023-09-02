package com.travelagencies.controllers;

import com.travelagencies.enums.BookingStatus;
import com.travelagencies.models.Booking;
import com.travelagencies.services.BookingService;

/**
 * Controller class responsible for handling booking-related operations.
 * This class delegates booking-related actions to the BookingService.
 */
public class BookingController {
    private final BookingService bookingService;

    /**
     * Constructs a BookingController with the provided BookingService.
     *
     * @param bookingService The service responsible for managing bookings.
     */
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Creates a new booking for a passenger to participate in an activity at a destination.
     *
     * @param passengerId   The unique identifier of the passenger making the booking.
     * @param activityId    The unique identifier of the activity being booked.
     * @param destinationId The unique identifier of the destination where the activity takes place.
     */
    public void createBooking(int passengerId, int activityId, int destinationId) {
        bookingService.createBooking(passengerId, activityId, destinationId);
    }

    /**
     * Updates the status of a booking to the specified new status.
     *
     * @param bookingId The unique identifier of the booking to update.
     * @param newStatus The new status to set for the booking.
     */
    public void updateBookingStatus(int bookingId, BookingStatus newStatus) {
        bookingService.updateBookingStatus(bookingId, newStatus);
    }

    /**
     * Retrieves details of a booking by its unique identifier.
     *
     * @param bookingId The unique identifier of the booking to retrieve.
     * @return The Booking object containing details of the specified booking.
     */
    public Booking getBookingDetails(int bookingId) {
        return bookingService.getBookingDetails(bookingId);
    }
}
