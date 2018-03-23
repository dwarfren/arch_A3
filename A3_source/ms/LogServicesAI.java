/******************************************************************************************************************
* File: DeleteServicesAI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the abstract interface for the log micro service, LogServices.
* The implementation of these abstract interfaces can be found in the LogServices.java class.
* The micro services are partitioned as Create, Retrieve, Update, Delete (CRUD) service packages. Each service 
* is its own process (eg. executing in a separate JVM). It would be a good practice to follow this convention
* when adding and modifying services. Note that services can be duplicated and differentiated by IP
* and/or port# they are hosted on. For this assignment, create and retrieve services have been provided and are
* services are hosted on the local host, on the default RMI port (1099). 
*
* Parameters: None
*
* Internal Methods:
*  String writeLog(String content) - write log content into file
*
* External Dependencies: None
******************************************************************************************************************/

import java.rmi.*;
		
public interface LogServicesAI extends java.rmi.Remote
{
	/*******************************************************
	* Delete the order corresponding to the order id in
	* method argument form the orderinfo database and 
	* returns the result of deleting
	*******************************************************/	

	void writeLog(String content ) throws RemoteException;
}