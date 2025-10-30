package com.lift.simulator.utility.tableUtility;

import com.lift.simulator.process.LiftI;
import com.lift.simulator.process.LiftStates;
import com.lift.simulator.constants.DBConstants;
import com.lift.simulator.utility.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class LiftsTableUtility {

    // restricting object creation for this class
    private LiftsTableUtility(){}

    private static final String tableName = "lifts";

    public static boolean createLiftsTable() throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder createLiftsTableSQL = new StringBuilder().append(DBConstants.CREATE).append(DBConstants.SPACE)
                .append(DBConstants.TABLE).append(DBUtility.doubleQuoted(tableName)).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS).append(DBUtility.doubleQuoted("id")).append(DBConstants.SPACE)
                .append(DBConstants.INT).append(DBConstants.SPACE).append(DBConstants.NOT_NULL)
                .append(DBConstants.SPACE).append(DBConstants.UNIQUE).append(DBConstants.SPACE)
                .append(DBConstants.GENERATED).append(DBConstants.SPACE).append(DBConstants.BY)
                .append(DBConstants.SPACE).append(DBConstants.DEFAULT).append(DBConstants.SPACE)
                .append(DBConstants.AS).append(DBConstants.SPACE).append(DBConstants.IDENTITY)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("building_id")).append(DBConstants.SPACE)
                .append(DBConstants.INT).append(DBConstants.COMMA).append(DBUtility.doubleQuoted("min_floor"))
                .append(DBConstants.SPACE).append(DBConstants.INT).append(DBConstants.COMMA)
                .append(DBUtility.doubleQuoted("max_floor")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("current_floor")).append(DBConstants.SPACE)
                .append(DBConstants.INT).append(DBConstants.SPACE).append(DBConstants.CHECK)
                .append(DBConstants.OPEN_PARENTHESIS).append("current_floor ").append(DBConstants.LESS_THAN_OR_EQUAL_TO)
                .append(" max_floor").append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.COMMA)
                .append(DBUtility.doubleQuoted("current_capacity")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.SPACE).append(DBConstants.CHECK).append(DBConstants.OPEN_PARENTHESIS)
                .append("current_capacity ").append(DBConstants.LESS_THAN_OR_EQUAL_TO).append(" max_capacity")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.COMMA)
                .append(DBUtility.doubleQuoted("max_capacity")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("state_id")).append(DBConstants.SPACE)
                .append(DBConstants.INT).append(DBConstants.COMMA).append(DBUtility.doubleQuoted("brand_id"))
                .append(DBConstants.SPACE).append(DBConstants.INT).append(DBConstants.COMMA)
                .append(DBConstants.PRIMARY_KEY).append(DBConstants.OPEN_PARENTHESIS).append(DBUtility.doubleQuoted("id"))
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.SEMICOLON);
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(createLiftsTableSQL.toString());
        }
        connection.close();
        return true;
    }

    public static boolean insertLiftsData(int buildingId, int minFloor, int maxFloor,
                                           int currentFloor, int currentCapacity, int maxCapacity,
                                           int stateId, int brandId) throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder insertIntoLiftsSQL = new StringBuilder()
                .append(DBConstants.INSERT).append(DBConstants.SPACE)
                .append(DBConstants.INTO).append(DBConstants.SPACE)
                .append(tableName)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append("building_id").append(DBConstants.COMMA)
                .append("min_floor").append(DBConstants.COMMA)
                .append("max_floor").append(DBConstants.COMMA)
                .append("current_floor").append(DBConstants.COMMA)
                .append("current_capacity").append(DBConstants.COMMA)
                .append("max_capacity").append(DBConstants.COMMA)
                .append("state_id").append(DBConstants.COMMA)
                .append("brand_id")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE)
                .append(DBConstants.VALUES).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append("?, ?, ?, ?, ?, ?, ?, ?")
                .append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.SEMICOLON);

        try (PreparedStatement ps = connection.prepareStatement(insertIntoLiftsSQL.toString())) {
            ps.setInt(1, buildingId);
            ps.setInt(2, minFloor);
            ps.setInt(3, maxFloor);
            ps.setInt(4, currentFloor);
            ps.setInt(5, currentCapacity);
            ps.setInt(6, maxCapacity);
            ps.setInt(7, stateId);
            ps.setInt(8, brandId);

            int res = ps.executeUpdate();
            connection.close();
            return res > 0;
        }

    }

    public static boolean addLiftsBrandForeignKey() throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder addForeignKeySQL = new StringBuilder().append(DBConstants.ALTER).append(DBConstants.SPACE)
                .append(DBConstants.TABLE).append(DBConstants.DOUBLE_QUOTE).append(tableName)
                .append(DBConstants.DOUBLE_QUOTE).append(DBConstants.SPACE).append(DBConstants.ADD)
                .append(DBConstants.SPACE).append(DBConstants.FOREIGN_KEY).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBConstants.DOUBLE_QUOTE).append("brand_id").append(DBConstants.DOUBLE_QUOTE)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE).append(DBConstants.REFERENCES)
                .append(DBConstants.SPACE).append(DBConstants.DOUBLE_QUOTE).append("lift_brands")
                .append(DBConstants.DOUBLE_QUOTE).append(DBConstants.OPEN_PARENTHESIS).append(DBConstants.DOUBLE_QUOTE)
                .append("id").append(DBConstants.DOUBLE_QUOTE).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.SPACE).append(DBConstants.ON).append(DBConstants.SPACE).append(DBConstants.UPDATE)
                .append(DBConstants.SPACE).append(DBConstants.NO).append(DBConstants.SPACE).append(DBConstants.ACTION)
                .append(DBConstants.SPACE).append(DBConstants.ON).append(DBConstants.SPACE).append(DBConstants.DELETE)
                .append(DBConstants.SPACE).append(DBConstants.NO).append(DBConstants.SPACE).append(DBConstants.ACTION)
                .append(DBConstants.SEMICOLON);
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(addForeignKeySQL.toString());
        }
        connection.close();
        return true;
    }

    public static boolean addLiftsStateForeignKey() throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder addForeignKeySQL = new StringBuilder().append(DBConstants.ALTER).append(DBConstants.SPACE)
                .append(DBConstants.TABLE).append(DBConstants.DOUBLE_QUOTE).append(tableName)
                .append(DBConstants.DOUBLE_QUOTE).append(DBConstants.SPACE).append(DBConstants.ADD)
                .append(DBConstants.SPACE).append(DBConstants.FOREIGN_KEY).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBConstants.DOUBLE_QUOTE).append("state_id").append(DBConstants.DOUBLE_QUOTE)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE).append(DBConstants.REFERENCES)
                .append(DBConstants.SPACE).append(DBConstants.DOUBLE_QUOTE).append("lift_states")
                .append(DBConstants.DOUBLE_QUOTE).append(DBConstants.OPEN_PARENTHESIS).append(DBConstants.DOUBLE_QUOTE)
                .append("id").append(DBConstants.DOUBLE_QUOTE).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.SPACE).append(DBConstants.ON).append(DBConstants.SPACE).append(DBConstants.UPDATE)
                .append(DBConstants.SPACE).append(DBConstants.NO).append(DBConstants.SPACE).append(DBConstants.ACTION)
                .append(DBConstants.SPACE).append(DBConstants.ON).append(DBConstants.SPACE).append(DBConstants.DELETE)
                .append(DBConstants.SPACE).append(DBConstants.NO).append(DBConstants.SPACE).append(DBConstants.ACTION)
                .append(DBConstants.SEMICOLON);
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(addForeignKeySQL.toString());
        }
        connection.close();
        return true;
    }

    public static boolean addLiftsBuildingForeignKey() throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder addForeignKeySQL = new StringBuilder().append(DBConstants.ALTER).append(DBConstants.SPACE)
                .append(DBConstants.TABLE).append(DBConstants.DOUBLE_QUOTE).append(tableName)
                .append(DBConstants.DOUBLE_QUOTE).append(DBConstants.SPACE).append(DBConstants.ADD)
                .append(DBConstants.SPACE).append(DBConstants.FOREIGN_KEY).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBConstants.DOUBLE_QUOTE).append("building_id").append(DBConstants.DOUBLE_QUOTE)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE).append(DBConstants.REFERENCES)
                .append(DBConstants.SPACE).append(DBConstants.DOUBLE_QUOTE).append("buildings")
                .append(DBConstants.DOUBLE_QUOTE).append(DBConstants.OPEN_PARENTHESIS).append(DBConstants.DOUBLE_QUOTE)
                .append("id").append(DBConstants.DOUBLE_QUOTE).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.SPACE).append(DBConstants.ON).append(DBConstants.SPACE).append(DBConstants.UPDATE)
                .append(DBConstants.SPACE).append(DBConstants.NO).append(DBConstants.SPACE).append(DBConstants.ACTION)
                .append(DBConstants.SPACE).append(DBConstants.ON).append(DBConstants.SPACE).append(DBConstants.DELETE)
                .append(DBConstants.SPACE).append(DBConstants.NO).append(DBConstants.SPACE).append(DBConstants.ACTION)
                .append(DBConstants.SEMICOLON);
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(addForeignKeySQL.toString());
        }
        connection.close();
        return true;
    }

    public static boolean addNewLift(LiftI lift) throws SQLException {
        return insertLiftsData( lift.getBuildingId(), lift.getMinFloor(), lift.getMaxFloor(),
                lift.getCurrentFloor(), lift.getCurrentCapacity(), lift.getTotalCapacity(),
                LiftStatesTableUtility.getStateId(lift.getCurrState()), lift.getBrandId());
    }

    public static boolean updateLiftState(int liftId, LiftStates state) throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder updateLiftStateSQL = new StringBuilder()
                .append(DBConstants.UPDATE).append(DBConstants.SPACE)
                .append(tableName).append(DBConstants.SPACE)
                .append(DBConstants.SET).append(DBConstants.SPACE)
                .append("state_id").append(DBConstants.EQUALS).append("?").append(DBConstants.SPACE)
                .append(DBConstants.WHERE).append(DBConstants.SPACE)
                .append("id").append(DBConstants.EQUALS).append("?");

        try (PreparedStatement ps = connection.prepareStatement(updateLiftStateSQL.toString())) {
            ps.setInt(1, LiftStatesTableUtility.getStateId(state));
            ps.setInt(2, liftId);

            int rowsAffected = ps.executeUpdate();
            connection.close();
            return rowsAffected > 0;        // true if update succeeded
        }
    }

    public static boolean updateLiftCurrentFloor(int liftId, int currentFloor) throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder updateLiftCurrentFloorSQL = new StringBuilder()
                .append(DBConstants.UPDATE).append(DBConstants.SPACE)
                .append(tableName).append(DBConstants.SPACE)
                .append(DBConstants.SET).append(DBConstants.SPACE)
                .append("current_floor").append(DBConstants.EQUALS).append("?").append(DBConstants.SPACE)
                .append(DBConstants.WHERE).append(DBConstants.SPACE)
                .append("id").append(DBConstants.EQUALS).append("?");

        try (PreparedStatement ps = connection.prepareStatement(updateLiftCurrentFloorSQL.toString())) {
            ps.setInt(1, currentFloor);
            ps.setInt(2, liftId);

            int rowsAffected = ps.executeUpdate();
            connection.close();
            return rowsAffected > 0;
        }
    }

    public static boolean updateLiftCurrentCapacity(int liftId, int currentCapacity) throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder updateLiftCurrentCapacitySQL = new StringBuilder()
                .append(DBConstants.UPDATE).append(DBConstants.SPACE)
                .append(tableName).append(DBConstants.SPACE)
                .append(DBConstants.SET).append(DBConstants.SPACE)
                .append("current_capacity").append(DBConstants.EQUALS).append("?").append(DBConstants.SPACE)
                .append(DBConstants.WHERE).append(DBConstants.SPACE)
                .append("id").append(DBConstants.EQUALS).append("?");

        try (PreparedStatement ps = connection.prepareStatement(updateLiftCurrentCapacitySQL.toString())) {
            ps.setInt(1, currentCapacity);
            ps.setInt(2, liftId);

            int rowsAffected = ps.executeUpdate();
            connection.close();
            return rowsAffected > 0;
        }
    }

}
