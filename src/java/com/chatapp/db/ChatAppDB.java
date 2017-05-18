package com.chatapp.db;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *  ChatApp DB Interface class
 * @author MaTaN
 */
public class ChatAppDB {
    
    /* 
    * DB Information - Keep Secrative.
    */
    final static String DB_URL = "app9443.database.windows.net:1433";
    final static String DB_NAME = "chatapp";
    final static String DB_USER = "chatapp@app9443";
    final static String DB_PASS = "dov#2017";
    
    public static Connection getConnection(){
        Connection connection = null;
        try {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                } 
            catch (ClassNotFoundException e) {
                e.printStackTrace();
                } 
            String _conn = String.format("jdbc:sqlserver://%s;database=%s;user=%s;password=%s", DB_URL, DB_NAME, DB_USER, DB_PASS);
            connection = DriverManager.getConnection(_conn);
            return connection;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}