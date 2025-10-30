package com.lift.simulator.utility;

import com.lift.simulator.constants.DBConstants;
import com.lift.simulator.utility.tableUtility.*;

import java.sql.*;

public class DBUtility {

    // restricting object creation to this class
    private DBUtility(){}

    private static Connection connection;

    public static String singleQuoted(String str) {
        return new StringBuilder()
                .append(DBConstants.SINGLE_QUOTE).append(str).append(DBConstants.SINGLE_QUOTE).toString();
    }

    public static String doubleQuoted(String str) {
        return new StringBuilder()
                .append(DBConstants.DOUBLE_QUOTE).append(str).append(DBConstants.DOUBLE_QUOTE).toString();
    }

    public static boolean dropTable(String tableName) throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder dropSQL = new StringBuilder().append(DBConstants.DROP).append(DBConstants.SPACE)
                .append(DBConstants.TABLE).append(DBConstants.SPACE).append(DBConstants.IF).append(DBConstants.SPACE)
                .append(DBConstants.EXISTS).append(DBConstants.SPACE).append(tableName).append(" CASCADE");
        try (Statement stmt = connection.createStatement()) {
            int result = stmt.executeUpdate(dropSQL.toString());
            System.out.println("Dropped table: " + tableName + ", result: " + result);
        }
        connection.close();
        return true;
    }

    public static boolean clearDB() throws SQLException {
        Connection connection = DBUtility.getConnection();
        StringBuilder getExistingDBSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(" tablename ").append(DBConstants.FROM).append(" pg_tables ")
                .append(DBConstants.WHERE).append(" schemaname = 'public'");

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getExistingDBSQL.toString())) {

            while (rs.next()) {
                String tableName = rs.getString("tablename"); // exact column name
                dropTable(tableName);
            }
        }
        connection.close();
        return true;
    }

    public static boolean insertMasterData() throws SQLException {
        return LiftStatesTableUtility.insertLiftStatesData()
                && LiftBrandsTableUtility.insertLiftBrandsData();
    }

    public static boolean addRelations() throws SQLException {
        LiftRequestsTableUtility.addLiftRequestsForeignKey();
        LiftsTableUtility.addLiftsBrandForeignKey();
        LiftsTableUtility.addLiftsStateForeignKey();
        LiftsTableUtility.addLiftsBuildingForeignKey();
        return true;
    }

    public static boolean createTables() throws SQLException {
        LiftsTableUtility.createLiftsTable();
        LiftRequestsTableUtility.createLiftRequestsTable();
        LiftBrandsTableUtility.createLiftBrandsTable();
        LiftStatesTableUtility.createLiftStatesTable();
        BuildingsTableUtility.createBuildingsTable();
        return true;
    }

    public static void prepareDB() throws SQLException {
        clearDB();
        createTables();
        addRelations();
        insertMasterData();
    }

    public static synchronized Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD);
    }

}
