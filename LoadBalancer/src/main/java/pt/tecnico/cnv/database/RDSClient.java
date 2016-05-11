package pt.tecnico.cnv.database;

import com.mysql.jdbc.Driver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class RDSClient {

    public static final String TABLE = "metrics";
    public static final String NUM_COL = "num";
    public static final String BB_COL = "bbs";

    Connection connection = null;
    private static RDSClient INSTANCE = null;

    public RDSClient() {
        this("rds.properties");
    }

    public RDSClient(String path) {
        final Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String url = properties.getProperty("url");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        String dbName = properties.getProperty("dbName");
        this.init(url, username, password, dbName);
    }

    public RDSClient(String url, String username, String password, String dbName) {
        this.init(url, username, password, dbName);
    }

    public static RDSClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RDSClient();
        }
        return INSTANCE;
    }

    private void init(String url, String username, String password, String dbName) {
        System.out.print("[RDS Client] Registering MySQL Connector... ");
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Done!");
        System.out.println("[RDS Client] Database: " + url + "/" + dbName);
        System.out.println("[RDS Client] Username: " + username);
        System.out.println("[RDS Client] Password: " + password.replaceAll(".", "*"));
        System.out.print("[RDS Client] Connecting to database... ");
        try {
            this.connection = DriverManager.getConnection(url + "/" + dbName, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Done!");
    }

    public ResultSet queryNumbersNextTo(BigInteger number) {
        final String bigInt = number.toString();
        final String sqlQueryGE = "SELECT * FROM "+ TABLE +" WHERE "+NUM_COL+" >= " + bigInt + " ORDER BY "+NUM_COL+" ASC LIMIT 3";
        final String sqlQueryLE = "SELECT * FROM "+ TABLE +" WHERE "+NUM_COL+" <= " + bigInt + " ORDER BY "+NUM_COL+" DESC LIMIT 3";
        final String sqlQuery = "(" + sqlQueryGE + ") UNION (" + sqlQueryLE + ")";
        try {
            return this.connection.createStatement().executeQuery(sqlQuery);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
