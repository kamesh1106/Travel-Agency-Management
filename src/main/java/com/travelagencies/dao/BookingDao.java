package com.travelagencies.dao;

import com.travelagencies.enums.BookingStatus;
import com.travelagencies.models.Booking;

import java.sql.*;
import java.util.Optional;

/**
 * Provides methods to access and manage booking records in the database.
 * The BookingRepository class allows interaction with the database to perform
 * operations related to booking records, including creation, retrieval, updating,
 * and deletion of booking records.
 */
public class BookingDao implements Repository<Booking> {

    private final String jdbcUrl = "jdbc:mysql://localhost:3306/travel_db";
    private final String jdbcUsername = "username";
    private final String jdbcPassword = "password";

    /**
     * Saves a new booking record to the database.
     *
     * @param booking The booking object to be saved.
     */
    @Override
    public void save(Booking booking) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String insertBookingQuery = "INSERT INTO bookings (booking_id, passenger_id, destination_id, activity_id, " +
                    "booking_status) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(insertBookingQuery)) {
                int i = 1;
                statement.setInt(i++, booking.getBookingId());
                statement.setInt(i++, booking.getPassengerId());
                statement.setInt(i++, booking.getDestinationId());
                statement.setInt(i++, booking.getActivityId());
                statement.setString(i++, booking.getBookingStatus().name());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing booking record in the database.
     *
     * @param booking The updated booking object.
     */
    @Override
    public void update(Booking booking) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String updateBookingQuery = "UPDATE bookings SET passenger_id = ?, destination_id = ?, " +
                    "activity_id = ?, booking_status = ? WHERE booking_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(updateBookingQuery)) {
                int i = 1;
                statement.setInt(i++, booking.getPassengerId());
                statement.setInt(i++, booking.getDestinationId());
                statement.setInt(i++, booking.getActivityId());
                statement.setString(i++, booking.getBookingStatus().name());
                statement.setInt(i++, booking.getBookingId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a booking record from the database.
     *
     * @param booking The booking record to be deleted.
     */
    @Override
    public void delete(Booking booking) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String deleteBookingQuery = "DELETE FROM bookings WHERE booking_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(deleteBookingQuery)) {
                int i = 1;
                statement.setInt(i++, booking.getBookingId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a booking record by its unique identifier from the database.
     *
     * @param id The unique identifier of the booking to retrieve.
     * @return An Optional containing the booking object if found, or empty if not found.
     */
    @Override
    public Optional<Booking> getById(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String getBookingByIdQuery = "SELECT passenger_id, destination_id, activity_id, booking_status " +
                    "FROM bookings WHERE booking_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(getBookingByIdQuery)) {
                int i = 1;
                statement.setInt(i++, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int passengerId = resultSet.getInt("passenger_id");
                        int destinationId = resultSet.getInt("destination_id");
                        int activityId = resultSet.getInt("activity_id");
                        String bookingStatus = resultSet.getString("booking_status");

                        Booking booking = Booking.builder()
                                .bookingId(id)
                                .passengerId(passengerId)
                                .destinationId(destinationId)
                                .activityId(activityId)
                                .bookingStatus(BookingStatus.valueOf(bookingStatus))
                                .build();

                        return Optional.of(booking);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Generates a unique booking ID for a new booking record.
     * This method generates a new unique identifier for booking records. The generated
     * ID can be used when creating new booking objects to ensure that each booking has
     * a distinct identifier.
     *
     * @return A unique bookingId as an integer.
     */
    public int generateBookingId() {
        int generatedBookingId = -1;

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String sql = "INSERT INTO bookings DEFAULT VALUES RETURNING booking_id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    generatedBookingId = resultSet.getInt("booking_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedBookingId;
    }
}
