package TwitterAnalytics.ConfigManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Map;
import java.util.Map.Entry;

import TwitterAnalytics.ConfigManager.Templates.Env;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Utils
{

    private Utils()
    {
        //
    }


    public static Object loadConfig(String filename, Class confClass)
    {
        System.out.println(confClass.getSimpleName() + " initialising configuration");

        Object conf = Utils.loadConfigurationFromFile(filename, confClass);

        if(conf == null)
        {
            System.out.println(confClass.getSimpleName() + " initialisation...FAILED");

            return null;
        }

        System.out.println(confClass.getSimpleName() + " initialisation...OK");

        return conf;
    }


    public static Object loadConfig(String filename, Class confClass, Env env)
    {
        System.out.println(confClass.getSimpleName() + " initialising configuration");

        Settings conf = (Settings) Utils.loadConfigurationFromFile(filename, confClass);

        if(conf == null)
        {
            System.out.println(confClass.getSimpleName() + " initialisation...FAILED");

            return null;
        }

        if(env != null)
        {
            Map<String, Object> env_settings = env.settings();

            for(Entry<String, Object> entry : conf.settings().entrySet())
            {
                String setting_key = entry.getKey();

                if(!env_settings.containsKey(setting_key))
                {
                    continue;
                }

                conf.setSetting(setting_key, env_settings.get(setting_key));

                System.out.println("setting override by env value, " + confClass.getSimpleName() + "." + setting_key + " = " + env_settings.get(setting_key));
            }

        }

        System.out.println(confClass.getSimpleName() + " initialisation...OK");

        return conf;
    }


    public static Object loadConfigurationFromFile(String filename, Class confClass)
    {
        File file = new File(filename);

        if( !(file.exists() && !file.isDirectory()) )
        {
            System.out.println("configuration file: " + filename + " does not exists");
            return null;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try
        {
            String fileData = new String(Files.readAllBytes(Paths.get(filename)));

            return gson.fromJson(fileData, confClass);
        }
        catch(IOException ex)
        {
            System.out.println("Could load configuration file: " + filename);
        }

        return null;
    }

}
