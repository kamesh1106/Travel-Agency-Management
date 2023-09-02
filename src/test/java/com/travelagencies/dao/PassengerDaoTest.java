package com.travelagencies.dao;

import com.travelagencies.enums.BookingStatus;
import com.travelagencies.enums.PassengerType;
import com.travelagencies.models.Booking;
import com.travelagencies.models.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PassengerDaoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockUpdatePassengerStatement;

    @Mock
    private PreparedStatement mockDeleteBookingsStatement;

    @Mock
    private PreparedStatement mockInsertBookingsStatement;

    @Mock
    private PreparedStatement mockDeletePassengerStatement;

    @Mock
    private PreparedStatement mockDeletePassengerBookingsStatement;

    @Mock
    private PreparedStatement mockGetPassengerStatement;

    @Mock
    private ResultSet mockResultSet;

    private PassengerDao passengerDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        passengerDao = new PassengerDao();
    }

    @Test
    public void testSave() throws SQLException {
        Passenger passenger = Passenger.builder()
                .passengerId(101).passengerName("pName").passengerMobile("9999988888")
                .passengerType(PassengerType.GOLD).balance(2500.0).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockUpdatePassengerStatement);

        passengerDao.save(passenger);

        verify(mockConnection).prepareStatement(anyString());
        verify(mockUpdatePassengerStatement).setInt(1, passenger.getPassengerId());
        verify(mockUpdatePassengerStatement).setString(2, passenger.getPassengerName());
        verify(mockUpdatePassengerStatement).setString(3, passenger.getPassengerMobile());
        verify(mockUpdatePassengerStatement).setString(4, passenger.getPassengerType().name());
        verify(mockUpdatePassengerStatement).setDouble(5, passenger.getBalance());

        verify(mockUpdatePassengerStatement).executeUpdate();

        for (Booking booking : passenger.getBookingList()) {
            verify(mockUpdatePassengerStatement).setInt(1, booking.getBookingId());
            verify(mockUpdatePassengerStatement).setInt(2, passenger.getPassengerId());
            verify(mockUpdatePassengerStatement).executeUpdate();
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        Passenger passenger = Passenger.builder()
                .passengerId(101).passengerName("pName").passengerMobile("9999988888")
                .passengerType(PassengerType.GOLD).balance(2500.0).build();
        passenger.setBookingList(List.of(Booking.builder().passengerId(1).bookingId(1).destinationId(2)
                .activityId(3).bookingStatus(BookingStatus.CONFIRMED).build()));

        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockUpdatePassengerStatement)
                .thenReturn(mockDeleteBookingsStatement)
                .thenReturn(mockInsertBookingsStatement);

        passengerDao.update(passenger);

        verify(mockUpdatePassengerStatement).setString(1, passenger.getPassengerName());
        verify(mockUpdatePassengerStatement).setString(2, passenger.getPassengerMobile());
        verify(mockUpdatePassengerStatement).setString(3, passenger.getPassengerType().name());
        verify(mockUpdatePassengerStatement).setDouble(4, passenger.getBalance());
        verify(mockUpdatePassengerStatement).setInt(5, passenger.getPassengerId());
        verify(mockUpdatePassengerStatement).executeUpdate();

        verify(mockDeleteBookingsStatement).setInt(1, passenger.getPassengerId());
        verify(mockDeleteBookingsStatement).executeUpdate();

        for (Booking booking : passenger.getBookingList()) {
            verify(mockInsertBookingsStatement).setInt(1, booking.getBookingId());
            verify(mockInsertBookingsStatement).setInt(2, passenger.getPassengerId());
            verify(mockInsertBookingsStatement).executeUpdate();
        }
    }

    @Test
    public void testDelete() throws SQLException {
        Passenger passenger = Passenger.builder()
                .passengerId(101).passengerName("pName").passengerMobile("9999988888")
                .passengerType(PassengerType.GOLD).balance(2500.0).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockDeletePassengerStatement)
                .thenReturn(mockDeletePassengerBookingsStatement);

        passengerDao.delete(passenger);

        verify(mockDeletePassengerBookingsStatement).setInt(1, passenger.getPassengerId());
        verify(mockDeletePassengerBookingsStatement).executeUpdate();

        verify(mockDeletePassengerStatement).setInt(1, passenger.getPassengerId());
        verify(mockDeletePassengerStatement).executeUpdate();
    }

    @Test
    public void testGetByIdHappyPath() throws SQLException {
        int passengerId = 101;
        Passenger expectedPassenger = Passenger.builder()
                .passengerId(101).passengerName("pName").passengerMobile("9999988888")
                .passengerType(PassengerType.GOLD).balance(2500.0).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockGetPassengerStatement);
        when(mockGetPassengerStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("passenger_id")).thenReturn(expectedPassenger.getPassengerId());
        when(mockResultSet.getString("name")).thenReturn(expectedPassenger.getPassengerName());
        when(mockResultSet.getString("mobile")).thenReturn(expectedPassenger.getPassengerMobile());
        when(mockResultSet.getString("passenger_type")).thenReturn(expectedPassenger.getPassengerType().name());
        when(mockResultSet.getDouble("balance")).thenReturn(expectedPassenger.getBalance());

        Optional<Passenger> result = passengerDao.getById(passengerId);

        assertTrue(result.isPresent());
        assertEquals(expectedPassenger, result.get());
    }

    @Test
    public void testGetByIdFalseCase() throws SQLException {
        int passengerId = 2;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockGetPassengerStatement);
        when(mockGetPassengerStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Optional<Passenger> result = passengerDao.getById(passengerId);

        assertFalse(result.isPresent());
    }

    @Test
    public void testGeneratePassengerId() throws SQLException {
        int expectedGeneratedPassengerId = 101;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockGetPassengerStatement);
        when(mockGetPassengerStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("passenger_id")).thenReturn(expectedGeneratedPassengerId);

        int generatedPassengerId = passengerDao.generatePassengerId();

        assertEquals(expectedGeneratedPassengerId, generatedPassengerId);
    }
}
