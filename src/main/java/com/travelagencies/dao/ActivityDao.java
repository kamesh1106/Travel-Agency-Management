package com.travelagencies.dao;

import com.travelagencies.models.Activity;
import com.travelagencies.models.Destination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The ActivityRepository class provides methods for managing activities.
 * It interacts with a database, to perform CRUD operations on activity records.
 */
public class ActivityDao implements Repository<Activity> {

    private final String jdbcUrl = "jdbc:mysql://localhost:3306/travel_db";
    private final String jdbcUsername = "username";
    private final String jdbcPassword = "password";

    /**
     * Creates a new activity record in the database.
     *
     * @param activity The activity object to be created.
     */
    @Override
    public void save(Activity activity) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String insertActivityQuery = "INSERT INTO activities (activity_id, destination_id, name, description, " +
                    "cost, capacity) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(insertActivityQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, activity.getActivityId());
                preparedStatement.setInt(i++, activity.getDestination().getDestinationId());
                preparedStatement.setString(i++, activity.getName());
                preparedStatement.setString(i++, activity.getDescription());
                preparedStatement.setDouble(i++, activity.getCost());
                preparedStatement.setInt(i++, activity.getCapacity());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an existing activity record in the database.
     *
     * @param activity The updated activity object.
     */
    @Override
    public void update(Activity activity) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String updateActivityQuery = "UPDATE activities SET destination_id = ?, name = ?, description = ?, " +
                    "cost = ?, capacity = ? WHERE activity_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(updateActivityQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, activity.getDestination().getDestinationId());
                preparedStatement.setString(i++, activity.getName());
                preparedStatement.setString(i++, activity.getDescription());
                preparedStatement.setDouble(i++, activity.getCost());
                preparedStatement.setInt(i++, activity.getCapacity());
                preparedStatement.setInt(i++, activity.getActivityId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes an activity record from the database.
     *
     * @param activity The activity to be deleted.
     */
    @Override
    public void delete(Activity activity) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String deleteActivityQuery = "DELETE FROM activities WHERE activity_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(deleteActivityQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, activity.getActivityId());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves an activity record by its unique identifier (activityId) from the database.
     *
     * @param id The unique identifier of the activity to retrieve.
     * @return The activity object if found, or Optional.empty() if not found.
     */
    @Override
    public Optional<Activity> getById(int id) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String selectActivityByIdQuery = "SELECT destination_id, name, description, cost, capacity FROM activities WHERE activity_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectActivityByIdQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int destinationId = resultSet.getInt("destination_id");
                        String name = resultSet.getString("name");
                        String description = resultSet.getString("description");
                        double cost = resultSet.getDouble("cost");
                        int capacity = resultSet.getInt("capacity");

                        Activity activity = Activity.builder()
                                .activityId(id)
                                .name(name)
                                .description(description)
                                .cost(cost)
                                .capacity(capacity)
                                .build();

                        activity.setDestination(getDestinationById(destinationId));

                        return Optional.of(activity);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    /**
     * Retrieves a list of activities associated with a specific destination from the database.
     *
     * @param destinationId The unique identifier of the destination.
     * @return A list of activity objects associated with the destination.
     */
    public List<Activity> getActivitiesByDestinationId(int destinationId) {
        List<Activity> activities = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String selectActivitiesByDestinationQuery = "SELECT activity_id, name, description, cost, capacity " +
                    "FROM activities WHERE destination_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectActivitiesByDestinationQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, destinationId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int activityId = resultSet.getInt("activity_id");
                        String name = resultSet.getString("name");
                        String description = resultSet.getString("description");
                        double cost = resultSet.getDouble("cost");
                        int capacity = resultSet.getInt("capacity");

                        Activity activity = Activity.builder()
                                .activityId(activityId)
                                .name(name)
                                .description(description)
                                .cost(cost)
                                .capacity(capacity)
                                .build();

                        activities.add(activity);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activities;
    }

    /**
     * Retrieves a list of activities that still have available space.
     *
     * @return A list of {@link Activity} objects representing activities with available space.
     * @throws SQLException If a database access error occurs.
     */
    public List<Activity> getActivitiesWithAvailableSpace() {
        List<Activity> activitiesWithAvailableSpace = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String selectActivitiesQuery = "SELECT activity_id, name, description, cost, " +
                    "capacity FROM activities WHERE capacity > 0";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectActivitiesQuery);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    int activityId = resultSet.getInt("activity_id");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    double cost = resultSet.getDouble("cost");
                    int capacity = resultSet.getInt("capacity");

                    Activity activity = Activity.builder()
                            .activityId(activityId)
                            .name(name)
                            .description(description)
                            .cost(cost)
                            .capacity(capacity)
                            .build();

                    activitiesWithAvailableSpace.add(activity);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return activitiesWithAvailableSpace;
    }


    /**
     * Generates a unique activity ID for a new activity record.
     * This method generates a new unique identifier for activity records. The generated
     * ID can be used when creating new activity objects to ensure that each activity has
     * a distinct identifier.
     *
     * @return A unique activityId as an integer.
     */
    public int generateActivityId() {
        int generatedActivityId = -1;

        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String sql = "INSERT INTO activities DEFAULT VALUES RETURNING activity_id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    generatedActivityId = resultSet.getInt("activity_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedActivityId;
    }

    private Destination getDestinationById(int destinationId) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUsername, jdbcPassword)) {
            String selectDestinationByIdQuery = "SELECT name FROM destinations WHERE destination_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(selectDestinationByIdQuery)) {
                int i = 1;
                preparedStatement.setInt(i++, destinationId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String name = resultSet.getString("name");
                        return Destination.builder()
                                .destinationId(destinationId)
                                .name(name)
                                .build();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
