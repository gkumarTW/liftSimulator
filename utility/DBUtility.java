package utility;

import java.sql.*;

public class DBUtility {
    public static boolean dropTable(Connection connection, String tableName) throws SQLException {
        String dropSQL = DBConstants.DROP + DBConstants.SPACE + DBConstants.TABLE + DBConstants.SPACE
                + DBConstants.IF + DBConstants.SPACE + DBConstants.EXISTS + DBConstants.SPACE + tableName + " CASCADE";
        // Using CASCADE ensures all dependent foreign keys are automatically removed
        try (Statement stmt = connection.createStatement()) {
            int result = stmt.executeUpdate(dropSQL);
            System.out.println("Dropped table: " + tableName + ", result: " + result);
        }
        return true;
    }

    public static boolean clearDB(Connection connection) throws SQLException {
        String getExistingDBSQL = DBConstants.SELECT + " tablename " + DBConstants.FROM
                + " pg_tables " + DBConstants.WHERE + " schemaname = 'public'";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(getExistingDBSQL)) {

            while (rs.next()) {
                String tableName = rs.getString("tablename"); // exact column name
                dropTable(connection, tableName);
            }
        }
        return true;
    }

    private static boolean createLiftsTable(Connection connection) throws SQLException {
        StringBuilder createLiftsTableSQL = new StringBuilder();
        String createLiftsTableSQL = DBConstants.CREATE + DBConstants.SPACE + DBConstants.TABLE
                + DBConstants.DOUBLE_QUOTE + "lifts" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                + DBConstants.OPEN_PARENTHESIS
                + DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE + DBConstants.INT
                + DBConstants.SPACE + DBConstants.NOT_NULL + DBConstants.SPACE + DBConstants.UNIQUE + DBConstants.SPACE
                + DBConstants.GENERATED + DBConstants.SPACE + DBConstants.BY + DBConstants.SPACE + DBConstants.DEFAULT
                + DBConstants.SPACE + DBConstants.AS + DBConstants.SPACE + DBConstants.IDENTITY + DBConstants.COMMA
                + DBConstants.DOUBLE_QUOTE + "building_id" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                + DBConstants.INT + DBConstants.COMMA
                + DBConstants.DOUBLE_QUOTE + "min_floor" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                + DBConstants.INT + DBConstants.COMMA
                + DBConstants.DOUBLE_QUOTE + "max_floor" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                + DBConstants.INT + DBConstants.COMMA
                + DBConstants.DOUBLE_QUOTE + "current_floor" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                + DBConstants.INT + DBConstants.SPACE + DBConstants.CHECK + DBConstants.OPEN_PARENTHESIS + "current_floor "
                + DBConstants.LESS_THAN_OR_EQUAL_TO + " max_floor" + DBConstants.CLOSED_PARENTHESIS + DBConstants.COMMA
                + DBConstants.DOUBLE_QUOTE + "current_capacity" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                + DBConstants.INT + DBConstants.SPACE + DBConstants.CHECK + DBConstants.OPEN_PARENTHESIS + "current_capacity "
                + DBConstants.LESS_THAN_OR_EQUAL_TO + " max_capacity"
                + DBConstants.CLOSED_PARENTHESIS + DBConstants.COMMA
                + DBConstants.DOUBLE_QUOTE + "max_capacity" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                + DBConstants.INT + DBConstants.COMMA + DBConstants.DOUBLE_QUOTE + "state" + DBConstants.DOUBLE_QUOTE
                + DBConstants.SPACE + DBConstants.INT + DBConstants.COMMA
                + DBConstants.DOUBLE_QUOTE + "brand" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                + DBConstants.INT + DBConstants.COMMA + DBConstants.PRIMARY_KEY
                + DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE
                + DBConstants.CLOSED_PARENTHESIS + DBConstants.CLOSED_PARENTHESIS + DBConstants.SEMICOLON;
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(createLiftsTableSQL);
        }
        return true;
    }

    private static boolean createLiftRequestsTable(Connection connection) throws SQLException {
        String createLiftRequestsTableSQL =
                DBConstants.CREATE + DBConstants.SPACE + DBConstants.TABLE +
                        DBConstants.DOUBLE_QUOTE + "lift_requests" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE +
                        DBConstants.OPEN_PARENTHESIS +
                        DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE +
                        DBConstants.INT + DBConstants.SPACE + DBConstants.NOT_NULL + DBConstants.SPACE
                        + DBConstants.UNIQUE + DBConstants.SPACE + DBConstants.GENERATED + DBConstants.SPACE
                        + DBConstants.BY + DBConstants.SPACE + DBConstants.DEFAULT + DBConstants.SPACE + DBConstants.AS
                        + DBConstants.SPACE + DBConstants.IDENTITY + DBConstants.COMMA +

                        DBConstants.DOUBLE_QUOTE + "lift_id" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                        + DBConstants.INT + DBConstants.COMMA + DBConstants.DOUBLE_QUOTE + "pick_up_floor"
                        + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE + DBConstants.INT + DBConstants.COMMA +
                        DBConstants.DOUBLE_QUOTE + "drop_off_floor" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                        + DBConstants.INT + DBConstants.COMMA + DBConstants.DOUBLE_QUOTE + "passenger_count"
                        + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE + DBConstants.INT + DBConstants.COMMA +

                        DBConstants.PRIMARY_KEY + DBConstants.OPEN_PARENTHESIS +
                        DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE +
                        DBConstants.CLOSED_PARENTHESIS +
                        DBConstants.CLOSED_PARENTHESIS + DBConstants.SEMICOLON;
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(createLiftRequestsTableSQL);
        }
        return true;
    }

    private static boolean createLiftBrandsTable(Connection connection) throws SQLException {
        String createLiftBrandsTableSQL =
                DBConstants.CREATE + DBConstants.SPACE + DBConstants.TABLE +
                        DBConstants.DOUBLE_QUOTE + "lift_brands" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE +
                        DBConstants.OPEN_PARENTHESIS +
                        DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE +
                        DBConstants.INT + DBConstants.SPACE + DBConstants.NOT_NULL + DBConstants.SPACE
                        + DBConstants.UNIQUE + DBConstants.SPACE + DBConstants.GENERATED + DBConstants.SPACE
                        + DBConstants.BY + DBConstants.SPACE + DBConstants.DEFAULT + DBConstants.SPACE + DBConstants.AS
                        + DBConstants.SPACE + DBConstants.IDENTITY + DBConstants.COMMA +

                        DBConstants.DOUBLE_QUOTE + "brand" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                        + DBConstants.TEXT + DBConstants.COMMA + DBConstants.DOUBLE_QUOTE + "floor_travel_time_ms"
                        + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE + DBConstants.BIGINT + DBConstants.COMMA +
                        DBConstants.DOUBLE_QUOTE + "boarding_time_ms" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                        + DBConstants.BIGINT + DBConstants.COMMA +

                        DBConstants.PRIMARY_KEY + DBConstants.OPEN_PARENTHESIS +
                        DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE +
                        DBConstants.CLOSED_PARENTHESIS +
                        DBConstants.CLOSED_PARENTHESIS + DBConstants.SEMICOLON;
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(createLiftBrandsTableSQL);
        }
        return true;
    }

    private static boolean createLiftStates(Connection connection) throws SQLException {
        String createLiftStatesTableSQL =
                DBConstants.CREATE + DBConstants.SPACE + DBConstants.TABLE +
                        DBConstants.DOUBLE_QUOTE + "lift_states" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                        + DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE
                        + DBConstants.SPACE + DBConstants.INT + DBConstants.SPACE + DBConstants.NOT_NULL
                        + DBConstants.SPACE + DBConstants.UNIQUE + DBConstants.SPACE + DBConstants.GENERATED
                        + DBConstants.SPACE + DBConstants.BY + DBConstants.SPACE + DBConstants.DEFAULT
                        + DBConstants.SPACE + DBConstants.AS + DBConstants.SPACE + DBConstants.IDENTITY
                        + DBConstants.COMMA +

                        DBConstants.DOUBLE_QUOTE + "state" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE
                        + DBConstants.TEXT + DBConstants.COMMA +

                        DBConstants.PRIMARY_KEY + DBConstants.OPEN_PARENTHESIS +
                        DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE +
                        DBConstants.CLOSED_PARENTHESIS +
                        DBConstants.CLOSED_PARENTHESIS + DBConstants.SEMICOLON;
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(createLiftStatesTableSQL);
        }
        return true;
    }

    private static boolean createBuildingsTable(Connection connection) throws SQLException {
        String createBuildingsTableSQL =
                DBConstants.CREATE + DBConstants.SPACE + DBConstants.TABLE +
                        DBConstants.DOUBLE_QUOTE + "buildings" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE +
                        DBConstants.OPEN_PARENTHESIS +
                        DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE +
                        DBConstants.INT + DBConstants.SPACE + DBConstants.NOT_NULL + DBConstants.SPACE +
                        DBConstants.UNIQUE + DBConstants.SPACE + DBConstants.GENERATED + DBConstants.SPACE +
                        DBConstants.BY + DBConstants.SPACE + DBConstants.DEFAULT + DBConstants.SPACE + DBConstants.AS +
                        DBConstants.SPACE + DBConstants.IDENTITY + DBConstants.COMMA +

                        DBConstants.DOUBLE_QUOTE + "minFloor" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE +
                        DBConstants.INT + DBConstants.COMMA +
                        DBConstants.DOUBLE_QUOTE + "maxFloor" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE +
                        DBConstants.INT + DBConstants.COMMA +

                        DBConstants.PRIMARY_KEY + DBConstants.OPEN_PARENTHESIS +
                        DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE +
                        DBConstants.CLOSED_PARENTHESIS +
                        DBConstants.CLOSED_PARENTHESIS + DBConstants.SEMICOLON;
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(createBuildingsTableSQL);
        }
        return true;
    }

    private static boolean addLiftRequestsForeignKey(Connection connection) throws SQLException {
        String addForeignKeySQL = DBConstants.ALTER + DBConstants.SPACE + DBConstants.TABLE + DBConstants.DOUBLE_QUOTE +
                "lift_requests" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE + DBConstants.ADD + DBConstants.SPACE +
                DBConstants.FOREIGN_KEY + DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "lift_id" +
                DBConstants.DOUBLE_QUOTE + DBConstants.CLOSED_PARENTHESIS + DBConstants.SPACE + DBConstants.REFERENCES +
                DBConstants.DOUBLE_QUOTE + "lifts" + DBConstants.DOUBLE_QUOTE + DBConstants.OPEN_PARENTHESIS +
                DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE + DBConstants.CLOSED_PARENTHESIS
                + DBConstants.SPACE + DBConstants.ON + DBConstants.UPDATE + DBConstants.NO + DBConstants.ACTION +
                DBConstants.ON + DBConstants.DELETE + DBConstants.NO + DBConstants.ACTION + DBConstants.SEMICOLON;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(addForeignKeySQL);
        }
        return true;
    }


    private static boolean addLiftsBrandForeignKey(Connection connection) throws SQLException {
        String addForeignKeySQL = DBConstants.ALTER + DBConstants.SPACE + DBConstants.TABLE + DBConstants.DOUBLE_QUOTE +
                "lifts" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE + DBConstants.ADD + DBConstants.SPACE +
                DBConstants.FOREIGN_KEY + DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "brand" +
                DBConstants.DOUBLE_QUOTE + DBConstants.CLOSED_PARENTHESIS + DBConstants.SPACE + DBConstants.REFERENCES +
                DBConstants.SPACE + DBConstants.DOUBLE_QUOTE + "lift_brands" + DBConstants.DOUBLE_QUOTE +
                DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE +
                DBConstants.CLOSED_PARENTHESIS
                + DBConstants.SPACE + DBConstants.ON + DBConstants.UPDATE + DBConstants.NO + DBConstants.ACTION +
                DBConstants.ON + DBConstants.DELETE + DBConstants.NO + DBConstants.ACTION + DBConstants.SEMICOLON;
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(addForeignKeySQL);
        }
        return true;
    }

    private static boolean addLiftsStateForeignKey(Connection connection) throws SQLException {
        String addForeignKeySQL = DBConstants.ALTER + DBConstants.SPACE + DBConstants.TABLE + DBConstants.DOUBLE_QUOTE +
                "lifts" + DBConstants.DOUBLE_QUOTE + DBConstants.SPACE + DBConstants.ADD + DBConstants.SPACE +
                DBConstants.FOREIGN_KEY + DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "state" +
                DBConstants.DOUBLE_QUOTE + DBConstants.CLOSED_PARENTHESIS + DBConstants.SPACE + DBConstants.REFERENCES +
                DBConstants.SPACE + DBConstants.DOUBLE_QUOTE + "lift_states" + DBConstants.DOUBLE_QUOTE +
                DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE +
                DBConstants.CLOSED_PARENTHESIS + DBConstants.SPACE + DBConstants.ON + DBConstants.UPDATE +
                DBConstants.NO + DBConstants.ACTION + DBConstants.ON + DBConstants.DELETE + DBConstants.NO +
                DBConstants.ACTION + DBConstants.SEMICOLON;
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(addForeignKeySQL);
        }
        return true;
    }

    private static boolean addLiftsBuildingForeignKey(Connection connection) throws SQLException {
        String sql = DBConstants.ALTER + DBConstants.SPACE + DBConstants.TABLE + DBConstants.DOUBLE_QUOTE + "lifts" + DBConstants.DOUBLE_QUOTE
                + DBConstants.SPACE + DBConstants.ADD + DBConstants.SPACE + DBConstants.FOREIGN_KEY
                + DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "building_id" + DBConstants.DOUBLE_QUOTE + DBConstants.CLOSED_PARENTHESIS
                + DBConstants.SPACE + DBConstants.REFERENCES + DBConstants.SPACE + DBConstants.DOUBLE_QUOTE + "buildings" + DBConstants.DOUBLE_QUOTE
                + DBConstants.OPEN_PARENTHESIS + DBConstants.DOUBLE_QUOTE + "id" + DBConstants.DOUBLE_QUOTE + DBConstants.CLOSED_PARENTHESIS
                + DBConstants.SPACE + DBConstants.ON + DBConstants.UPDATE + DBConstants.NO + DBConstants.ACTION +
                DBConstants.ON + DBConstants.DELETE + DBConstants.NO + DBConstants.ACTION + DBConstants.SEMICOLON;
        try (Statement statement = connection.createStatement();) {
            statement.executeUpdate(sql);
        }
        return true;
    }

    public static boolean createTables(Connection connection) throws SQLException {
        createLiftsTable(connection);
        createLiftRequestsTable(connection);
        createLiftBrandsTable(connection);
        createLiftStates(connection);
        createBuildingsTable(connection);
        return true;
    }

    public static boolean addRelations(Connection connection) throws SQLException {
        addLiftRequestsForeignKey(connection);
        addLiftsBrandForeignKey(connection);
        addLiftsStateForeignKey(connection);
        addLiftsBuildingForeignKey(connection);
        return true;
    }

    public static boolean insertLiftStatesData(Connection connection) throws SQLException {
        String insertIntoLiftStatesSQL = DBConstants.INSERT + DBConstants.SPACE + DBConstants.INTO + DBConstants.SPACE +
                "lift_states" + DBConstants.OPEN_PARENTHESIS + "states" + DBConstants.CLOSED_PARENTHESIS +
                DBConstants.SPACE + DBConstants.VALUES + DBConstants.OPEN_PARENTHESIS + DBConstants.SINGLE_QUOTE +
                "going_up" + DBConstants.SINGLE_QUOTE + DBConstants.CLOSED_PARENTHESIS + DBConstants.COMMA +
                DBConstants.OPEN_PARENTHESIS + DBConstants.SINGLE_QUOTE + "going_down" + DBConstants.SINGLE_QUOTE +
                DBConstants.CLOSED_PARENTHESIS + DBConstants.COMMA + DBConstants.OPEN_PARENTHESIS +
                DBConstants.SINGLE_QUOTE + "idle" + DBConstants.SINGLE_QUOTE + DBConstants.CLOSED_PARENTHESIS;
        try (Statement statement = connection.createStatement()) {
            int res = statement.executeUpdate(insertIntoLiftStatesSQL);
            return res > 0;
        }
    }

    public static boolean insertLiftBrandsData(Connection connection) throws SQLException {
        String insertIntoLiftBrandsSQL = DBConstants.INSERT + DBConstants.SPACE + DBConstants.INTO + DBConstants.SPACE +
                "lift_brands" + DBConstants.OPEN_PARENTHESIS + "brands" + DBConstants.COMMA +
                "floor_travel_time_ms" + DBConstants.COMMA +
                "boarding_time_ms" + DBConstants.CLOSED_PARENTHESIS + DBConstants.SPACE +
                DBConstants.VALUES + DBConstants.SPACE + DBConstants.OPEN_PARENTHESIS + DBConstants.SINGLE_QUOTE +
                "Toshiba" + DBConstants.SINGLE_QUOTE + DBConstants.COMMA + "3000" + DBConstants.COMMA + "1000" +
                DBConstants.CLOSED_PARENTHESIS + DBConstants.COMMA + DBConstants.SPACE + DBConstants.OPEN_PARENTHESIS +
                DBConstants.SINGLE_QUOTE + "Otis" + DBConstants.SINGLE_QUOTE + DBConstants.COMMA + "4000" +
                DBConstants.COMMA + "1000" + DBConstants.CLOSED_PARENTHESIS + DBConstants.SEMICOLON;

        try (Statement statement = connection.createStatement()) {
            int res = statement.executeUpdate(insertIntoLiftBrandsSQL);
            return res > 0;
        }
    }

    public static boolean insertBuildingData(Connection connection, int minFloor, int maxFloor) throws SQLException {
        String insertIntoLiftBrandsSQL = DBConstants.INSERT + DBConstants.SPACE + DBConstants.INTO + DBConstants.SPACE +
                "lift_brands" + DBConstants.OPEN_PARENTHESIS + "brands" + DBConstants.COMMA +
                "floor_travel_time_ms" + DBConstants.COMMA +
                "boarding_time_ms" + DBConstants.CLOSED_PARENTHESIS + DBConstants.SPACE +
                DBConstants.VALUES + DBConstants.SPACE + DBConstants.OPEN_PARENTHESIS + DBConstants.SINGLE_QUOTE +
                "Toshiba" + DBConstants.SINGLE_QUOTE + DBConstants.COMMA + "3000" + DBConstants.COMMA + "1000" +
                DBConstants.CLOSED_PARENTHESIS + DBConstants.COMMA + DBConstants.SPACE + DBConstants.OPEN_PARENTHESIS +
                DBConstants.SINGLE_QUOTE + "Otis" + DBConstants.SINGLE_QUOTE + DBConstants.COMMA + "4000" +
                DBConstants.COMMA + "1000" + DBConstants.CLOSED_PARENTHESIS + DBConstants.SEMICOLON;

        try (Statement statement = connection.createStatement()) {
            int res = statement.executeUpdate(insertIntoLiftBrandsSQL);
            return res > 0;
        }
    }


    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD);
            System.out.println("hiii");
            clearDB(connection);
            createTables(connection);
            System.out.println("hi");
            addRelations(connection);
            System.out.println("hii");
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }
}
