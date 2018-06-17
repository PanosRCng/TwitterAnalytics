package TwitterAnalytics;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import TwitterAnalytics.ConfigManager.Config;



public class DB
{

    private Connection conn = null;


    private DB()
    {
        System.out.println("connecting to database " + Config.database().DATABASE_NAME);

        String url = "jdbc:mysql://" + Config.database().DATABASE_SERVER + ":" + Config.database().DATABASE_PORT + "/" + Config.database().DATABASE_NAME;

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


    public static int insert(String query)
    {
        try
        {
            Statement stmt = DB.conn().createStatement();

            stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            ResultSet resultSet = stmt.getGeneratedKeys();

            if(resultSet.next())
            {
                return resultSet.getInt(1);
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex);
        }

        return -1;
    }


    public static boolean update(String query)
    {
        try
        {
            int affected_rows = DB.conn().createStatement().executeUpdate(query);

            if(affected_rows == 1)
            {
                return true;
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex);
        }

        return false;
    }


    public static boolean delete(String query)
    {
        try
        {
            int affected_rows = DB.conn().createStatement().executeUpdate(query);

            if(affected_rows == 1)
            {
                return true;
            }
        }
        catch(SQLException ex)
        {
            System.out.println(ex);
        }

        return false;
    }

}
