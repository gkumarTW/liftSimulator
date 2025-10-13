package com.lift.simulator.utility.tableUtility;

import com.lift.simulator.constants.DBConstants;
import com.lift.simulator.utility.DBUtility;

import java.sql.*;
import java.util.ArrayList;

public class LiftBrandsTableUtility {
    static final String tableName = "lift_brands";

    public static boolean createLiftBrandsTable(Connection connection) throws SQLException {
        StringBuilder createLiftBrandsTableSQL = new StringBuilder()
                .append(DBConstants.CREATE).append(DBConstants.SPACE).append(DBConstants.TABLE)
                .append(DBUtility.doubleQuoted(tableName)).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.doubleQuoted("id")).append(DBConstants.SPACE).append(DBConstants.INT)
                .append(DBConstants.SPACE).append(DBConstants.NOT_NULL).append(DBConstants.SPACE)
                .append(DBConstants.UNIQUE).append(DBConstants.SPACE).append(DBConstants.GENERATED)
                .append(DBConstants.SPACE).append(DBConstants.BY).append(DBConstants.SPACE)
                .append(DBConstants.DEFAULT).append(DBConstants.SPACE).append(DBConstants.AS)
                .append(DBConstants.SPACE).append(DBConstants.IDENTITY).append(DBConstants.COMMA)
                .append(DBUtility.doubleQuoted("brand")).append(DBConstants.SPACE).append(DBConstants.TEXT)
                .append(DBConstants.COMMA)
                .append(DBUtility.doubleQuoted("floor_travel_time_ms")).append(DBConstants.SPACE)
                .append(DBConstants.BIGINT).append(DBConstants.COMMA)
                .append(DBUtility.doubleQuoted("boarding_time_ms")).append(DBConstants.SPACE)
                .append(DBConstants.BIGINT).append(DBConstants.COMMA)
                .append(DBUtility.doubleQuoted("total_capacity_limit")).append(DBConstants.SPACE)
                .append(DBConstants.INT).append(DBConstants.COMMA)
                .append(DBConstants.PRIMARY_KEY).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.doubleQuoted("id")).append(DBConstants.CLOSED_PARENTHESIS)
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createLiftBrandsTableSQL.toString());
        }
        return true;
    }

    public static boolean insertLiftBrandsData(Connection connection) throws SQLException {
        StringBuilder insertIntoLiftBrandsSQL = new StringBuilder()
                .append(DBConstants.INSERT).append(DBConstants.SPACE)
                .append(DBConstants.INTO).append(DBConstants.SPACE).append(tableName)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append("brand").append(DBConstants.COMMA)
                .append("floor_travel_time_ms").append(DBConstants.COMMA)
                .append("boarding_time_ms").append(DBConstants.COMMA)
                .append("total_capacity_limit")
                .append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SPACE)
                .append(DBConstants.VALUES).append(DBConstants.SPACE)
                .append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.singleQuoted("Toshiba")).append(DBConstants.COMMA)
                .append("3000").append(DBConstants.COMMA).append("1000").append(DBConstants.COMMA)
                .append("4").append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.COMMA)
                .append(DBConstants.SPACE).append(DBConstants.OPEN_PARENTHESIS)
                .append(DBUtility.singleQuoted("Otis")).append(DBConstants.COMMA)
                .append("4000").append(DBConstants.COMMA).append("1000").append(DBConstants.COMMA)
                .append("6").append(DBConstants.CLOSED_PARENTHESIS).append(DBConstants.SEMICOLON);

        try (Statement statement = connection.createStatement()) {
            int res = statement.executeUpdate(insertIntoLiftBrandsSQL.toString());
            return res > 0;
        }
    }

    public static int[] getBrandIds(Connection connection) throws SQLException {
        ArrayList<Integer> res=new ArrayList<>();
        StringBuilder getAllBrandsSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(DBConstants.SPACE).append("id").append(DBConstants.SPACE)
                .append(DBConstants.FROM).append(DBConstants.SPACE).append(tableName).append(DBConstants.SEMICOLON);
        try (Statement statement = connection.createStatement();
             ResultSet rs =statement.executeQuery(getAllBrandsSQL.toString())){
            while(rs.next()){
                int brand = rs.getInt("id");
                res.add(brand);
            }
        }
        return res.stream().mapToInt(Integer::intValue).toArray();
    }

    public static String[] getAllBrands(Connection connection) throws SQLException {
        ArrayList<String> res=new ArrayList<>();
        StringBuilder getAllBrandsSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(DBConstants.SPACE).append("brand").append(DBConstants.SPACE)
                .append(DBConstants.FROM).append(DBConstants.SPACE).append(tableName).append(DBConstants.SEMICOLON);
        try (Statement statement = connection.createStatement();
             ResultSet rs =statement.executeQuery(getAllBrandsSQL.toString())){
            while(rs.next()){
                String brand = rs.getString("brand");
                res.add(brand);
            }
        }
        return res.toArray(new String[0]);
    }

    public static String getBrandById(Connection connection, int brandId) throws SQLException {
        String brand=null;
        StringBuilder getBrandByIdSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(DBConstants.SPACE).append("brand").append(DBConstants.SPACE)
                .append(DBConstants.FROM).append(DBConstants.SPACE).append(tableName).append(DBConstants.SPACE)
                .append(DBConstants.WHERE).append(DBConstants.SPACE).append("id").append(DBConstants.EQUALS).append("?")
                .append(DBConstants.SEMICOLON);

        try (PreparedStatement ps = connection.prepareStatement(getBrandByIdSQL.toString())){
            ps.setInt(1,brandId);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    brand=rs.getString("brand");
                }
            }
        }
        return brand;
    }

    public static long getFloorTravelTimeMs(Connection connection, int brandId) throws SQLException {
        long floorTravelTimeMs=-1;
        StringBuilder getBrandByIdSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(DBConstants.SPACE).append("floor_travel_time_ms")
                .append(DBConstants.SPACE).append(DBConstants.FROM).append(DBConstants.SPACE).append(tableName)
                .append(DBConstants.SPACE).append(DBConstants.WHERE).append(DBConstants.SPACE).append("id")
                .append(DBConstants.EQUALS).append("?").append(DBConstants.SEMICOLON);

        try (PreparedStatement ps = connection.prepareStatement(getBrandByIdSQL.toString())){
            ps.setInt(1,brandId);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    floorTravelTimeMs=rs.getLong("floor_travel_time_ms");
                }
            }
        }
        return floorTravelTimeMs;
    }

    public static long getBoardingTimeMs(Connection connection, int brandId) throws SQLException {
        long boardingTimeMs=-1;
        StringBuilder getBrandByIdSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(DBConstants.SPACE).append("boarding_time_ms")
                .append(DBConstants.SPACE).append(DBConstants.FROM).append(DBConstants.SPACE).append(tableName)
                .append(DBConstants.SPACE).append(DBConstants.WHERE).append(DBConstants.SPACE).append("id")
                .append(DBConstants.EQUALS).append("?").append(DBConstants.SEMICOLON);

        try (PreparedStatement ps = connection.prepareStatement(getBrandByIdSQL.toString())){
            ps.setInt(1,brandId);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    boardingTimeMs=rs.getLong("boarding_time_ms");
                }
            }
        }
        return boardingTimeMs;
    }

    public static int getTotalCapacityLimitById(Connection connection, int brandId) throws SQLException {
        int totalCapacityLimit = -1;
        StringBuilder getTotalCapacityLimitByIdSQL = new StringBuilder()
                .append(DBConstants.SELECT).append(DBConstants.SPACE).append("total_capacity_limit")
                .append(DBConstants.SPACE).append(DBConstants.FROM).append(DBConstants.SPACE).append(tableName)
                .append(DBConstants.SPACE).append(DBConstants.WHERE).append(DBConstants.SPACE).append("id")
                .append(DBConstants.EQUALS).append("?");

        try (PreparedStatement ps = connection.prepareStatement(getTotalCapacityLimitByIdSQL.toString())){
            ps.setInt(1, brandId);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    totalCapacityLimit=rs.getInt("total_capacity_limit");
                }
            }
        }
        return totalCapacityLimit;
    }

}
