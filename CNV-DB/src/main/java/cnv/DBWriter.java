package cnv;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.sql.*;
import java.util.Properties;

/**
 * Created by rui on 07-05-2016.
 */
public class DBWriter {
    public static Properties getProperties() throws IOException {

        String versionString = null;

        //to load application's properties, we use this class
        Properties mainProperties = new Properties();

        FileInputStream file;

        //the base folder is ./, the root of the main.properties file
        String path = "./mysql.config";

        //load the file handle for main.properties
        file = new FileInputStream(path);

        //load all the properties from this file
        mainProperties.load(file);

        //we have loaded the properties, so close the file handle
        file.close();

        //retrieve the property we are intrested, the app.version
        return mainProperties;
    }

    private static Properties prop = null;

    public static synchronized void write(String input, String bb) throws IOException {
        if(prop == null){
            prop = getProperties();
        }
        String confUrl = prop.getProperty("url");
        String dbName = prop.getProperty("dbName");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        String url = "jdbc:mysql://" + confUrl + "/" + dbName;

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement stmt = connection.createStatement();
            stmt.execute(String.format("insert into metrics values (%s,%s)",input, bb));
            stmt.close();
        } catch(SQLIntegrityConstraintViolationException e){
            // Insert same value twice exception, no worries
            //System.out.println("already in whats the worry?");
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public static void main(String... args) throws IOException {
        if(args.length != 2){
            System.out.println("Need two parameters");
            System.exit(0);
        }
        write(""+BigInteger.valueOf(Long.parseLong(args[0])),
                ""+BigInteger.valueOf(Long.parseLong(args[1])));
    }
}
