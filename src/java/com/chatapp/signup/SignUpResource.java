package com.chatapp.signup;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import com.chatapp.JsonResponse.JsonResponse;
import com.chatapp.db.ChatAppDB;
import com.chatapp.user.User;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * REST Web Service - Web service for signing up user for the chat application.
 *
 * @author MaTaN
 */
@Path("SignUp")
public class SignUpResource {
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
    public String signup(String inputJson){
        User user = new Gson().fromJson(inputJson, User.class);

        JsonResponse returnMsg = new JsonResponse();
        if(checkUserExist(user) == true) {
            returnMsg.setStatus(-1);
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
                returnMsg.setStatus(-1);
                returnMsg.setMessage("Error!");
            }
        }
        
        return new Gson().toJson(returnMsg);
    }
    
    public boolean checkUserExist(User user){
        Connection connection = null;
        try {
            connection = ChatAppDB.getConnection();
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
        Connection connection = null;
        try {
            connection = ChatAppDB.getConnection();
            PreparedStatement createUser = connection.prepareStatement(
                        "INSERT INTO chatapp.USERS (ID, \"NAME\", EMAIL, PHONE, TOKEN)\n" +
                        "VALUES (?,?,?,?,?);"
                        );

            createUser.setLong(1, Long.parseLong(user.getId()));
            createUser.setString(2, user.getName());
            createUser.setString(3, user.getEmail());
            createUser.setString(4, user.getPhone());
            createUser.setString(5, user.getToken());
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

    /**
     * Retrieves representation of an instance of com.chatapp.signup.SignUpResource
     * @return an instance of java.lang.String
     */
    @PUT
    @Path("updateToken")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String updateToken(String inputJson) throws Exception {
        JSONObject jsonObj = (JSONObject) new JSONParser().parse(inputJson);
        String token = jsonObj.get("token").toString();
        String id = jsonObj.get("id").toString();
        
        JsonResponse returnMsg = new JsonResponse();
        
        boolean success = updateUserToken(id, token);
        if (success == true){
            returnMsg.setStatus(0);
            returnMsg.setMessage("Updated User Token!");
        }
        else {
            returnMsg.setStatus(-1);
            returnMsg.setMessage("Error!");
        }

        
        return new Gson().toJson(returnMsg);
    }
    
    public boolean updateUserToken(String id, String token){
        Connection connection = null;
        try {
            connection = ChatAppDB.getConnection();
            PreparedStatement updateUserToken = connection.prepareStatement(
                        "UPDATE chatapp.USERS\n" +
                        "SET \"TOKEN\" = ?\n" +
                        "WHERE \"ID\" = ? ;"
                        );

            updateUserToken.setString(1, token);
            updateUserToken.setLong(2, Long.parseLong(id));
            updateUserToken.execute();
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