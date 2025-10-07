package utility;

import lifts.LiftStates;
import utility.tableUtility.*;

import java.sql.*;

public class DBUtility {

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

    public static boolean prepareDB(Connection connection) throws SQLException {
        clearDB(connection);
        createTables(connection);
        addRelations(connection);
        insertMasterData(connection);
        return true;
    }

/*    public static void main(String[] args) {
        try {
            Connection connection = DriverManager
                    .getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD);
            System.out.println("byee");
//            updateLiftState(connection, 1, LiftStates.goingUp);

//            insertBuildingData(connection, 0, 10);
//            insertLiftsData(connection,1, 0, 10,0, 0, 4, getStateId(connection, LiftStates.idle), 2);
//            insertLiftRequestsData(connection, 2, 3,6,3);

            clearDB(connection);
            System.out.println("bye");
            createTables(connection);
            System.out.println("hi");
            addRelations(connection);
            System.out.println("hii");
            insertMasterData(connection);
            System.out.println("hiii");
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }*/
}
