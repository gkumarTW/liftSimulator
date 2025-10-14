package com.lift.simulator.utility;

import com.lift.simulator.constants.DBConstants;
import com.lift.simulator.utility.tableUtility.*;

import java.sql.*;

public class DBUtility {

    private static Connection connection;

    public static String singleQuoted(String str) {
        return new StringBuilder()
                .append(DBConstants.SINGLE_QUOTE).append(str).append(DBConstants.SINGLE_QUOTE).toString();
    }

    public static String doubleQuoted(String str) {
        return new StringBuilder()
                .append(DBConstants.DOUBLE_QUOTE).append(str).append(DBConstants.DOUBLE_QUOTE).toString();
    }

    public static boolean dropTable(Connection connection, String tableName) throws SQLException {
        StringBuilder dropSQL = new StringBuilder().append(DBConstants.DROP).append(DBConstants.SPACE)
                .append(DBConstants.TABLE).append(DBConstants.SPACE).append(DBConstants.IF).append(DBConstants.SPACE)
                .append(DBConstants.EXISTS).append(DBConstants.SPACE).append(tableName).append(" CASCADE");
        try (Statement stmt = connection.createStatement()) {
            int result = stmt.executeUpdate(dropSQL.toString());
            System.out.println("Dropped table: " + tableName + ", result: " + result);
        }
        return true;
    }

    public static boolean clearDB(Connection connection) throws SQLException {
        StringBuilder getExistingDBSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(" tablename ").append(DBConstants.FROM).append(" pg_tables ")
                .append(DBConstants.WHERE).append(" schemaname = 'public'");

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(getExistingDBSQL.toString())) {

            while (rs.next()) {
                String tableName = rs.getString("tablename"); // exact column name
                dropTable(connection, tableName);
            }
        }
        return true;
    }

    public static boolean insertMasterData(Connection connection) throws SQLException {
        return LiftStatesTableUtility.insertLiftStatesData(connection)
                && LiftBrandsTableUtility.insertLiftBrandsData(connection);
    }

    public static boolean addRelations(Connection connection) throws SQLException {
        LiftRequestsTableUtility.addLiftRequestsForeignKey(connection);
        LiftsTableUtility.addLiftsBrandForeignKey(connection);
        LiftsTableUtility.addLiftsStateForeignKey(connection);
        LiftsTableUtility.addLiftsBuildingForeignKey(connection);
        return true;
    }

    public static boolean createTables(Connection connection) throws SQLException {
        LiftsTableUtility.createLiftsTable(connection);
        LiftRequestsTableUtility.createLiftRequestsTable(connection);
        LiftBrandsTableUtility.createLiftBrandsTable(connection);
        LiftStatesTableUtility.createLiftStatesTable(connection);
        BuildingsTableUtility.createBuildingsTable(connection);
        return true;
    }

    public static void prepareDB(Connection connection) throws SQLException {
        clearDB(connection);
        createTables(connection);
        addRelations(connection);
        insertMasterData(connection);
    }


    public static synchronized Connection getConnection() throws SQLException {
        if(connection==null || connection.isClosed()){
            connection = DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD);
        }
        return connection;
    }

}
