package com.chatapp.messaging;

import com.chatapp.JsonResponse.JsonResponse;
import com.chatapp.db.ChatAppDB;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import com.chatapp.messaging.FCMConnector;
import com.chatapp.user.User;

/**
 * REST Web Service
 *
 * @author MaTaN
 */
@Path("messaging")
public class MessagingResource {

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
            returnMsg.setStatus(-1);
            returnMsg.setMessage("User Doesn't Exist.");
            }
        
        return new Gson().toJson(returnMsg);
    }
    
    @PUT
    @Path("sendMessage")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String sendMessage(String inputJson) throws Exception{
        FCMConnector fcm = FCMConnector.getInstance();
        JsonResponse returnMsg = new JsonResponse();
        
        JsonObject placeholder = new Gson().fromJson(inputJson, JsonObject.class) ;
        String recipient = placeholder.get("to").getAsString();
        String sender = placeholder.get("from").getAsString();
        String msg = placeholder.get("msg").getAsString();
         
        if(checkUserExistByPhone(recipient) == true) {
            String recipientToken = getUserToken(recipient);
            JsonObject dataObject = new JsonObject(); 
            JsonObject notificationObject = new JsonObject();
            dataObject.addProperty("msg", msg);
            dataObject.addProperty("From", sender); 
            notificationObject.addProperty("Title", "New Message From " + sender); 
            // RECIPIENT is the token of the device, or device group, or a topic.
            storeMessage(sender, recipient, msg);
            fcm.sendNotifictaionAndData(FCMConnector.TYPE_TO, recipientToken, notificationObject, dataObject);
                    
            returnMsg.setStatus(0);
            returnMsg.setMessage("Message Sent.");
        }
        else {
            returnMsg.setStatus(-1);
            returnMsg.setMessage("Error: User Doesn't Exist.");
            }
        
        return new Gson().toJson(returnMsg);
    }
    
    public String getUserToken(String recipient){
        Connection connection = null;
        try 
          {
            connection = ChatAppDB.getConnection();
            PreparedStatement getToken = connection.prepareStatement(
                        "SELECT TOKEN FROM chatapp.USERS\n" +
                        "WHERE Phone=?;"
                        );
            getToken.setString(1, recipient);
            ResultSet result = getToken.executeQuery();
            while(result.next()) {
                String token = result.getString("TOKEN");
                return token;
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
        return null;
    }
    
    public boolean checkUserExistByPhone(String phone){
        Connection connection = null;
        try {
            connection = ChatAppDB.getConnection(); 
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
    
    public User getUserByPhone(String phone) {
        Connection connection = null;
        try {
            connection = ChatAppDB.getConnection();
            PreparedStatement getToken = connection.prepareStatement(
                        "SELECT * FROM chatapp.USERS\n" +
                        "WHERE Phone=?;"
                        );
            getToken.setString(1, phone);
            ResultSet result = getToken.executeQuery();
            while(result.next()) {
                User user = new User(result.getString("id"), result.getString("name"), 
                        result.getString("email"), result.getString("phone"), result.getString("TOKEN"));
                return user;
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
        return null;
    }
    
    public void storeMessage(String sender, String recipient, String msg){
        User senderUser = getUserByPhone(sender);
        User recipientUser = getUserByPhone(recipient);
        
        Connection connection = null;
        try {
            connection = ChatAppDB.getConnection();
            PreparedStatement saveMessage = connection.prepareStatement(
             "INSERT INTO chatapp.MESSAGES (\"message\", \"sender_id\",\"sender_name\"," +
                                           "\"recipient_id\", \"recipient_name\", \"time\")\n" +
             "VALUES (?, ?, ?, ?, ?, ?);"
             );
            saveMessage.setString(1, msg);
            saveMessage.setString(2, senderUser.getId());
            saveMessage.setString(3, senderUser.getName());
            saveMessage.setString(4, recipientUser.getId());
            saveMessage.setString(5, recipientUser.getName());
            java.util.Date today = new java.util.Date();
            saveMessage.setTimestamp(6, new java.sql.Timestamp(today.getTime()));
            saveMessage.execute();
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
    }
}