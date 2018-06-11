package TwitterAnalytics.ConfigManager;


import TwitterAnalytics.ConfigManager.Templates.Database;
import TwitterAnalytics.ConfigManager.Templates.Env;
import TwitterAnalytics.ConfigManager.Templates.TwitterApi;

public class Config
{
    private static final String CONFIG_PATH = "TwitterAnalytics/Config/";

    private Env env = null;
    private TwitterApi twitter_api = null;
    private Database database = null;


    private Config()
    {
        this.env =  (Env) Utils.loadConfig("env.json", Env.class);
    }


    private static class SingletonHelper
    {
        private static final Config INSTANCE = new Config();
    }


    public static Env env()
    {
        return SingletonHelper.INSTANCE.env;
    }


    public static TwitterApi twitter_api()
    {
        if(SingletonHelper.INSTANCE.twitter_api == null)
        {
            SingletonHelper.INSTANCE.twitter_api =  (TwitterApi) Utils.loadConfig(CONFIG_PATH + "twitter_api.json", TwitterApi.class, SingletonHelper.INSTANCE.env);
        }

        return SingletonHelper.INSTANCE.twitter_api;
    }


    public static Database database()
    {
        if(SingletonHelper.INSTANCE.database == null)
        {
            SingletonHelper.INSTANCE.database =  (Database) Utils.loadConfig(CONFIG_PATH + "database.json", Database.class, SingletonHelper.INSTANCE.env);
        }

        return SingletonHelper.INSTANCE.database;
    }

}
