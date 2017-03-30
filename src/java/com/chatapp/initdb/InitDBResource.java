package com.chatapp.initdb;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.annotation.Resource;
import javax.annotation.sql.DataSourceDefinition;
import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.DriverManager;

@DataSourceDefinition(
    name = "java:global/jdbc/chatapp",
    className = "org.apache.derby.jdbc.ClientDataSource",
    url = "jdbc:derby://app9443.database.windows.net/",
    databaseName = "chatapp",
    user = "root",
    password = "root" )

/**
 * REST Web Service
 *
 * @author MaTaN
 */
@Path("InitDB")
public class InitDBResource {
    
    
    
    //InitialContext ic = new InitialContext();
    //DataSource dataSource = (DataSource) ic.lookup("java:global/jdbc/chatapp");
    
    //@Resource(name="java:global/jdbc/chatapp")
    //DataSource dataSource;
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of InitDBResource
     */
    public InitDBResource() throws NamingException {
    }

    /**
     * Retrieves representation of an instance of com.chatapp.initdb.InitDBResource
     * @return an instance of java.lang.String
     */
    @GET
    @Path("createSchema")
    @Consumes(MediaType.APPLICATION_XML)
    public void initiateDatabase() {
        Connection connection = null;
     
        try 
        {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } 
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            } 

            connection = DriverManager.getConnection(
                    "jdbc:sqlserver://app9443.database.windows.net:1433;database=chatapp;user=chatapp@app9443;password=dov#2017");
            
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
                    "(ID                BIGINT not null PRIMARY KEY," +
                    "message            varchar(999) default null," +
                    "sender_id          BIGINT default -1 not null," +
                    "sender_name        VARCHAR(25) not null," +
                    "recipient_id       BIGINT default -1 not null," +
                    "recipient_name     VARCHAR(25) not null," +
                    "timestamp timestamp," +
                    "foreign key(sender_id) REFERENCES chatapp.USERS(ID))\n"
               + "END ;"
                    );
            
        return createTableQuery.execute();
    }
}
