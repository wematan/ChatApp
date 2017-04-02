package com.chatapp.signup;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;

import com.chatapp.JsonResponse.JsonResponse;
import com.chatapp.user.User;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


/**
 * REST Web Service
 *
 * @author MaTaN
 */
@Path("SignUp")
public class SignUpResource {
    
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of SignUpResource
     */
    public SignUpResource() {
    }

    /**
     * Retrieves representation of an instance of com.chatapp.signup.SignUpResource
     * @return an instance of java.lang.String
     */
    @PUT
    @Path("registerUser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String signup(String inputJson) {
        User user = new Gson().fromJson(inputJson, User.class) ;
        
        JsonResponse returnMsg = new JsonResponse();
        if(checkUserExist(user) == true) {
            returnMsg.setStatus(1);
            returnMsg.setMessage("User Already Exists.");
            return new Gson().toJson(returnMsg);
        }
        else {
            boolean success = registerUser(user);
            if (success == true){
                returnMsg.setStatus(0);
                returnMsg.setMessage("User Signup Successful.");
            }
            else {
                   
            }
        }
        
        return new Gson().toJson(returnMsg);
    }
    
    public boolean checkUserExist(User user){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                        "jdbc:sqlserver://app9443.database.windows.net:1433;database=chatapp;user=chatapp@app9443;password=dov#2017");

            String id = user.getId();
            PreparedStatement fetchId = connection.prepareStatement(
                        "SELECT * FROM chatapp.USERS\n" +
                        "WHERE ID=?;"
                        );
            fetchId.setInt(1, Integer.parseInt(id));
            ResultSet result = fetchId.executeQuery();
            while(result.next()) {
                return true;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try
            {
                connection.close();
            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public boolean registerUser(User user){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                        "jdbc:sqlserver://app9443.database.windows.net:1433;database=chatapp;user=chatapp@app9443;password=dov#2017");

            PreparedStatement createUser = connection.prepareStatement(
                        "INSERT INTO chatapp.USERS (ID, \"NAME\", EMAIL, PHONE)\n" +
                        "VALUES (?,?,?,?);"
                        );

            createUser.setInt(1, Integer.parseInt(user.getId()));
            createUser.setString(2, user.getName());
            createUser.setString(3, user.getEmail());
            createUser.setString(4, user.getPhone());
            createUser.execute();
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try
            {
                connection.close();
            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }
        }
        return false;
    }
}

