package com.chatapp.initdb;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.chatapp.db.ChatAppDB;

/**
 * REST Web Service - To initialize the application DB resource
 *
 * @author MaTaN
 */
@Path("InitDB")
public class InitDBResource {    

    /**
     * Creates a new instance of InitDBResource
     */
    public InitDBResource(){}

    /**
     * Retrieves representation of an instance of com.chatapp.initdb.InitDBResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("createSchema")
    @Consumes(MediaType.APPLICATION_XML)
    public void initiateDatabase() {
        Connection connection = null;
        try {
            connection = ChatAppDB.getConnection();
            createDb(connection);
            createUsersTable(connection);
            createMessagesTable(connection);
            System.out.println("Created The DB Structure!");
        }
        catch (SQLException e){
            e.printStackTrace();
            //return false;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally
        {
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
    public boolean createDb(Connection connection) throws SQLException{
        PreparedStatement createDbQuery = connection.prepareStatement(
                    "IF NOT EXISTS (SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'chatapp')" +
                    "EXEC('CREATE SCHEMA chatapp AUTHORIZATION dbo');"
                    );
        return createDbQuery.execute();
    }
    public boolean createUsersTable(Connection connection) throws SQLException{
        PreparedStatement createTableQuery = connection.prepareStatement(
                "IF (NOT EXISTS (SELECT * "
              + "FROM INFORMATION_SCHEMA.TABLES "
              + "WHERE TABLE_SCHEMA = 'chatapp' "
              + "AND TABLE_NAME = 'USERS'))"
              + "BEGIN\n" +
              "CREATE TABLE chatapp.USERS" +
                    "(ID    BIGINT default -1 not null primary key," +
                    "NAME   VARCHAR(25) default null," +
                    "EMAIL  VARCHAR(60) default null," + 
                    "TOKEN  VARCHAR(500) default null," +
                    "PHONE  VARCHAR(15))\n"
               + " END ;"    
                ) ;
        return createTableQuery.execute();
    }
    
    public boolean createMessagesTable(Connection connection) throws SQLException{
        PreparedStatement createTableQuery = connection.prepareStatement(
                "IF (NOT EXISTS (SELECT * "
              + "FROM INFORMATION_SCHEMA.TABLES "
              + "WHERE TABLE_SCHEMA = 'chatapp' "
              + "AND TABLE_NAME = 'MESSAGES'))"
              + "BEGIN\n" +
                    "CREATE TABLE chatapp.MESSAGES" +
                    "(ID                INT not null IDENTITY(1,1) PRIMARY KEY," +
                    "message            varchar(999) default null," +
                    "sender_id          BIGINT default -1 not null," +
                    "sender_name        VARCHAR(25) not null," +
                    "recipient_id       BIGINT default -1 not null," +
                    "recipient_name     VARCHAR(25) not null," +
                    "time DATETIME," +
                    "foreign key(sender_id) REFERENCES chatapp.USERS(ID))\n"
               + "END ;"
                    );
            
        return createTableQuery.execute();
    }
    
    @GET
    @Path("clearTables")
    public void clearTables() {
        Connection connection = null;
        try 
        {
            connection = ChatAppDB.getConnection();
            PreparedStatement clearTablesQuery = connection.prepareStatement(
                "DELETE FROM chatapp.USERS;"
                + "DELETE FROM chatapp.MESSAGES;");
            
            clearTablesQuery.execute();
            System.out.println("Cleared Database Tables.");
        }
        catch (SQLException e){
            e.printStackTrace();
            //return false;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally
        {
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
