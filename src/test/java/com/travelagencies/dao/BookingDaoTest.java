package com.travelagencies.dao;

import com.travelagencies.enums.BookingStatus;
import com.travelagencies.models.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookingDaoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private BookingDao bookingDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingDao = new BookingDao();
    }

    @Test
    public void testSave() throws SQLException {
        Booking booking = Booking.builder()
                .bookingId(101).passengerId(201).destinationId(301)
                .activityId(401).bookingStatus(BookingStatus.PENDING).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        bookingDao.save(booking);

        verify(mockConnection).prepareStatement(anyString());
        verify(mockStatement).setInt(1, booking.getBookingId());
        verify(mockStatement).setInt(2, booking.getPassengerId());
        verify(mockStatement).setInt(3, booking.getDestinationId());
        verify(mockStatement).setInt(4, booking.getActivityId());
        verify(mockStatement).setString(5, booking.getBookingStatus().name());
        verify(mockStatement).executeUpdate();
    }

    @Test
    public void testUpdate() throws SQLException {
        Booking booking = Booking.builder()
                .bookingId(101).passengerId(201).destinationId(301)
                .activityId(401).bookingStatus(BookingStatus.CONFIRMED).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1); // Indicate that one row was updated

        bookingDao.update(booking);

        verify(mockConnection).prepareStatement(anyString());
        verify(mockStatement).setInt(1, booking.getPassengerId());
        verify(mockStatement).setInt(2, booking.getDestinationId());
        verify(mockStatement).setInt(3, booking.getActivityId());
        verify(mockStatement).setString(4, booking.getBookingStatus().name());
        verify(mockStatement).setInt(5, booking.getBookingId());
        verify(mockStatement).executeUpdate();
    }

    @Test
    public void testDelete() throws SQLException {
        Booking booking = Booking.builder()
                .bookingId(101).passengerId(201).destinationId(301)
                .activityId(401).bookingStatus(BookingStatus.CONFIRMED).build();

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);

        bookingDao.delete(booking);

        verify(mockConnection).prepareStatement(anyString());
        verify(mockStatement).setInt(1, booking.getBookingId());
        verify(mockStatement).executeUpdate();
    }

    @Test
    public void testGetById() throws SQLException {
        int bookingId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        when(mockResultSet.getInt("passenger_id")).thenReturn(123);
        when(mockResultSet.getInt("destination_id")).thenReturn(456);
        when(mockResultSet.getInt("activity_id")).thenReturn(789);
        when(mockResultSet.getString("booking_status")).thenReturn("CONFIRMED");

        Optional<Booking> optionalBooking = bookingDao.getById(bookingId);

        verify(mockConnection).prepareStatement(anyString());
        verify(mockStatement).setInt(1, bookingId);

        assertTrue(optionalBooking.isPresent());
        Booking booking = optionalBooking.get();
        assertEquals(bookingId, booking.getBookingId());
        assertEquals(123, booking.getPassengerId());
        assertEquals(456, booking.getDestinationId());
        assertEquals(789, booking.getActivityId());
        assertEquals(BookingStatus.CONFIRMED, booking.getBookingStatus());
    }

    @Test
    public void testGetByIdWhenNotFound() throws SQLException {
        int bookingId = 1;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        Optional<Booking> optionalBooking = bookingDao.getById(bookingId);

        verify(mockConnection).prepareStatement(anyString());
        verify(mockStatement).setInt(1, bookingId);

        assertFalse(optionalBooking.isPresent());
    }
}
