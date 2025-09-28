package main;

public interface DBConstants {
    // -------------------------------
    // DB Connection
    // -------------------------------
    int PORT = 5432;
    String HOST = "localhost";
    String DB_NAME = "java_jdbc";
    String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;

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

    // -------------------------------
    // Common Data Types (PostgreSQL)
    // -------------------------------
    String INT = "INT";
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
    String LIKE = "LIKE";
    String LIMIT = "LIMIT";
}
