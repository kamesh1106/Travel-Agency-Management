package com.travelagencies.services;

import com.travelagencies.dao.ActivityDao;
import com.travelagencies.dao.BookingDao;
import com.travelagencies.dao.PassengerDao;
import com.travelagencies.enums.BookingStatus;
import com.travelagencies.enums.PassengerType;
import com.travelagencies.models.Activity;
import com.travelagencies.models.Booking;
import com.travelagencies.models.Passenger;

/**
 * Service class responsible for managing booking-related operations.
 * This class coordinates booking actions, performs validation, and interacts with
 * the BookingDao, PassengerDao, and ActivityDao to persist and retrieve booking data.
 */
public class BookingService {
    private final BookingDao bookingDao;
    private final PassengerDao passengerDao;
    private final ActivityDao activityDao;

    public BookingService(BookingDao bookingDao, PassengerDao passengerDao, ActivityDao activityDao) {
        this.bookingDao = bookingDao;
        this.passengerDao = passengerDao;
        this.activityDao = activityDao;
    }

    public void createBooking(int passengerId, int activityId, int destinationId) {
        Passenger passenger = passengerDao.getById(passengerId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Passenger %s not found.", passengerId)));

        Activity activity = activityDao.getById(activityId)
                .orElseThrow(() -> new IllegalArgumentException(String.format("activityId %s not found.", activityId)));

        if (activity.getCapacity() <= 0) {
            throw new IllegalArgumentException(String.format("Capacity is full for activityId: %s.", activity));
        }

        double bookingCost = getDiscountedCost(passenger, activity);
        if (passenger.getBalance() < bookingCost) {
            throw new IllegalArgumentException(String.format("Insufficient balance for booking the activity: %s", activityId));
        }

        Booking booking = Booking.builder()
                .bookingId(bookingDao.generateBookingId())
                .passengerId(passengerId)
                .destinationId(destinationId)
                .activityId(activityId)
                .bookingStatus(BookingStatus.PENDING)
                .build();
        bookingDao.save(booking);

        passenger.setBalance(passenger.getBalance() - bookingCost);
        passenger.getBookingList().add(booking);
        passengerDao.update(passenger);

        activity.setCapacity(activity.getCapacity() - 1);
        activityDao.update(activity);
    }

    public void updateBookingStatus(int bookingId, BookingStatus newStatus) {
        Booking booking = bookingDao.getById(bookingId).orElseThrow(
                () -> new IllegalArgumentException(String.format("bookingId: %s does not exists.", bookingId)));

        booking.setBookingStatus(newStatus);
        bookingDao.update(booking);

        if (BookingStatus.CANCELLED == newStatus) {
            Passenger passenger = passengerDao.getById(booking.getPassengerId()).orElseThrow(
                    () -> new IllegalArgumentException(String.format("Passenger: %s not found", booking.getPassengerId())));

            Activity activity = activityDao.getById(booking.getActivityId()).orElseThrow(
                    () -> new IllegalArgumentException(String.format("activityId: %s not found.", booking.getActivityId())));

            double refundAmount = calculateRefundAmount(passenger, activity);

            // Refund the passenger's balance
            passenger.setBalance(passenger.getBalance() + refundAmount);
            passenger.getBookingList().remove(booking);
            passengerDao.update(passenger);

            // Increment activity capacity
            activity.setCapacity(activity.getCapacity() + 1);
            activityDao.update(activity);
        }
    }

    public Booking getBookingDetails(int bookingId) {
        return bookingDao.getById(bookingId).orElseThrow(
                () -> new IllegalArgumentException(String.format("bookingId: %s not found.", bookingId)));
    }

    private Double getDiscountedCost(Passenger passenger, Activity activity) {
        if (PassengerType.STANDARD.equals(passenger.getPassengerType())) {
            return activity.getCost();
        } else if (PassengerType.GOLD.equals(passenger.getPassengerType())) {
            double discount = activity.getCost() * 0.10;
            return activity.getCost() - discount;
        }

        // Booking cost is free for PREMIUM passengers.
        return 0.0;
    }

    private double calculateRefundAmount(Passenger passenger, Activity activity) {
        if (passenger.getPassengerType() == PassengerType.STANDARD) {
            return activity.getCost(); // Full refund for STANDARD passengers
        } else if (passenger.getPassengerType() == PassengerType.GOLD) {
            double discount = activity.getCost() * 0.10; // 10% discount for GOLD passengers
            return activity.getCost() - discount; // 90% of the activity cost as refund
        } else {
            return 0.0; // No refund for PREMIUM passengers
        }
    }
}
