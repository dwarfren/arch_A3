/******************************************************************************************************************
* File: MSClientAPI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides access to the webservices via RMI. Users of this class need not worry about the
* details of RMI (provided the services are running and registered via rmiregistry).  
*
* Parameters: None
*
* Internal Methods:
*  String retrieveOrders() - gets and returns all the orders in the orderinfo database
*  String retrieveOrders(String id) - gets and returns the order associated with the order id
*  String newOrder(String Date, String FirstName, String LastName, String Address, String Phone) - creates a new 
*  order in the orderinfo database
* String deleteOrders(String id, String username, String token) - delete order record by order id
* String newUser(String username, String password) - register new user
 * String login(String username, String password) - user login
 * void writeLog(String content) - write log into file
* External Dependencies: None
******************************************************************************************************************/

import java.rmi.Naming; 
import java.rmi.RemoteException;

public class MSClientAPI
{
	String response = null;

	/********************************************************************************
	* Description: Retrieves all the orders in the orderinfo database. Note 
	*              that this method is serviced by the RetrieveServices server 
	*			   process.
	* Parameters: None
	* Returns: String of all the current orders in the orderinfo database
	********************************************************************************/

	public String retrieveOrders(String username, String token) throws Exception
	{
	   RetrieveServicesAI obj = (RetrieveServicesAI) Naming.lookup("RetrieveServices");
	   response = obj.retrieveOrders(username, token);
	   return(response);
	}
	
	/********************************************************************************
	* Description: Retrieves the order based on the id argument provided from the
	*              orderinfo database. Note that this method is serviced by the 
	*			   RetrieveServices server process.
	* Parameters: None
	* Returns: String of all the order corresponding to the order id argument 
	*          in the orderinfo database.
	********************************************************************************/

	public String retrieveOrders(String id, String username, String token) throws Exception
	{
           RetrieveServicesAI obj = (RetrieveServicesAI) Naming.lookup("RetrieveServices");  
           response = obj.retrieveOrders(id, username, token);
           return(response);	

	}

	/********************************************************************************
	* Description: Creates the new order to the orderinfo database
	* Parameters: None
	* Returns: String that contains the status of the create operatation
	********************************************************************************/

   	public String newOrder(String Date, String FirstName, String LastName, String Address, String Phone, String username, String token) throws Exception
	{
           CreateServicesAI obj = (CreateServicesAI) Naming.lookup("CreateServices"); 
           response = obj.newOrder(Date, FirstName, LastName, Address, Phone, username, token);
           return(response);	
		
    }

    /********************************************************************************
     * Description: delete the order based on the id argument provided from the
     *              orderinfo database. Note that this method is serviced by the
     *			   DeleteService server process.
     * Parameters: None
     * Returns: String of all the order corresponding to the order id argument
     *          in the orderinfo database.
     ********************************************************************************/

    public String deleteOrders(String id, String username, String token) throws Exception
    {
        DeleteServicesAI obj = (DeleteServicesAI) Naming.lookup("DeleteServices");
        response = obj.deleteOrders(id, username, token);
        return(response);

    }

    /********************************************************************************
     * Description: create a new user. Note that this method is serviced by the
     *			   RegService server process.
     * Parameters: None
     * Returns: the result of creating the user
     ********************************************************************************/

    public String newUser(String username, String password) throws Exception
    {
        RegServicesAI obj = (RegServicesAI) Naming.lookup("RegServices");
        response = obj.newUser(username, password);
        return(response);

    }

    /********************************************************************************
     * Description: login. Note that this method is serviced by the
     *			   RegService server process.
     * Parameters: None
     * Returns: the result of login
     ********************************************************************************/

    public String login(String username, String password) throws Exception
    {
        LoginServicesAI obj = (LoginServicesAI) Naming.lookup("LoginServices");
        response = obj.login(username, password);
        return(response);

    }

    /********************************************************************************
     * Description: write lig
     * Parameters: log content
     * Returns: None
     ********************************************************************************/

    public void writeLog(String content) throws Exception
    {
        LogServicesAI obj = (LogServicesAI) Naming.lookup("LogServices");
        obj.writeLog(content);

    }


}