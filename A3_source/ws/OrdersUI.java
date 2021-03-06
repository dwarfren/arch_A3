/******************************************************************************************************************
* File:OrdersUI.java
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*	1.0 February 2018 - Initial write of assignment 3 (ajl).
*
* Description: This class is the console for the an orders database. This interface uses a webservices or microservice
* client class to update the orderinfo MySQL database. 
*
* Parameters: None
*
* Internal Methods: None
*
* External Dependencies (one of the following):
*	- RESTClientAPI - this class provides a restful interface to a node.js webserver (see Server.js and REST.js).
*	- ms_client - this class provides access to micro services vis-a-vis remote method invocation
*
******************************************************************************************************************/

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.Console;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrdersUI
{
	public static void main(String args[])
	{
		boolean done = false;						// main loop flag
		boolean error = false;						// error flag
		String    option;							// Menu choice from user
		Console c = System.console();				// Press any key
		String  date = null;						// order date
		String  first = null;						// customer first name
		String  last = null;						// customer last name
		String  address = null;						// customer address
		String  phone = null;						// customer phone number
		String  orderid = null;						// order ID
		String 	response = null;					// response string from REST 
		Scanner keyboard = new Scanner(System.in);	// keyboard scanner object for user input
		DateTimeFormatter dtf = null;				// Date object formatter
		LocalDate localDate = null;					// Date object
		WSClientAPI api = new WSClientAPI();	    // RESTful api object


		String username = null;                     // user name for login
		String token = null;                        // token for authorize

		/////////////////////////////////////////////////////////////////////////////////
		// Main UI loop
		/////////////////////////////////////////////////////////////////////////////////

		while (!done)
		{	
			// Here, is the main menu set of choices

			System.out.println( "\n\n\n\n" );
			System.out.println( "Orders Database User Interface: \n" );
			System.out.println( "Select an Option: \n" );
			System.out.println( "1: Login." );
			System.out.println( "2: Register." );
			System.out.println( "3: Retrieve all orders in the order database." );
			System.out.println( "4: Retrieve an order by ID." );
			System.out.println( "5: Add a new order to the order database." );
			System.out.println( "6: Delete an order from the order database." );				
			System.out.println( "X: Exit\n" );
			System.out.print( "\n>>>> " );
			option = keyboard.nextLine();	
			//keyboard.nextLine();	// Removes data from keyboard buffer. If you don't clear the buffer, you blow 
									// through the next call to nextLine()

			//////////// option 1 ////////////

			//option 1 for login
			if ( option.equals("1") )
			{
				System.out.print( "Enter the username: " );
				String login_username = keyboard.nextLine(); // enter user name

				System.out.print( "Enter the password: " );
				String login_password = keyboard.nextLine(); // enter password

				try
				{
					response = api.login(login_username, login_password);
					System.out.println(response);

					//if login success, token will be returned
					String pattern = "\"token\":\"(.*)@\"";
					Pattern r = Pattern.compile(pattern);
					Matcher m = r.matcher(response);
					//assign the token and username
					if (m.find()) {
						username = login_username;
						token = m.group(1);
					}

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();
			}

			//////////// option 2 ////////////

			//option 2 for register
			else if ( option.equals("2") )
			{
				System.out.println( "\nRegister - Provide the username and password::" );
				String reg_username = null;
				String reg_password1 = null;
				String reg_password2 = null;

				error = false;
				while (!error) 
				{
					System.out.print( "Enter the username: " );
					reg_username = keyboard.nextLine(); // enter user name
					if (reg_username.length() > 256) // length of the username should be <= 256
					{
						System.out.print( "The length of the username should be no more than 256, please input again!\n" );
					}
					else
					{
						error = true;
					}
				}
				

				error = false;
				while (!error)
				{
					//enter password twice
					System.out.print( "Enter the password: " );
					reg_password1 = keyboard.nextLine();
					System.out.print( "Enter the password again: " );
					reg_password2 = keyboard.nextLine();

					if (reg_password1.equals(reg_password2))// two input should be the same
					{
						error = true;
					}
					else
					{
						System.out.print( "\nThe two passwords are NOT the same, please enter the password again!" );
					}
				}

				try
				{
					response = api.createUser(reg_username, reg_password1);
					System.out.println(response);

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);
					
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();
			}

			//////////// option 3 ////////////

			//option 3 for retrieve orders

			else if ( option.equals("3") )
			{
				//user should login first
				if (token == null || username == null)
				{
					System.out.println( "\nPlease login first!" );
					continue;
				}
				// Here we retrieve all the orders in the order database

				System.out.println( "\nRetrieving All Orders::" );
				try
				{
					response = api.retrieveOrders(username, token);
					System.out.println(response);

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);

				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

			} // if

			//////////// option 4 ////////////

			//option 4 for retrieve specific order
			else if ( option.equals("4") )
			{
				if (token == null)
				{
					System.out.println( "\nPlease login first!" );
					continue;
				}
				// Here we get the order ID from the user

				error = true;

				while (error)
				{
					System.out.print( "\nEnter the order ID: " );
					orderid = keyboard.nextLine();

					try
					{
						Integer.parseInt(orderid);
						error = false;
					} catch (NumberFormatException e) {

						System.out.println( "Not a number, please try again..." );
						System.out.println("\nPress enter to continue..." );

					} // if

				} // while

				try
				{
					response = api.retrieveOrders(orderid, username, token);
					System.out.println(response);

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);
					
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

			} // if

			//////////// option 5 ////////////

			//option 5 create a new order
			else if ( option.equals("5") )
			{
				if (token == null)
				{
					System.out.println( "\nPlease login first!" );
					continue;
				}
				// Here we create a new order entry in the database

				dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				localDate = LocalDate.now();
				date = localDate.format(dtf);

				System.out.println("Enter first name:");
				first = keyboard.nextLine();

				System.out.println("Enter last name:");
				last = keyboard.nextLine();
		
				System.out.println("Enter address:");
				address = keyboard.nextLine();

				System.out.println("Enter phone:");
				phone = keyboard.nextLine();

				System.out.println("Creating the following order:");
				System.out.println("==============================");
				System.out.println(" Date:" + date);		
				System.out.println(" First name:" + first);
				System.out.println(" Last name:" + last);
				System.out.println(" Address:" + address);
				System.out.println(" Phone:" + phone);
				System.out.println("==============================");					
				System.out.println("\nPress 'y' to create this order:");

				option = keyboard.nextLine();

				if ( (option.equals("y")) || (option.equals("Y")) )
				{
					try
					{
						System.out.println("\nCreating order...");
						response = api.newOrder(date, first, last, address, phone, username, token);
						System.out.println(response);

					} catch(Exception e) {

						System.out.println("Request failed:: " + e);

					}

				} else {

					System.out.println("\nOrder not created...");
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();

				//option = " "; //Clearing option. This incase the user enterd X/x the program will not exit.

			} // if

			//////////// option 6 ////////////

			//option 6 delete an order
			else if ( option.equals("6") )
			{
				if (token == null)
				{
					System.out.println( "\nPlease login first!" );
					continue;
				}

				error = true;

				while (error)
				{
					System.out.print( "\nEnter the order ID: " );
					orderid = keyboard.nextLine();

					try
					{
						Integer.parseInt(orderid);
						error = false;
					} catch (NumberFormatException e) {

						System.out.println( "Not a number, please try again..." );
						System.out.println("\nPress enter to continue..." );

					} // if

				} // while

				try
				{
					response = api.deleteOrders(orderid, username, token);
					System.out.println(response);

				} catch (Exception e) {

					System.out.println("Request failed:: " + e);
					
				}

				System.out.println("\nPress enter to continue..." );
				c.readLine();
			}

			//////////// option X ////////////

			else if ( ( option.equals("X")) || ( option.equals("x")) )
			{
				// Here the user is done, so we set the Done flag and halt the system

				done = true;
				System.out.println( "\nDone...\n\n" );

			}

			// no such option
			else
			{
				System.out.println("\nNo such option!" );
				System.out.println("Press enter to continue..." );
				c.readLine();
			}

		} // while

  	} // main

} // OrdersUI
