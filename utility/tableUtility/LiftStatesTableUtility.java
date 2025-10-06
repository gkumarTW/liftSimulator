package utility.tableUtility;

import lifts.LiftStates;
import utility.DBConstants;

import java.sql.*;

import static utility.DBUtility.doubleQuoted;

public class LiftStatesTableUtility {

    public static boolean createLiftStatesTable(Connection connection) throws SQLException {
        StringBuilder createLiftStatesTableSQL = new StringBuilder()
                .append(DBConstants.CREATE).append(DBConstants.SPACE).append(DBConstants.TABLE)
                .append(doubleQuoted("lift_states")).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append(doubleQuoted("id")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.SPACE).append(DBConstants.NOT_NULL).append(DBConstants.SPACE)
                .append(DBConstants.UNIQUE).append(DBConstants.SPACE).append(DBConstants.GENERATED)
                .append(DBConstants.SPACE).append(DBConstants.BY).append(DBConstants.SPACE).append(DBConstants.DEFAULT)
                .append(DBConstants.SPACE).append(DBConstants.AS).append(DBConstants.SPACE).append(DBConstants.IDENTITY)
                .append(DBConstants.COMMA).append(doubleQuoted("state")).append(DBConstants.SPACE).append(DBConstants.TEXT)
                .append(DBConstants.COMMA).append(DBConstants.PRIMARY_KEY)
                .append(DBConstants.OPEN_PARENTHESIS).append(doubleQuoted("id")).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createLiftStatesTableSQL.toString());
        }
        return true;
    }

    public static int getStateId(Connection connection, String state) throws SQLException {
        int stateId=-1;
        StringBuilder getStateIdSQL=new StringBuilder()
                .append(DBConstants.SELECT).append(DBConstants.SPACE).append("id").append(DBConstants.SPACE)
                .append(DBConstants.FROM).append(DBConstants.SPACE).append("lift_states").append(DBConstants.SPACE)
                .append(DBConstants.WHERE).append(DBConstants.SPACE).append("state").append(DBConstants.EQUALS)
                .append("?").append(DBConstants.SEMICOLON);
        try(PreparedStatement ps=connection.prepareStatement(getStateIdSQL.toString())){
            ps.setString(1, state.toLowerCase());
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    stateId=rs.getInt("id");
                }
            }
        }
        return stateId;
    }

    public static int getStateId(Connection connection, LiftStates state) throws SQLException {
        int stateId=-1;
        switch (state){
            case goingUp:
                stateId=getStateId(connection, "going_up");
                break;
            case goingDown:
                stateId=getStateId(connection, "going_down");
                break;
            case idle:
                stateId=getStateId(connection, "idle");
                break;
        }
        return stateId;
    }

    public static boolean insertLiftStatesData(Connection connection) throws SQLException {
        StringBuilder insertIntoLiftStatesSQL = new StringBuilder().append(DBConstants.INSERT).append(DBConstants.SPACE)
                .append(DBConstants.INTO).append(DBConstants.SPACE).append("lift_states")
                .append(DBConstants.OPEN_PARENTHESIS).append("state").append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.SPACE).append(DBConstants.VALUES).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBConstants.SINGLE_QUOTE).append("going_up").append(DBConstants.SINGLE_QUOTE)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.COMMA).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBConstants.SINGLE_QUOTE).append("going_down").append(DBConstants.SINGLE_QUOTE)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.COMMA).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBConstants.SINGLE_QUOTE).append("idle").append(DBConstants.SINGLE_QUOTE)
                .append(DBConstants.CLOSED_PARENTHESIS);
        try (Statement statement = connection.createStatement()) {
            int res = statement.executeUpdate(insertIntoLiftStatesSQL.toString());
            return res > 0;
        }
    }
}
