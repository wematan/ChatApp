/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chatapp.messaging;

import com.chatapp.JsonResponse.JsonResponse;
import com.chatapp.user.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author MaTaN
 */
@Path("messaging")
public class MessagingResource {

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of MessagingResource
     */
    public MessagingResource() {
    }

    /**
     * Retrieves representation of an instance of com.chatapp.messaging.MessagingResource
     * @return an instance of java.lang.String
     */
    @PUT
    @Path("validateAppUser")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String validateAppUser(String inputJson) {
        JsonObject placeholder = new Gson().fromJson(inputJson, JsonObject.class) ;
        String phone = placeholder.get("phone").getAsString();
        
        JsonResponse returnMsg = new JsonResponse();
        
        if(checkUserExistByPhone(phone) == true) {
            returnMsg.setStatus(0);
            returnMsg.setMessage("User Exist.");
            return new Gson().toJson(returnMsg);
        }
        else {
            returnMsg.setStatus(0);
            returnMsg.setMessage("User Doesn't Exist.");
            }
        
        return new Gson().toJson(returnMsg);
    }
    
    public boolean checkUserExistByPhone(String phone){
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

            PreparedStatement fetchUserByPhone = connection.prepareStatement(
                        "SELECT * FROM chatapp.USERS\n" +
                        "WHERE Phone=?;"
                        );
            fetchUserByPhone.setString(1, phone);
            ResultSet result = fetchUserByPhone.executeQuery();
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
}
