/******************************************************************************************************************
* File: DeleteServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the delete micro services. These services run
* in their own process (JVM).
*
* Parameters: None
*
* Internal Methods:
*  String deleteOrders(String id) - delete and returns the order associated with the order id
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

public class DeleteServices extends UnicastRemoteObject implements DeleteServicesAI
{ 
    // Set up the JDBC driver name and database URL
    static final String JDBC_CONNECTOR = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://18.217.158.102/ms_orderinfo?autoReconnect=true&useSSL=false";

    // Set up the orderinfo database credentials
    static final String USER = "root";
    static final String PASS = "Renhao250"; //replace with your MySQL root password

    // Do nothing constructor
    public DeleteServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry.
        try 
        {
            DeleteServices obj = new DeleteServices();

            // Bind this object instance to the name RetrieveServices in the rmiregistry 
            Naming.rebind("//localhost:1099/DeleteServices", obj);

        } catch (Exception e) {

            System.out.println("DeleteServices binding err: " + e.getMessage());
            e.printStackTrace();
        } 

    } // main




    // This method will delete the order in the orderinfo database corresponding to the id
    // provided in the argument.

    public String deleteOrders(String orderid, String username, String token) throws RemoteException
    {
      	// Local declarations

        Connection conn = null;		// connection to the orderinfo database
        Statement stmt = null;		// A Statement object is an interface that represents a SQL statement.
        String ReturnString = "Order deleted";	// Return string. If everything works you get an ordered pair of data
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
            ResultSet rs_login = stmt.executeQuery(sql);

            // if the user is authorized
            if (rs_login.next() && token.equals(rs_login.getString("token")) )
            {
                sql = "DELETE FROM orders where order_id=" + orderid;
                if (stmt.executeUpdate(sql) == 0)
                {
                    ReturnString = "Order not exist!";
                }
            }
            else
            {
                ReturnString = "Result: Fail, Reason: user not authorized";
            }

            //Clean-up environment
            rs_login.close();
            stmt.close();
            conn.close();
            stmt.close(); 
            conn.close();

        } catch(Exception e) {

            ReturnString = e.toString();

        } 

        return(ReturnString);

    } //delete order by id

} // DeleteServices