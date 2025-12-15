package utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class JDBCUtil {

    private static HikariDataSource dataSource;

    // 懶加載 DataSource
    private static HikariDataSource getDataSource() {
        if (dataSource == null) {
            try {
                HikariConfig config = new HikariConfig();
                config.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver"); // 明確指定 driver
                config.setJdbcUrl("jdbc:sqlserver://localhost:1433;DatabaseName=petDB;encrypt=false");
                config.setUsername("seren");
                config.setPassword("1234");
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                dataSource = new HikariDataSource(config);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize HikariCP DataSource", e);
            }
        }
        return dataSource;
    }

    // 取得連線
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    // 關閉單一連線
    public static void closeResource(Connection connection) {
        if (connection != null) try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    // 關閉連線與 Statement
    public static void closeResource(Connection connection, Statement statement) {
        if (statement != null) try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
        if (connection != null) try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    // 關閉連線、Statement 與 ResultSet
    public static void closeResource(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) try { resultSet.close(); } catch (SQLException e) { e.printStackTrace(); }
        if (statement != null) try { statement.close(); } catch (SQLException e) { e.printStackTrace(); }
        if (connection != null) try { connection.close(); } catch (SQLException e) { e.printStackTrace(); }
    }

    // 關閉整個 DataSource
    public static void closeDataSource() {
        if (dataSource != null) dataSource.close();
    }
}
