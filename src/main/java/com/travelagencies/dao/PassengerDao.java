package com.travelagencies.dao;

import com.travelagencies.enums.BookingStatus;
import com.travelagencies.enums.PassengerType;
import com.travelagencies.models.Booking;
import com.travelagencies.models.Passenger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides methods to access and manage passenger records in the database.
 * The PassengerRepository class allows interaction with the database to perform
 * operations related to passenger records, including creation, retrieval, updating,
 * and deletion of passenger records.
 */
public class PassengerDao implements Repository<Passenger> {

    private final String jdbcUrl = "jdbc:mysql://localhost:3306/travel_db";
    private final String jdbcUsername = "username";
    private final String jdbcPassword = "password";

    /**
     * Saves a new passenger record to the database.
     *
     * @param passenger The passenger object to be saved.
     */
    @Override
    public void save(Passenger passenger) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String insertPassengerQuery = "INSERT INTO passengers (passenger_id, name, mobile, passenger_type, balance) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertPassengerQuery)) {
                int i = 1;
                statement.setInt(i++, passenger.getPassengerId());
                statement.setString(i++, passenger.getPassengerName());
                statement.setString(i++, passenger.getPassengerMobile());
                statement.setString(i++, passenger.getPassengerType().name());
                statement.setDouble(i++, passenger.getBalance());
                statement.executeUpdate();
            }

            String insertPassengerBookingsQuery = "INSERT INTO passenger_bookings (booking_id, passenger_id) VALUES (?, ?)";
            for (Booking booking: passenger.getBookingList()) {
                try (PreparedStatement statement = connection.prepareStatement(insertPassengerBookingsQuery)) {
                    int i = 1;
                    statement.setInt(i++, booking.getBookingId());
                    statement.setInt(i++, passenger.getPassengerId());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing passenger record in the database.
     *
     * @param passenger The updated passenger object.
     */
    @Override
    public void update(Passenger passenger) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String updatePassengerQuery = "UPDATE passengers SET name = ?, mobile = ?, passenger_type = ?, balance = ? WHERE passenger_id = ?";
            String deleteBookingsQuery = "DELETE FROM passenger_bookings where passenger_id = ?";
            String insertBookingsQuery = "INSERT INTO passenger_bookings (booking_id, passenger_id) VALUES (?, ?)";

            try (PreparedStatement updatePassengerStatement = connection.prepareStatement(updatePassengerQuery);
                 PreparedStatement deleteBookingsStatement = connection.prepareStatement(deleteBookingsQuery);
                 PreparedStatement insertBookingsStatement = connection.prepareStatement(insertBookingsQuery)) {

                int i = 1;
                updatePassengerStatement.setString(i++, passenger.getPassengerName());
                updatePassengerStatement.setString(i++, passenger.getPassengerMobile());
                updatePassengerStatement.setString(i++, passenger.getPassengerType().name());
                updatePassengerStatement.setDouble(i++, passenger.getBalance());
                updatePassengerStatement.setDouble(i++, passenger.getPassengerId());
                updatePassengerStatement.executeUpdate();

                i = 1;
                deleteBookingsStatement.setInt(i++, passenger.getPassengerId());
                deleteBookingsStatement.executeUpdate();

                i = 1;
                for (Booking booking: passenger.getBookingList()) {
                    insertBookingsStatement.setInt(i++, booking.getBookingId());
                    insertBookingsStatement.setInt(i++, passenger.getPassengerId());
                    insertBookingsStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a passenger record from the database.
     *
     * @param passenger The passenger object to be deleted.
     */
    @Override
    public void delete(Passenger passenger) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String deletePassengerQuery = "DELETE FROM passengers where passenger_id = ?";
            String deletePassengerBookingsQuery = "DELETE FROM passenger_bookings where passenger_id = ?";

            try (PreparedStatement deletePassengerStatement = connection.prepareStatement(deletePassengerQuery);
                 PreparedStatement deleteBookingsStatement = connection.prepareStatement(deletePassengerBookingsQuery)) {

                deleteBookingsStatement.setInt(1, passenger.getPassengerId());
                deleteBookingsStatement.executeUpdate();

                deletePassengerStatement.setInt(1, passenger.getPassengerId());
                deletePassengerStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a passenger record by its unique identifier from the database.
     *
     * @param id The unique identifier of the passenger to retrieve.
     * @return An Optional containing the passenger object if found, or empty if not found.
     */
    @Override
    public Optional<Passenger> getById(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String getPassengerQuery = "SELECT passenger_id, name, mobile, passenger_type, balance " +
                    "FROM passengers where passenger_id = ?";

            try (PreparedStatement getPassengerStatement = connection.prepareStatement(getPassengerQuery)) {

                int i = 1;
                getPassengerStatement.setInt(i++, id);

                try (ResultSet passengerResultSet = getPassengerStatement.executeQuery()) {

                    if (passengerResultSet.next()) {
                        int passengerId = passengerResultSet.getInt("passenger_id");
                        String name = passengerResultSet.getString("name");
                        String mobile = passengerResultSet.getString("mobile");
                        String passenger_type = passengerResultSet.getString("passenger_type");
                        Double balance = passengerResultSet.getDouble("balance");

                        Passenger passenger = Passenger.builder()
                                .passengerId(passengerId)
                                .passengerName(name)
                                .passengerMobile(mobile)
                                .passengerType(PassengerType.valueOf(passenger_type))
                                .balance(balance)
                                .build();

                        passenger.setBookingList(getAllPassengerBookings(passengerId));

                        return Optional.of(passenger);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Generates a unique passenger ID for a new passenger record.
     * This method generates a new unique identifier for passenger records. The generated
     * ID can be used when creating new passenger objects to ensure that each passenger
     * has a distinct identifier.
     *
     * @return A unique passenger ID as an integer.
     */
    public int generatePassengerId() {
        int generatedPassengerId = -1;

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String sql = "INSERT INTO passengers DEFAULT VALUES RETURNING passenger_id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    generatedPassengerId = resultSet.getInt("passenger_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedPassengerId;
    }

    private List<Booking> getAllPassengerBookings(int passengerId) {
        List<Booking> bookings = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String getPassengerBookingsQuery = "SELECT pb.booking_id, b.destination_id, b.activity_id, " +
                    "b.booking_status FROM passenger_bookings pb INNER JOIN booking b on pb.booking_id = b.booking_id " +
                    "where pb.passenger_id = ?";

            try (PreparedStatement getPassengerBookingsStatement = connection.prepareStatement(getPassengerBookingsQuery)) {
                int i = 1;
                getPassengerBookingsStatement.setInt(i++, passengerId);

                try (ResultSet passengerBookingsResultSet = getPassengerBookingsStatement.executeQuery()) {
                    while (passengerBookingsResultSet.next()) {
                        int bookingId = passengerBookingsResultSet.getInt("booking_id");
                        int destinationId = passengerBookingsResultSet.getInt("destination_id");
                        int activityId = passengerBookingsResultSet.getInt("activity_id");
                        String bookingStatus = passengerBookingsResultSet.getString("booking_status");

                        Booking booking = Booking.builder()
                                .bookingId(bookingId)
                                .passengerId(passengerId)
                                .destinationId(destinationId)
                                .activityId(activityId)
                                .bookingStatus(BookingStatus.valueOf(bookingStatus))
                                .build();

                        bookings.add(booking);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookings;
    }
}
