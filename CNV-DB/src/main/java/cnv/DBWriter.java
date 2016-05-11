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
        if(prop == null){
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
            prop = mainProperties;
        }
        return prop;
    }

    private static Properties prop = null;

    private static Connection connection = null;

    public static Connection getConnection() throws IOException {
        if(connection == null){
            getProperties();
            String confUrl = prop.getProperty("url");
            String dbName = prop.getProperty("dbName");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");
            String url = "jdbc:mysql://" + confUrl + "/" + dbName;
            try {
                connection = DriverManager.getConnection(url, username, password);            
            } catch (SQLException e) {
                throw new IOException("Cannot connect the database!", e);
            }
        }
        return connection;        
    }

    public static synchronized void write(String input, String bb) throws IOException {
        getConnection();
        try {           
            Statement stmt = connection.createStatement();
            stmt.execute(String.format("insert into metrics values (%s,%s)",input, bb));
            stmt.close();
        } catch(SQLIntegrityConstraintViolationException e){
            // Insert same value twice exception, no worries
            //System.out.println("already in whats the worry?");
        }catch (SQLException e) {
                throw new IOException("Cannot insert into database!", e);
        }
    }

    public static void main(String... args) throws IOException {
        if(args.length != 2){
            System.out.println("Need two parameters");
            System.exit(0);
        }
        String input = ""+BigInteger.valueOf(Long.parseLong(args[0]));
        String bb = ""+BigInteger.valueOf(Long.parseLong(args[1]));
        System.out.println("input:"+input+" bb:"+bb);
        write(input,bb);
    }
}
