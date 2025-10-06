package utility;

public interface DBConstants {
    // -------------------------------
    // TW DB
    // -------------------------------
//    int PORT = 5432;
//    String HOST = "localhost";
//    String DB_NAME = "lift";
//    String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;
//    String USER= "postgres";
//    String PASSWORD="123456";


    // -------------------------------
    // Personal DB
    // -------------------------------
    int PORT = 5432;
    String HOST = "localhost";
    String DB_NAME = "lift_personal";
    String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;
    String USER= "nitish";
    String PASSWORD="secret123";



    // -------------------------------
    // Symbols
    // -------------------------------
    String COMMA = ",";
    String SEMICOLON = ";";
    String SPACE = " ";
    String OPEN_PARENTHESIS = "(";
    String CLOSED_PARENTHESIS = ")";
    String SINGLE_QUOTE = "'";
    String DOUBLE_QUOTE = "\"";
    String EQUALS = "=";
    String STAR = "*";
    String LESS_THAN_OR_EQUAL_TO = "<=";
    String ADD_OPERATOR = "+";

    // -------------------------------
    // SQL Keywords
    // -------------------------------
    String CREATE = "CREATE";
    String TABLE = "TABLE";
    String DROP = "DROP";
    String ALTER = "ALTER";
    String INSERT = "INSERT";
    String INTO = "INTO";
    String VALUES = "VALUES";
    String SELECT = "SELECT";
    String UPDATE = "UPDATE";
    String DELETE = "DELETE";
    String FROM = "FROM";
    String WHERE = "WHERE";
    String SET = "SET";
    String PRIMARY_KEY = "PRIMARY KEY";
    String FOREIGN_KEY = "FOREIGN KEY";
    String REFERENCES = "REFERENCES";
    String NOT_NULL = "NOT NULL";
    String UNIQUE = "UNIQUE";
    String DEFAULT = "DEFAULT";
    String SERIAL = "SERIAL";
    String GENERATED = "GENERATED";
    String BY = "BY";
    String AS = "AS";
    String IDENTITY = "IDENTITY";
    String CHECK = "CHECK";
    String ADD = "ADD";
    String ON = "ON";
    String NO = "NO";
    String ACTION = "ACTION";

    // -------------------------------
    // Common Data Types (PostgreSQL)
    // -------------------------------
    String INT = "INTEGER";
    String BIGINT = "BIGINT";
    String VARCHAR = "VARCHAR";
    String TEXT = "TEXT";
    String BOOLEAN = "BOOLEAN";
    String DATE = "DATE";
    String TIMESTAMP = "TIMESTAMP";

    // -------------------------------
    // Query helpers
    // -------------------------------
    String ORDER_BY = "ORDER BY";
    String GROUP_BY = "GROUP BY";
    String ASC = "ASC";
    String DESC = "DESC";
    String AND = "AND";
    String OR = "OR";
    String IN = "IN";
    String IF = "IF";
    String LIKE = "LIKE";
    String LIMIT = "LIMIT";
    String EXISTS = "EXISTS";
}
