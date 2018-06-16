package TwitterAnalytics;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import TwitterAnalytics.ConfigManager.Config;



public class DB
{

    private Connection conn = null;


    private DB()
    {
        System.out.println("connecting to database " + Config.database().DATABASE_NAME);

        String url = "jdbc:mysql://" + Config.database().DATABASE_SERVER + ":" +
                Config.database().DATABASE_PORT + "/" + Config.database().DATABASE_NAME+"?rewriteBatchedStatements=true";

        try
        {
            this.conn = DriverManager.getConnection(url, Config.database().DATABASE_USER, Config.database().DATABASE_PASSWORD);

            System.out.println("database connected...OK");
        }
        catch(SQLException e)
        {
            System.out.println("database connected...FAILED");
        }
    }


    private static class SingletonHelper
    {
        private static final DB INSTANCE = new DB();
    }


    public static Connection conn()
    {
        return SingletonHelper.INSTANCE.conn;
    }


}