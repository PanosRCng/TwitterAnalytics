package TwitterAnalytics;


import java.sql.*;

import TwitterAnalytics.ConfigManager.Config;



public class DB
{

    private Connection conn = null;


    private DB()
    {
        System.out.println("connecting to database " + Config.database().DATABASE_NAME);

        String url = "jdbc:mysql://" + Config.database().DATABASE_SERVER + ":" + Config.database().DATABASE_PORT + "/" + Config.database().DATABASE_NAME + "?useTimezone=true&serverTimezone=Europe/Athens&useSSL=false";

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


    public static ResultSet query(String query)
    {
        try
        {
            ResultSet resultSet = DB.conn().createStatement().executeQuery(query);

            if(!resultSet.next())
            {
                return null;
            }

            return resultSet;
        }
        catch(SQLException ex)
        {
            System.out.println(ex);
        }

        return null;
    }

}
