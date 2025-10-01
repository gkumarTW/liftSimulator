package utility.tableUtility;

import utility.DBConstants;
import utility.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class BuildingsTableUtility {
    public static boolean createBuildingsTable(Connection connection) throws SQLException {
        StringBuilder createBuildingsTableSQL = new StringBuilder()
                .append(DBConstants.CREATE).append(DBConstants.SPACE).append(DBConstants.TABLE)
                .append(DBUtility.doubleQuoted("buildings")).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.doubleQuoted("id")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.SPACE).append(DBConstants.NOT_NULL).append(DBConstants.SPACE).append(DBConstants.UNIQUE)
                .append(DBConstants.SPACE).append(DBConstants.GENERATED).append(DBConstants.SPACE).append(DBConstants.BY)
                .append(DBConstants.SPACE).append(DBConstants.DEFAULT).append(DBConstants.SPACE).append(DBConstants.AS)
                .append(DBConstants.SPACE).append(DBConstants.IDENTITY)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("min_floor")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("max_floor")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBConstants.PRIMARY_KEY)
                .append(DBConstants.OPEN_PARENTHESIS).append(DBUtility.doubleQuoted("id")).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createBuildingsTableSQL.toString());
        }
        return true;
    }

    private static boolean insertBuildingData(Connection connection,
                                              int minFloor, int maxFloor) throws SQLException {
        StringBuilder insertIntoBuildingsSQL = new StringBuilder()
                .append(DBConstants.INSERT).append(DBConstants.SPACE)
                .append(DBConstants.INTO).append(DBConstants.SPACE)
                .append("buildings")
                .append(DBConstants.OPEN_PARENTHESIS)
                .append("min_floor").append(DBConstants.COMMA)
                .append("max_floor")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE)
                .append(DBConstants.VALUES).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append("?, ?")
                .append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.SEMICOLON);

        try (PreparedStatement ps = connection.prepareStatement(insertIntoBuildingsSQL.toString())) {
            ps.setInt(1, minFloor);
            ps.setInt(2, maxFloor);
            int res = ps.executeUpdate();
            return res > 0;
        }
    }
}
