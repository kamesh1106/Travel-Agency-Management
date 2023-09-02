package com.travelagencies.services;

import com.travelagencies.dao.PassengerDao;
import com.travelagencies.enums.PassengerType;
import com.travelagencies.models.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PassengerServiceTest {

    @InjectMocks
    private PassengerService passengerService;

    @Mock
    private PassengerDao passengerDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreatePassenger_Success() {
        String passengerName = "pName";
        String mobile = "123456";
        PassengerType passengerType = PassengerType.STANDARD;
        Double balance = 100.0;

        int generatedPassengerId = 101;
        when(passengerDao.generatePassengerId()).thenReturn(generatedPassengerId);

        passengerService.createPassenger(passengerName, mobile, passengerType, balance);

        verify(passengerDao, times(1)).save(argThat(passenger ->
                passenger.getPassengerName().equals(passengerName) &&
                passenger.getPassengerMobile().equals(mobile) &&
                passenger.getPassengerType() == passengerType &&
                passenger.getBalance().equals(balance) &&
                passenger.getPassengerId() == generatedPassengerId));
    }

    @Test
    public void testUpdatePassengerBalance_Success() {
        int passengerId = 101;
        double newBalance = 150.0;

        Passenger mockPassenger = Passenger.builder().passengerId(passengerId).balance(100.0).build();
        mockPassenger.setPassengerId(passengerId);

        when(passengerDao.getById(passengerId)).thenReturn(java.util.Optional.of(mockPassenger));

        passengerService.updatePassengerBalance(passengerId, newBalance);

        assertEquals(newBalance, mockPassenger.getBalance());
        verify(passengerDao, times(1)).update(mockPassenger);
    }

    @Test
    public void testUpdatePassengerBalance_PassengerNotFound() {
        int passengerId = 101;
        double newBalance = 150.0;

        when(passengerDao.getById(passengerId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> passengerService.updatePassengerBalance(passengerId, newBalance));
    }

    @Test
    public void testGetPassengerDetails_Success() {
        int passengerId = 101;

        Passenger mockPassenger = Passenger.builder().passengerId(passengerId).passengerName("pName").balance(100.0).build();
        mockPassenger.setPassengerId(passengerId);

        when(passengerDao.getById(passengerId)).thenReturn(java.util.Optional.of(mockPassenger));

        Passenger result = passengerService.getPassengerDetails(passengerId);

        assertNotNull(result);
        assertEquals(passengerId, result.getPassengerId());
        assertEquals("pName", result.getPassengerName());
    }

    @Test
    public void testGetPassengerDetails_PassengerNotFound() {
        int passengerId = 101;

        when(passengerDao.getById(passengerId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> passengerService.getPassengerDetails(passengerId));
    }
}

