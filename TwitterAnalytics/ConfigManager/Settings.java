package TwitterAnalytics.ConfigManager;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


public class Settings
{

    public Map<String, Object> settings()
    {
        Map<String, Object> settings_map = new HashMap<String, Object>();

        try
        {
            for(Field field : this.getClass().getDeclaredFields())
            {
                settings_map.put(field.getName(), field.get(this));
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }

        return settings_map;
    }


    public void setSetting(String setting_key, Object setting_value)
    {
        try
        {
            for(Field field : this.getClass().getDeclaredFields())
            {
                if( !field.getName().equals(setting_key) )
                {
                    continue;
                }

                field.set(this, setting_value);
            }
        }
        catch(Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

}