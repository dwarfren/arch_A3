/******************************************************************************************************************
* File: DeleteServicesAI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the abstract interface for the delete micro service, DeleteServices.
* The implementation of these abstract interfaces can be found in the DeleteServices.java class.
* The micro services are partitioned as Create, Retrieve, Update, Delete (CRUD) service packages. Each service 
* is its own process (eg. executing in a separate JVM). It would be a good practice to follow this convention
* when adding and modifying services. Note that services can be duplicated and differentiated by IP
* and/or port# they are hosted on. For this assignment, create and retrieve services have been provided and are
* services are hosted on the local host, on the default RMI port (1099). 
*
* Parameters: None
*
* Internal Methods:
*  String deleteOrders(String id) - delete the order record by order id
*
* External Dependencies: None
******************************************************************************************************************/

import java.rmi.*;
		
public interface DeleteServicesAI extends java.rmi.Remote
{
	/*******************************************************
	* Delete the order corresponding to the order id in
	* method argument form the orderinfo database and 
	* returns the result of deleting
	*******************************************************/	

	String deleteOrders(String id, String username, String token ) throws RemoteException;
}