package com.travelagencies.dao;

import com.travelagencies.models.Activity;
import com.travelagencies.models.Destination;
import com.travelagencies.models.TravelPackage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Provides methods to access and manage travel package records in the database.
 * The TravelPackageRepository class allows interaction with the database to perform
 * operations related to travel package records, including creation, retrieval, updating,
 * and deletion of travel package records.
 */
public class TravelPackageDao implements Repository<TravelPackage> {

    private final String jdbcUrl = "jdbc:mysql://localhost:3306/travel_db";
    private final String jdbcUsername = "username";
    private final String jdbcPassword = "password";

    /**
     * Saves a new travel package record to the database.
     *
     * @param travelPackage The travel package object to be saved.
     */
    @Override
    public void save(TravelPackage travelPackage) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String insertPackageQuery = "INSERT INTO travel_packages (package_id, name, description, " +
                    "capacity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertPackageQuery)) {
                int i = 1;
                statement.setInt(i++, travelPackage.getPackageId());
                statement.setString(i++, travelPackage.getName());
                statement.setString(i++, travelPackage.getDescription());
                statement.setInt(i++, travelPackage.getCapacity());
                statement.executeUpdate();

                // Save associated destinations to package_destinations table
                saveAssociatedDestinations(travelPackage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing travel package record in the database.
     *
     * @param travelPackage The updated travel package object.
     */
    @Override
    public void update(TravelPackage travelPackage) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String updateTravelPackageQuery = "UPDATE travel_packages SET name = ?, description = ?, " +
                    "capacity = ? WHERE package_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(updateTravelPackageQuery)) {
                int i = 1;
                statement.setString(i++, travelPackage.getName());
                statement.setString(i++, travelPackage.getDescription());
                statement.setInt(i++, travelPackage.getCapacity());
                statement.setInt(i++, travelPackage.getPackageId());
                statement.executeUpdate();

                // Update associated destinations in package_destinations table
                updateAssociatedDestinations(travelPackage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a travel package record from the database.
     *
     * @param travelPackage The travelPackage to be deleted.
     */
    @Override
    public void delete(TravelPackage travelPackage) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String deleteTravelPackageQuery = "DELETE FROM travel_packages WHERE package_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteTravelPackageQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, travelPackage.getPackageId());
                preparedStatement.executeUpdate();

                // Delete associated destinations from package_destinations table
                deleteAssociatedDestinations(travelPackage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a travel package record by its unique identifier from the database.
     *
     * @param id The unique identifier of the travel package to retrieve.
     * @return An Optional containing the travel package object if found, or empty if not found.
     */
    @Override
    public Optional<TravelPackage> getById(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String selectTravelPackageQuery = "SELECT tp.name AS package_name, tp.description AS package_description, " +
                    "tp.capacity, pd.destination_id, d.name AS destination_name, " +
                    "a.activity_id, a.name AS activity_name, a.description AS activity_description, " +
                    "a.cost AS activity_cost, a.capacity AS activity_capacity " +
                    "FROM travel_packages tp " +
                    "LEFT JOIN package_destinations pd ON tp.package_id = pd.package_id " +
                    "LEFT JOIN destinations d ON pd.destination_id = d.destination_id " +
                    "LEFT JOIN activities a ON d.destination_id = a.destination_id " +
                    "WHERE tp.package_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectTravelPackageQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    TravelPackage travelPackage = null;
                    while (resultSet.next()) {
                        if (travelPackage == null) {
                            String packageName = resultSet.getString("package_name");
                            String packageDescription = resultSet.getString("package_description");
                            int passengerCapacity = resultSet.getInt("capacity");

                            travelPackage = TravelPackage.builder()
                                    .packageId(id)
                                    .name(packageName)
                                    .description(packageDescription)
                                    .capacity(passengerCapacity)
                                    .build();
                        }

                        int destinationId = resultSet.getInt("destination_id");
                        String destinationName = resultSet.getString("destination_name");

                        if (destinationId != 0) {
                            if (travelPackage.getDestinations() == null) {
                                travelPackage.setDestinations(new ArrayList<>());
                            }

                            // Check if the destination is already added
                            boolean destinationExists = travelPackage.getDestinations()
                                    .stream()
                                    .anyMatch(destination -> destination.getDestinationId() == destinationId);

                            if (!destinationExists) {
                                Destination destination = Destination.builder()
                                        .destinationId(destinationId)
                                        .name(destinationName)
                                        .build();

                                travelPackage.getDestinations().add(destination);
                            }

                            // Check if there is an associated activity
                            int activityId = resultSet.getInt("activity_id");
                            if (activityId != 0) {
                                String activityName = resultSet.getString("activity_name");
                                String activityDescription = resultSet.getString("activity_description");
                                double activityCost = resultSet.getDouble("activity_cost");
                                int activityCapacity = resultSet.getInt("activity_capacity");

                                Activity activity = Activity.builder()
                                        .activityId(activityId)
                                        .name(activityName)
                                        .description(activityDescription)
                                        .cost(activityCost)
                                        .capacity(activityCapacity)
                                        .build();

                                // Find the destination and add the activity to it
                                travelPackage.getDestinations()
                                        .stream()
                                        .filter(destination -> destination.getDestinationId() == destinationId)
                                        .findFirst()
                                        .ifPresent(destination -> destination.getActivities().add(activity));
                            }
                        }
                    }

                    if (travelPackage != null) {
                        return Optional.of(travelPackage);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }


    /**
     * Generates a unique package ID for a new travel package record.
     * This method generates a new unique identifier for travel package records. The generated
     * ID can be used when creating new travel package objects to ensure that each package
     * has a distinct identifier.
     *
     * @return A unique package ID as an integer.
     */
    public int generatePackageId() {
        int generatedPackageId = -1;

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String sql = "INSERT INTO travel_packages DEFAULT VALUES RETURNING package_id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    generatedPackageId = resultSet.getInt("package_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedPackageId;
    }

    /**
     * Saves the associated destinations of a travel package to the database.
     *
     * This method is responsible for saving the destinations associated with a travel package
     * to the database. It iterates through the list of destinations in the travel package object
     * and creates records in the "package_destinations" table to establish the associations
     * between the travel package and its destinations.
     *
     * @param travelPackage The travel package containing the associated destinations to be saved.
     */
    private void saveAssociatedDestinations(TravelPackage travelPackage) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String insertPackageDestinationsQuery = "INSERT INTO package_destinations (package_id, destination_id) " +
                    "VALUES (?, ?)";

            for (Destination destination : travelPackage.getDestinations()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertPackageDestinationsQuery)) {
                    int i = 1;
                    preparedStatement.setInt(i++, travelPackage.getPackageId());
                    preparedStatement.setInt(i++, destination.getDestinationId());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the associations between a travel package and its destinations in the database.
     *
     * This method updates the associations between a travel package and its destinations
     * in the database by modifying records in the "package_destinations" table.
     * It ensures that the destinations associated with the travel package accurately reflect
     * the current state of the travel package object.
     *
     * @param travelPackage The travel package whose associations with destinations need to be updated.
     */
    private void updateAssociatedDestinations(TravelPackage travelPackage) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String deletePackageDestinationsQuery = "DELETE FROM package_destinations WHERE package_id = ?";
            String insertPackageDestinationsQuery = "INSERT INTO package_destinations (package_id, destination_id) VALUES (?, ?)";

            // Delete existing associations
            try (PreparedStatement preparedStatement = connection.prepareStatement(deletePackageDestinationsQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, travelPackage.getPackageId());
                preparedStatement.executeUpdate();
            }

            // Insert new associations
            for (Destination destination : travelPackage.getDestinations()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertPackageDestinationsQuery)) {
                    int i = 1;
                    preparedStatement.setInt(i++, travelPackage.getPackageId());
                    preparedStatement.setInt(i++, destination.getDestinationId());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Deletes associations between a travel package and its destinations from the database.
     *
     * This method deletes associations between a travel package and its destinations
     * from the database by removing records from the "package_destinations" table.
     * It ensures that the destinations are no longer associated with the specified travel package.
     *
     * @param travelPackage The travel package from which associations with destinations need to be deleted.
     */
    private void deleteAssociatedDestinations(TravelPackage travelPackage) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String deletePackageDestinationsQuery = "DELETE FROM package_destinations WHERE package_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deletePackageDestinationsQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, travelPackage.getPackageId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
