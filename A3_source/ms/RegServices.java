/******************************************************************************************************************
* File: RegServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the create user. These services run
* in their own process (JVM).
*
* Parameters: None
*
* Internal Methods:
*  String newUser() - creates an user in the ms_orderinfo database from the supplied parameters.
*
* External Dependencies: 
*	- rmiregistry must be running to start this server
*	= MySQL
	- orderinfo database 
******************************************************************************************************************/
import java.rmi.Naming; 
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Base64;

public class RegServices extends UnicastRemoteObject implements RegServicesAI
{ 
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/ms_orderinfo?autoReconnect=true&useSSL=false";

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = "Renhao250"; //replace with your MySQL root password

    // Do nothing constructor
    public RegServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry. 

        try 
        { 
            RegServices obj = new RegServices();

            // Bind this object instance to the name RetrieveServices in the rmiregistry 
            Naming.rebind("//localhost:1099/RegServices", obj);

        } catch (Exception e) {

            System.out.println("RegServices binding err: " + e.getMessage());
            e.printStackTrace();
        } 

    } // main


    // Inplmentation of the abstract classes in RegServicesAI happens here.

    // This method add the entry into the ms_orderinfo database

    public String newUser(String username, String password) throws RemoteException
    {
      	// Local declarations

        Connection conn = null;		                 // connection to the orderinfo database
        Statement stmt = null;		                 // A Statement object is an interface that represents a SQL statement.
        String ReturnString = "User Created";	     // Return string. If everything works you get an 'OK' message
        							                 // if not you get an error string
        try
        {
            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.

            Class.forName(JDBC_CONNECTOR);

            //Open the connection to the orderinfo database

            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            String salt = calculateSalt();
            String hash = calculateHashForPassword(password, salt);
            String token = calculateToken();

            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).

            stmt = conn.createStatement();

            String sql;
            sql = "SELECT * FROM users where username=" + "\"" + username + "\"";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next())
            {
                ReturnString = "Register failed, the username has already exist";
            }
            else
            {
                sql = "INSERT INTO users(username, salt, password, token) VALUES (\""+username+"\",\""+salt+"\",\""+hash+"\",\""+token+"\")";
                // execute the update

                stmt.executeUpdate(sql);
            }


            // clean up the environment
            rs.close();
            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();

        } catch(Exception e) {

            ReturnString = e.toString();
        } 
        
        return(ReturnString);

    } //reg user

    /**
     * calculate the salt for encrypt the password
     * @return salt
     */
    private String calculateSalt() {
        Random RANDOM = new SecureRandom();
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        String str = new String(Base64.getEncoder().encodeToString(salt));
        System.out.println(str);
        if (str.length() > 16) return str.substring(0, 16);
        return str;
    }

    /**
     * calculate token for user login
     * @return
     */
    private String calculateToken() {
        Random RANDOM = new SecureRandom();
        byte[] token = new byte[16];
        RANDOM.nextBytes(token);
        String str = new String(Base64.getEncoder().encodeToString(token));
        System.out.println(str);
        if (str.length() > 16) return str.substring(0, 16);
        return str;
    }

    /**
     * calculate the encrypt password
     * @param password original password
     * @param salt the salt
     * @return the hash value for password
     */
    public String calculateHashForPassword(String password, String salt) {
        String hash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            hash = sb.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return hash;
    }

} // RegServices