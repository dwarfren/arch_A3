
/******************************************************************************************************************
* File:REST.js
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*   1.0 February 2018 - Initial write of assignment 3 for 2018 architectures course(ajl).
*
* Description: This module provides the restful webservices for the Server.js Node server. This module contains GET,
* and POST services.  
*
* Parameters: 
*   router - this is the URL from the client
*   connection - this is the connection to the database
*   md5 - This is the md5 hashing/parser... included by convention, but not really used 
*
* Internal Methods: 
*   router.get("/"... - returns the system version information
*   router.get("/orders"... - returns a listing of everything in the ws_orderinfo database
*   router.get("/orders/:order_id"... - returns the data associated with order_id
*   router.post("/order?"... - adds the new customer data into the ws_orderinfo database
*
* External Dependencies: mysql
*
******************************************************************************************************************/

var mysql   = require("mysql");     //Database
var fs = require("fs");             // file system
var user = "";                      // user name
var crypto = require('crypto');

// calculateSaltAndHashForPassword - calculate the salt and the hash for user's password
// password paramdter is the user's password
// return the salt and the hash

function calculateSaltAndHashForPassword(password) {
    var salt =  crypto.randomBytes(8).toString('hex').slice(0, 16); // generate random salt
    var hash = crypto.createHmac('sha512', salt); /** Hashing algorithm sha512 */
    hash.update(password);
    var value = hash.digest('hex');
    return {
        salt:salt,
        hashValue:value
    };
}

// write log - write the user operation into log file
// content paramdter is the log text

function writeLog(content) {
    fs.open('log.txt', 'a', function(err, fd) { //open log file
        if (err) {
            return console.error(err); // output error
        }

        var myDate = new Date();
        var datetime = myDate.toLocaleString(); //get date and time

        //write log file
        fs.appendFile('log.txt', datetime + " " + content + '\n',  function(err) {
            if (err) {
                return console.error(err);
            }
        });

        // close file
        fs.close(fd, function(err){
            if (err){
                console.log(err);
            }
        });
    });
}

function REST_ROUTER(router,connection) {
    var self = this;
    self.handleRoutes(router,connection);
}

// Here is where we define the routes. Essentially a route is a path taken through the code dependent upon the 
// contents of the URL

REST_ROUTER.prototype.handleRoutes= function(router,connection) {

    // GET with no specifier - returns system version information
    // req paramdter is the request object
    // res parameter is the response object

    router.get("/",function(req,res){
        res.json({"Message":"Orders Webservices Server Version 1.0"});
    });
    
    // GET for /orders specifier - returns all orders currently stored in the database
    // req paramdter is the request object
    // res parameter is the response object
  
    router.get("/orders",function(req,res){
        console.log("Getting all database entries..." );
        var query = "SELECT * FROM ??";
        var table = ["orders"];
        var log_message = "";

        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                log_message =  "Error executing MySQL query";
                res.json({"Error" : true, "Message" : log_message});
            } else {
                log_message =  "Success";
                res.json({"Error" : false, "Message" : log_message, "Orders" : rows});
            }
            writeLog("operation: " + query + " result: " + log_message);
        });

    });

    // GET for /orders/order id specifier - returns the order for the provided order ID
    // req paramdter is the request object
    // res parameter is the response object
     
    router.get("/orders/:order_id",function(req,res){
        console.log("Getting order ID: ", req.params.order_id );
        var query = "SELECT * FROM ?? WHERE ??=?";
        var table = ["orders","order_id",req.params.order_id];
        var log_message = "";

        query = mysql.format(query,table);
        console.log("query: ", query );
        connection.query(query,function(err,rows){
            if(err) {
                log_message +=  "Error executing MySQL query";
                res.json({"Error" : true, "Message" : log_message});
            } else {
                log_message +=  "Success";
                res.json({"Error" : false, "Message" : log_message, "Users" : rows});
            }
            writeLog("operation: " + query + " result: " + log_message);
        });

        writeLog(query + " " + log_message);
    });

    // DELETE for /orders/order id specifier - delete the order for the provided order ID
    // req paramdter is the request object
    // res parameter is the response object

    router.delete("/orders/:order_id",function(req,res){
        console.log("Deleting order ID: ", req.params.order_id );
        var query = "DELETE FROM ?? WHERE ??=?";
        var table = ["orders","order_id",req.params.order_id];
        var log_message = "";

        query = mysql.format(query,table);
        console.log("query: ", query );
        connection.query(query,function(err,rows){
            if(err) {
                log_message +=  "Error executing MySQL query";
                res.json({"Error" : true, "Message" : log_message});

            } else {
                log_message +=  "Success";
                res.json({"Error" : false, "Message" : log_message, "Users" : rows});
            }
            writeLog("operation: " + query + " result: " + log_message);
        });

        writeLog(query + " " + log_message);
    });

    // POST for /orders?order_date&first_name&last_name&address&phone - adds order
    // req paramdter is the request object - note to get parameters (eg. stuff afer the '?') you must use req.body.param
    // res parameter is the response object 
  
    router.post("/orders",function(req,res){
        //console.log("url:", req.url);
        //console.log("body:", req.body);
        console.log("Adding to orders table ", req.body.order_date,",",req.body.first_name,",",req.body.last_name,",",req.body.address,",",req.body.phone);
        var query = "INSERT INTO ??(??,??,??,??,??) VALUES (?,?,?,?,?)";
        var table = ["orders","order_date","first_name","last_name","address","phone",req.body.order_date,req.body.first_name,req.body.last_name,req.body.address,req.body.phone];
        var log_message = "";

        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                log_message =  "Error executing MySQL query";
                res.json({"Error" : true, "Message" : log_message});

            } else {
                log_message =  "User Added !";
                res.json({"Error" : false, "Message" : log_message});

            }
            writeLog("operation: " + query + " result: " + log_message);
        });

        writeLog(query + " " + log_message);
    });

    // POST for /orders?username&password for creating new user
    // req paramdter is the request object - note to get parameters (eg. stuff afer the '?') you must use req.body.param
    // res parameter is the response object

    router.post("/register",function(req,res){
        //console.log("url:", req.url);
        //console.log("body:", req.body);
        console.log("Adding to users table ", req.body.username,",",req.body.password);
        var password = calculateSaltAndHashForPassword(req.body.password)
        var query = "INSERT INTO ??(??,??,??) VALUES (?,?,?)";
        var table = ["users","username","salt","password", req.body.username, password.salt, password.hashValue];
        var log_message = "";

        query = mysql.format(query,table);
        connection.query(query,function(err,rows){
            if(err) {
                log_message =  "Error executing MySQL, user already register";
                res.json({"Error" : true, "Message" : log_message, "Users" : rows});

            } else {
                log_message =  "User Added !";
                res.json({"Error" : false, "Message" : log_message, "Users" : rows});

            }
            writeLog("operation: " + query + " result: " + log_message);
        });

        writeLog(query + " " + log_message);
    });

}

// The next line just makes this module available... think of it as a kind package statement in Java

module.exports = REST_ROUTER;