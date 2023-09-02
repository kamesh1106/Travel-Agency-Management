package com.travelagencies.dao;

import com.travelagencies.models.Destination;

import java.sql.*;
import java.util.Optional;

/**
 * Provides methods to access and manage destination records in the database.
 *
 * The DestinationRepository class allows interaction with the database to perform
 * operations related to destination records, including creation, retrieval, updating,
 * and deletion of destination records.
 */
public class DestinationDao implements Repository<Destination> {

    private final String jdbcUrl = "jdbc:mysql://localhost:3306/travel_db";
    private final String jdbcUsername = "username";
    private final String jdbcPassword = "password";

    /**
     * Saves a new destination record to the database.
     *
     * @param destination The destination object to be saved.
     */
    @Override
    public void save(Destination destination) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String insertDestinationQuery = "INSERT INTO destinations (destination_id, name) VALUES (?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertDestinationQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, destination.getDestinationId());
                preparedStatement.setString(i++, destination.getName());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing destination record in the database.
     *
     * @param destination The updated destination object.
     */
    @Override
    public void update(Destination destination) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String updateDestinationQuery = "UPDATE destinations SET name = ? WHERE destination_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateDestinationQuery)) {
                int i = 1;
                preparedStatement.setString(i++, destination.getName());
                preparedStatement.setInt(i++, destination.getDestinationId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a destination record from the database.
     *
     * @param destination The destination object to be deleted.
     */
    @Override
    public void delete(Destination destination) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String deleteDestinationQuery = "DELETE FROM destinations WHERE destination_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteDestinationQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, destination.getDestinationId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a destination record by its unique identifier from the database.
     *
     * @param id The unique identifier of the destination to retrieve.
     * @return An Optional containing the destination object if found, or empty if not found.
     */
    @Override
    public Optional<Destination> getById(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String selectDestinationByIdQuery = "SELECT name FROM destinations WHERE destination_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectDestinationByIdQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        Destination destination = Destination.builder()
                                .destinationId(id)
                                .name(name)
                                .build();

                        return Optional.of(destination);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Generates a unique destination ID for a new destination record.
     * This method generates a new unique identifier for destination records. The generated
     * ID can be used when creating new destination objects to ensure that each destination
     * has a distinct identifier.
     *
     * @return A unique destination ID as an integer.
     */
    public int generateDestinationId() {
        int generatedDestinationId = -1;

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String sql = "INSERT INTO destinations DEFAULT VALUES RETURNING destination_id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    generatedDestinationId = resultSet.getInt("destination_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedDestinationId;
    }
}
