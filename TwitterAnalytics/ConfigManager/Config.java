package TwitterAnalytics.ConfigManager;



public class Config
{

    private static final Config instance = new Config();
    private static final String CONFIG_PATH = "TwitterAnalytics/Config/";


    private Env env = null;
    private TwitterApi twitter_api = null;


    private Config()
    {
        this.env =  (Env) Utils.loadConfig("env.json", Env.class);
    }


    public static Config instance()
    {
        return instance;
    }


    public Env env()
    {
        return this.env;
    }


    public TwitterApi twitter_api()
    {
        if(this.twitter_api == null)
        {
            this.twitter_api =  (TwitterApi) Utils.loadConfig(CONFIG_PATH + "twitter_api.json", TwitterApi.class, this.env);
        }

        return this.twitter_api;
    }

}
