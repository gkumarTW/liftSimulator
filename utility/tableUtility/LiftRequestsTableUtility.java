package utility.tableUtility;

import lifts.LiftRequestStatus;
import utility.DBConstants;
import utility.DBUtility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class LiftRequestsTableUtility {
    public static boolean createLiftRequestsTable(Connection connection) throws SQLException {
        StringBuilder createLiftRequestsTableSQL = new StringBuilder()
                .append(DBConstants.CREATE).append(DBConstants.SPACE).append(DBConstants.TABLE)
                .append(DBUtility.doubleQuoted("lift_requests")).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.doubleQuoted("id")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.SPACE).append(DBConstants.NOT_NULL).append(DBConstants.SPACE)
                .append(DBConstants.UNIQUE).append(DBConstants.SPACE).append(DBConstants.GENERATED)
                .append(DBConstants.SPACE).append(DBConstants.BY).append(DBConstants.SPACE).append(DBConstants.DEFAULT)
                .append(DBConstants.SPACE).append(DBConstants.AS).append(DBConstants.SPACE).append(DBConstants.IDENTITY)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("lift_id")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("pick_up_floor")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("drop_off_floor")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("passenger_count")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.COMMA).append(DBUtility.doubleQuoted("status")).append(DBConstants.SPACE)
                .append(DBConstants.VARCHAR).append(DBConstants.OPEN_PARENTHESIS).append("20")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE).append(DBConstants.CHECK).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS).append(DBUtility.doubleQuoted("status")).append(DBConstants.SPACE).append(DBConstants.IN)
                .append(DBConstants.SPACE).append(DBConstants.OPEN_PARENTHESIS).append(DBUtility.singleQuoted("PENDING"))
                .append(DBConstants.COMMA).append(DBConstants.SPACE).append(DBUtility.singleQuoted("IN_PROGRESS")).append(DBConstants.COMMA)
                .append(DBConstants.SPACE).append(DBUtility.singleQuoted("COMPLETED")).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.COMMA)
                .append(DBConstants.PRIMARY_KEY)
                .append(DBConstants.OPEN_PARENTHESIS).append(DBUtility.doubleQuoted("id")).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createLiftRequestsTableSQL.toString());
        }
        return true;
    }

    public static boolean addLiftRequestsForeignKey(Connection connection) throws SQLException {
        StringBuilder addForeignKeySQL = new StringBuilder().append(DBConstants.ALTER).append(DBConstants.SPACE)
                .append(DBConstants.TABLE).append(DBConstants.DOUBLE_QUOTE).append("lift_requests")
                .append(DBConstants.DOUBLE_QUOTE).append(DBConstants.SPACE).append(DBConstants.ADD)
                .append(DBConstants.SPACE).append(DBConstants.FOREIGN_KEY).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBConstants.DOUBLE_QUOTE).append("lift_id").append(DBConstants.DOUBLE_QUOTE)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE).append(DBConstants.REFERENCES)
                .append(DBConstants.DOUBLE_QUOTE).append("lifts").append(DBConstants.DOUBLE_QUOTE)
                .append(DBConstants.OPEN_PARENTHESIS).append(DBConstants.DOUBLE_QUOTE).append("id")
                .append(DBConstants.DOUBLE_QUOTE).append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE)
                .append(DBConstants.ON).append(DBConstants.SPACE).append(DBConstants.UPDATE).append(DBConstants.SPACE)
                .append(DBConstants.NO).append(DBConstants.SPACE).append(DBConstants.ACTION).append(DBConstants.SPACE)
                .append(DBConstants.ON).append(DBConstants.SPACE).append(DBConstants.DELETE).append(DBConstants.SPACE)
                .append(DBConstants.NO).append(DBConstants.SPACE).append(DBConstants.ACTION)
                .append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(addForeignKeySQL.toString());
        }
        return true;
    }

    public static boolean insertLiftRequestsData(Connection connection, int liftId, int pickUpFloor,
                                                  int dropOffFloor, int passengerCount) throws SQLException{
        StringBuilder insertIntoLiftRequestsSQL = new StringBuilder()
                .append(DBConstants.INSERT).append(DBConstants.SPACE)
                .append(DBConstants.INTO).append(DBConstants.SPACE)
                .append("lift_requests")
                .append(DBConstants.OPEN_PARENTHESIS)
                .append("lift_id").append(DBConstants.COMMA)
                .append("pick_up_floor").append(DBConstants.COMMA)
                .append("drop_off_floor").append(DBConstants.COMMA)
                .append("passenger_count").append(DBConstants.COMMA)
                .append("status")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE)
                .append(DBConstants.VALUES).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append("?, ?, ?, ?, ").append(DBUtility.singleQuoted("PENDING"))
                .append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.SEMICOLON);

        try(PreparedStatement ps=connection.prepareStatement(insertIntoLiftRequestsSQL.toString())){
            ps.setInt(1, liftId);
            ps.setInt(2, pickUpFloor);
            ps.setInt(3, dropOffFloor);
            ps.setInt(4, passengerCount);

            int res = ps.executeUpdate();
            return res > 0;
        }
    }

    public static boolean updateStatusById(Connection connection, int liftId, LiftRequestStatus status) {
        StringBuilder updateLiftStatusByIdSQL = new StringBuilder()
                .append(DBConstants.UPDATE).append(DBConstants.SPACE).append("lift_requests").append(DBConstants.SPACE)
                .append(DBConstants.SET).append(DBConstants.SPACE).append("status").append(DBConstants.SPACE)
                .append(DBConstants.EQUALS).append(DBConstants.SPACE).append("?").append(DBConstants.SPACE)
                .append(DBConstants.WHERE).append(DBConstants.SPACE).append("lift_id").append(DBConstants.SPACE)
                .append(DBConstants.EQUALS).append(DBConstants.SPACE).append("?").append(DBConstants.SEMICOLON);
        try (PreparedStatement ps = connection.prepareStatement(updateLiftStatusByIdSQL.toString())) {
            ps.setString(1, status.name()); // enum -> String
            ps.setInt(2, liftId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


}
