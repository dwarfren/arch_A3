/******************************************************************************************************************
* File: LogServices.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class provides the concrete implementation of the write log micro services. These services run
* in their own process (JVM).
*
* Parameters: None
*
* Internal Methods:
*  void write(String content) - write log to the file
*
* External Dependencies: 
*	- rmiregistry must be running to start this server

******************************************************************************************************************/
import java.rmi.Naming; 
import java.rmi.RemoteException; 
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Date;
import java.text.SimpleDateFormat;

public class LogServices extends UnicastRemoteObject implements LogServicesAI
{
    // Do nothing constructor
    public LogServices() throws RemoteException {}

    // Main service loop
    public static void main(String args[]) 
    { 	
    	// What we do is bind to rmiregistry, in this case localhost, port 1099. This is the default
    	// RMI port. Note that I use rebind rather than bind. This is better as it lets you start
    	// and restart without having to shut down the rmiregistry.
        try 
        {
            LogServices obj = new LogServices();

            // Bind this object instance to the name RetrieveServices in the rmiregistry 
            Naming.rebind("//localhost:1099/LogServices", obj);

        } catch (Exception e) {

            System.out.println("LogServices binding err: " + e.getMessage());
            e.printStackTrace();
        } 

    } // main




    // This method will write log to file
    // provided in the log content

    public void writeLog(String content) throws RemoteException
    {
        File file = new File("log.txt");
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {

            FileWriter writer = new FileWriter("log.txt", true);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            content = df.format(System.currentTimeMillis()) + " " + content;
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    } //writeLog

} // LogServices