/******************************************************************************************************************
* File: LoginServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the login micro services. These services run
* in their own process (JVM).
*
* Parameters: None
*
* Internal Methods:
*  String login() - check if the username and password are correct
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class LoginServices extends UnicastRemoteObject implements LoginServicesAI
{ 
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/ms_orderinfo?autoReconnect=true&useSSL=false";

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = "Renhao250"; //replace with your MySQL root password

    // Do nothing constructor
    public LoginServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry.
        try 
        { 
            LoginServices obj = new LoginServices();

            // Bind this object instance to the name RetrieveServices in the rmiregistry 
            Naming.rebind("//localhost:1099/LoginServices", obj);

        } catch (Exception e) {

            System.out.println("LoginServices binding err: " + e.getMessage());
            e.printStackTrace();
        } 

    } // main

    // This method will check the username and password
    // provided in the argument.

    public String login(String username, String password) throws RemoteException
    {
      	// Local declarations

        Connection conn = null;		// connection to the orderinfo database
        Statement stmt = null;		// A Statement object is an interface that represents a SQL statement.
        String ReturnString = null;	// Return string. If everything works you get an ordered pair of data
        							// if not you get an error string

        try
        {
            // Here we load and initialize the JDBC connector. Essentially a static class
            // that is used to provide access to the database from inside this class.

            Class.forName(JDBC_CONNECTOR);

            //Open the connection to the orderinfo database

            //System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            // Here we create the queery Execute a query. Not that the Statement class is part
            // of the Java.rmi.* package that enables you to submit SQL queries to the database
            // that we are connected to (via JDBC in this case).

            // System.out.println("Creating statement...");
            stmt = conn.createStatement();
            
            String sql;
            sql = "SELECT * FROM users where username=" + "\"" + username + "\"";
            ResultSet rs = stmt.executeQuery(sql);

            String salt = null;
            String hash = null;
            String token = null;



            // Extract data from result set. Note there should only be one for this method.
            // I used a while loop should there every be a case where there might be multiple
            // orders for a single ID.

            if (rs.next())
            {
                //Retrieve by column name
                salt = rs.getString("salt");
                hash = rs.getString("password");
                token = rs.getString("token");

                if ( hash.equals(calculateHashForPassword(password, salt)) )
                {
                    ReturnString = "Result: Success, token:" + token + "@";
                }
                else
                {
                    ReturnString = "Result: Fail, Reason: Username or Password incorrect!";
                }
            }
            else
            {
                ReturnString = "Result: Fail, Reason: Username or Password incorrect!";
            }

            //Clean-up environment

            rs.close();
            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();

        } catch(Exception e) {

            ReturnString = e.toString();

        } 

        return(ReturnString);

    } //login

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

} // LoginServices