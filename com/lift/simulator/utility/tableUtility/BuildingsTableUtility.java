package com.lift.simulator.utility.tableUtility;

import com.lift.simulator.constants.DBConstants;
import com.lift.simulator.utility.DBUtility;

import java.sql.*;

public class BuildingsTableUtility {

    // restricting object creation for this class
    private BuildingsTableUtility(){}

    private static final String tableName = "buildings";

    public static boolean createBuildingsTable(Connection connection) throws SQLException {
        StringBuilder createBuildingsTableSQL = new StringBuilder()
                .append(DBConstants.CREATE).append(DBConstants.SPACE).append(DBConstants.TABLE)
                .append(DBUtility.doubleQuoted(tableName)).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.doubleQuoted("id")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.SPACE).append(DBConstants.NOT_NULL).append(DBConstants.SPACE)
                .append(DBConstants.UNIQUE).append(DBConstants.SPACE).append(DBConstants.GENERATED)
                .append(DBConstants.SPACE).append(DBConstants.BY).append(DBConstants.SPACE).append(DBConstants.DEFAULT)
                .append(DBConstants.SPACE).append(DBConstants.AS).append(DBConstants.SPACE).append(DBConstants.IDENTITY)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("min_floor")).append(DBConstants.SPACE)
                .append(DBConstants.INT).append(DBConstants.COMMA).append(DBUtility.doubleQuoted("max_floor"))
                .append(DBConstants.SPACE).append(DBConstants.INT).append(DBConstants.COMMA)
                .append(DBUtility.doubleQuoted("no_of_lifts")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBConstants.PRIMARY_KEY).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.doubleQuoted("id")).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createBuildingsTableSQL.toString());
        }
        return true;
    }

    public static boolean insertBuildingData(Connection connection,
                                              int minFloor, int maxFloor, int noOfLifts) throws SQLException {
        StringBuilder insertIntoBuildingsSQL = new StringBuilder()
                .append(DBConstants.INSERT).append(DBConstants.SPACE).append(DBConstants.INTO).append(DBConstants.SPACE)
                .append(tableName).append(DBConstants.OPEN_PARENTHESIS).append("min_floor").append(DBConstants.COMMA)
                .append("max_floor").append(DBConstants.COMMA).append("no_of_lifts")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE).append(DBConstants.VALUES)
                .append(DBConstants.SPACE).append(DBConstants.OPEN_PARENTHESIS).append("?, ?, ?")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (PreparedStatement ps = connection.prepareStatement(insertIntoBuildingsSQL.toString())) {
            ps.setInt(1, minFloor);
            ps.setInt(2, maxFloor);
            ps.setInt(3, noOfLifts);
            int res = ps.executeUpdate();
            return res > 0;
        }
    }

    public static int getMaxFloorById(Connection connection, int buildingId) throws SQLException {
        int maxFloor = -1;

        StringBuilder getMaxFloorByIdSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(DBConstants.SPACE).append("max_floor").append(DBConstants.SPACE)
                .append(DBConstants.FROM).append(DBConstants.SPACE).append(tableName).append(DBConstants.SPACE)
                .append(DBConstants.WHERE).append(DBConstants.SPACE).append("id")
                .append(DBConstants.EQUALS).append("?").append(DBConstants.SEMICOLON);

        try (PreparedStatement ps = connection.prepareStatement(getMaxFloorByIdSQL.toString())){
            ps.setInt(1, buildingId);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    maxFloor=rs.getInt("max_floor");
                }
            }
        }
        return maxFloor;
    }
}
