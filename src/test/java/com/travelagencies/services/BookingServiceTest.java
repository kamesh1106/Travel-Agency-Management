package com.travelagencies.services;

import com.travelagencies.dao.ActivityDao;
import com.travelagencies.dao.BookingDao;
import com.travelagencies.dao.PassengerDao;
import com.travelagencies.enums.BookingStatus;
import com.travelagencies.enums.PassengerType;
import com.travelagencies.models.Activity;
import com.travelagencies.models.Booking;
import com.travelagencies.models.Passenger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingDao bookingDao;

    @Mock
    private PassengerDao passengerDao;

    @Mock
    private ActivityDao activityDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateBooking() {
        int passengerId = 1;
        int activityId = 2;
        int destinationId = 3;

        Passenger mockPassenger = Passenger.builder().passengerId(1).passengerName("name").passengerMobile("1234")
                        .passengerType(PassengerType.STANDARD).balance(500.0).build();
        when(passengerDao.getById(passengerId)).thenReturn(java.util.Optional.of(mockPassenger));

        Activity mockActivity = Activity.builder().activityId(2).name("aName")
                .description("aDescription").cost(100.0).capacity(5).build();
        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.of(mockActivity));

        when(bookingDao.generateBookingId()).thenReturn(101);
        doNothing().when(bookingDao).save(any(Booking.class));

        assertDoesNotThrow(() -> bookingService.createBooking(passengerId, activityId, destinationId));

        verify(passengerDao, times(1)).getById(passengerId);
        verify(activityDao, times(1)).getById(activityId);
        verify(bookingDao, times(1)).save(any(Booking.class));
        verify(passengerDao, times(1)).update(any(Passenger.class));
        verify(activityDao, times(1)).update(any(Activity.class));
    }

    @Test
    public void testCreateBooking_InsufficientBalance() {
        int passengerId = 1;
        int activityId = 2;
        int destinationId = 3;

        Passenger mockPassenger = Passenger.builder().passengerId(1).passengerName("name").passengerMobile("1234")
                .passengerType(PassengerType.GOLD).balance(50.0).build();
        when(passengerDao.getById(passengerId)).thenReturn(java.util.Optional.of(mockPassenger));

        Activity mockActivity = Activity.builder().activityId(2).name("aName")
                .description("aDescription").cost(100.0).capacity(5).build();
        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.of(mockActivity));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(passengerId, activityId, destinationId));

        verify(passengerDao, times(1)).getById(passengerId);
        verify(activityDao, times(1)).getById(activityId);
        verify(bookingDao, never()).save(any(Booking.class));
    }

    @Test
    public void testCreateBooking_FullCapacity() {
        int passengerId = 1;
        int activityId = 2;
        int destinationId = 3;

        Passenger mockPassenger = Passenger.builder().passengerId(1).passengerName("name").passengerMobile("1234")
                .passengerType(PassengerType.GOLD).balance(400.0).build();
        when(passengerDao.getById(passengerId)).thenReturn(java.util.Optional.of(mockPassenger));

        Activity mockActivity = Activity.builder().activityId(2).name("aName")
                .description("aDescription").cost(100.0).capacity(0).build();
        when(activityDao.getById(activityId)).thenReturn(java.util.Optional.of(mockActivity));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(passengerId, activityId, destinationId));

        verify(passengerDao, times(1)).getById(passengerId);
        verify(activityDao, times(1)).getById(activityId);
        verify(bookingDao, never()).save(any(Booking.class));
    }

    @Test
    public void testUpdateBookingStatus_CancelledBooking() {
        int bookingId = 101;
        BookingStatus newStatus = BookingStatus.CANCELLED;

        Booking mockBooking = Booking.builder().bookingId(101).passengerId(1).destinationId(2)
                .activityId(3).bookingStatus(BookingStatus.CANCELLED).build();
        when(bookingDao.getById(bookingId)).thenReturn(java.util.Optional.of(mockBooking));
        doNothing().when(bookingDao).update(any(Booking.class));

        Passenger mockPassenger = Passenger.builder().passengerId(1).passengerName("name").passengerMobile("1234")
                .passengerType(PassengerType.GOLD).balance(400.0).build();
        when(passengerDao.getById(mockBooking.getPassengerId())).thenReturn(java.util.Optional.of(mockPassenger));

        Activity mockActivity = Activity.builder().activityId(2).name("aName")
                .description("aDescription").cost(100.0).capacity(4).build();
        when(activityDao.getById(mockBooking.getActivityId())).thenReturn(java.util.Optional.of(mockActivity));

        assertDoesNotThrow(() -> bookingService.updateBookingStatus(bookingId, newStatus));

        verify(bookingDao, times(1)).update(any(Booking.class));
        verify(passengerDao, times(1)).getById(mockBooking.getPassengerId());
        verify(activityDao, times(1)).getById(mockBooking.getActivityId());
    }

    @Test
    public void testUpdateBookingStatus_NonCancelledBooking() {
        int bookingId = 101;
        BookingStatus newStatus = BookingStatus.CONFIRMED;

        Booking mockBooking = Booking.builder().bookingId(101).passengerId(1).destinationId(2)
                .activityId(3).bookingStatus(BookingStatus.CONFIRMED).build();
        when(bookingDao.getById(bookingId)).thenReturn(java.util.Optional.of(mockBooking));
        doNothing().when(bookingDao).update(any(Booking.class));

        assertDoesNotThrow(() -> bookingService.updateBookingStatus(bookingId, newStatus));

        verify(bookingDao, times(1)).update(any(Booking.class));
        verify(passengerDao, never()).getById(anyInt());
        verify(activityDao, never()).getById(anyInt());
    }

    @Test
    public void testUpdateBookingStatus_BookingNotFound() {
        int bookingId = 101;
        BookingStatus newStatus = BookingStatus.CANCELLED;

        when(bookingDao.getById(bookingId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBookingStatus(bookingId, newStatus));

        verify(bookingDao, times(1)).getById(bookingId);
        verify(passengerDao, never()).getById(anyInt());
        verify(activityDao, never()).getById(anyInt());
    }

    @Test
    public void testGetBookingDetails_BookingFound() {
        int bookingId = 101;
        Booking mockBooking = Booking.builder().bookingId(101).passengerId(1).destinationId(2)
                .activityId(3).bookingStatus(BookingStatus.CONFIRMED).build();
        when(bookingDao.getById(bookingId)).thenReturn(java.util.Optional.of(mockBooking));

        Booking result = bookingService.getBookingDetails(bookingId);

        verify(bookingDao, times(1)).getById(bookingId);
        assertEquals(mockBooking, result);
    }

    @Test
    public void testGetBookingDetails_BookingNotFound() {
        int bookingId = 101;
        when(bookingDao.getById(bookingId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> bookingService.getBookingDetails(bookingId));

        verify(bookingDao, times(1)).getById(bookingId);
    }
}
