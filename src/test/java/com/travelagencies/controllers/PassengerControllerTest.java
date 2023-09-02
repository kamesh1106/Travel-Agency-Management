package com.travelagencies.controllers;

import com.travelagencies.enums.PassengerType;
import com.travelagencies.models.Passenger;
import com.travelagencies.services.PassengerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PassengerControllerTest {

    @InjectMocks
    private PassengerController passengerController;

    @Mock
    private PassengerService passengerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePassenger() {
        String passengerName = "pName";
        String mobile = "1234567890";
        PassengerType passengerType = PassengerType.STANDARD;
        double balance = 100.0;

        doNothing().when(passengerService).createPassenger(passengerName, mobile, passengerType, balance);

        passengerController.createPassenger(passengerName, mobile, passengerType, balance);

        verify(passengerService, times(1))
                .createPassenger(passengerName, mobile, passengerType, balance);
    }

    @Test
    public void testUpdatePassengerBalance() {
        int passengerId = 123;
        double newBalance = 150.0;

        doNothing().when(passengerService).updatePassengerBalance(passengerId, newBalance);

        passengerController.updatePassengerBalance(passengerId, newBalance);

        verify(passengerService, times(1)).updatePassengerBalance(passengerId, newBalance);
    }

    @Test
    public void testGetPassengerDetails() {
        int passengerId = 123;
        Passenger mockPassenger = Passenger.builder().passengerId(123).passengerName("pName").passengerMobile("12345")
                .passengerType(PassengerType.GOLD).balance(1000.0).build();

        when(passengerService.getPassengerDetails(passengerId)).thenReturn(mockPassenger);

        Passenger result = passengerController.getPassengerDetails(passengerId);

        verify(passengerService, times(1)).getPassengerDetails(passengerId);

        assertEquals(mockPassenger, result);
    }
}
