package com.travelagencies.services;

import com.travelagencies.dao.PassengerDao;
import com.travelagencies.enums.PassengerType;
import com.travelagencies.models.Passenger;

/**
 * Service class for managing passenger-related operations.
 */
public class PassengerService {
    private final PassengerDao passengerDao;

    public PassengerService(PassengerDao passengerDao) {
        this.passengerDao = passengerDao;
    }

    public void createPassenger(String passengerName, String mobile, PassengerType passengerType, Double balance) {
        int passengerId = passengerDao.generatePassengerId();

        Passenger passenger = Passenger.builder()
                .passengerId(passengerId)
                .passengerName(passengerName)
                .passengerMobile(mobile)
                .passengerType(passengerType)
                .balance(balance)
                .build();
        passengerDao.save(passenger);
    }

    public void updatePassengerBalance(int passengerId, double newBalance) {
        Passenger passenger = passengerDao.getById(passengerId).orElseThrow(
                () -> new IllegalArgumentException(String.format("Passenger: %s not found", passengerId)));

        passenger.setBalance(newBalance);
        passengerDao.update(passenger);
    }

    public Passenger getPassengerDetails(int passengerId) {
        return passengerDao.getById(passengerId).orElseThrow(
                () -> new IllegalArgumentException(String.format("passengerId: %s not found.", passengerId)));
    }
}
