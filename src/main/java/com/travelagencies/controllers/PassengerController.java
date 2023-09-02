package com.travelagencies.controllers;

import com.travelagencies.enums.PassengerType;
import com.travelagencies.models.Passenger;
import com.travelagencies.services.PassengerService;

/**
 * Controller class responsible for managing passenger-related operations.
 * This class delegates passenger-related actions to the PassengerService.
 */
public class PassengerController {
    private final PassengerService passengerService;

    /**
     * Constructs a PassengerController with the provided PassengerService.
     *
     * @param passengerService The service responsible for managing passengers.
     */
    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    /**
     * Creates a new passenger with the specified details.
     *
     * @param passengerName The name of the new passenger.
     * @param mobile        The mobile number of the new passenger.
     * @param passengerType The type of the new passenger (e.g., STANDARD, GOLD).
     * @param balance       The initial balance of the new passenger.
     */
    public void createPassenger(String passengerName, String mobile, PassengerType passengerType, double balance) {
        passengerService.createPassenger(passengerName, mobile, passengerType, balance);
    }

    /**
     * Updates the balance of an existing passenger.
     *
     * @param passengerId The unique identifier of the passenger to update.
     * @param newBalance  The new balance to set for the passenger.
     */
    public void updatePassengerBalance(int passengerId, double newBalance) {
        passengerService.updatePassengerBalance(passengerId, newBalance);
    }

    /**
     * Retrieves details of a passenger by their unique identifier.
     *
     * @param passengerId The unique identifier of the passenger to retrieve.
     * @return The Passenger object containing details of the specified passenger.
     */
    public Passenger getPassengerDetails(int passengerId) {
        return passengerService.getPassengerDetails(passengerId);
    }
}
