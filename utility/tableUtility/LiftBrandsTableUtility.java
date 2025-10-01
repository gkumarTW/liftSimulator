package utility.tableUtility;

import utility.DBConstants;
import utility.DBUtility;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LiftBrandsTableUtility {
    public static boolean createLiftBrandsTable(Connection connection) throws SQLException {
        StringBuilder createLiftBrandsTableSQL = new StringBuilder()
                .append(DBConstants.CREATE).append(DBConstants.SPACE).append(DBConstants.TABLE)
                .append(DBUtility.doubleQuoted("lift_brands")).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.doubleQuoted("id")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.SPACE).append(DBConstants.NOT_NULL).append(DBConstants.SPACE)
                .append(DBConstants.UNIQUE).append(DBConstants.SPACE).append(DBConstants.GENERATED)
                .append(DBConstants.SPACE).append(DBConstants.BY).append(DBConstants.SPACE).append(DBConstants.DEFAULT)
                .append(DBConstants.SPACE).append(DBConstants.AS).append(DBConstants.SPACE).append(DBConstants.IDENTITY)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("brand")).append(DBConstants.SPACE).append(DBConstants.TEXT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("floor_travel_time_ms")).append(DBConstants.SPACE).append(DBConstants.BIGINT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("boarding_time_ms")).append(DBConstants.SPACE).append(DBConstants.BIGINT)
                .append(DBConstants.COMMA).append(DBConstants.PRIMARY_KEY)
                .append(DBConstants.OPEN_PARENTHESIS).append(DBUtility.doubleQuoted("id")).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createLiftBrandsTableSQL.toString());
        }
        return true;
    }

    public static boolean insertLiftBrandsData(Connection connection) throws SQLException {
        StringBuilder insertIntoLiftBrandsSQL = new StringBuilder().append(DBConstants.INSERT).append(DBConstants.SPACE)
                .append(DBConstants.INTO).append(DBConstants.SPACE).append("lift_brands")
                .append(DBConstants.OPEN_PARENTHESIS).append("brand").append(DBConstants.COMMA)
                .append("floor_travel_time_ms").append(DBConstants.COMMA).append("boarding_time_ms")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE).append(DBConstants.VALUES)
                .append(DBConstants.SPACE).append(DBConstants.OPEN_PARENTHESIS).append(DBConstants.SINGLE_QUOTE)
                .append("Toshiba").append(DBConstants.SINGLE_QUOTE).append(DBConstants.COMMA).append("3000")
                .append(DBConstants.COMMA).append("1000").append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.COMMA).append(DBConstants.SPACE).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBConstants.SINGLE_QUOTE).append("Otis").append(DBConstants.SINGLE_QUOTE)
                .append(DBConstants.COMMA).append("4000").append(DBConstants.COMMA).append("1000")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            int res = statement.executeUpdate(insertIntoLiftBrandsSQL.toString());
            return res > 0;
        }
    }

}
