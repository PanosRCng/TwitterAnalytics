package TwitterAnalytics.Services;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.HubRetweeter;

import java.util.ArrayList;
import java.util.List;

public class HubRetweeterService {

    public static List<HubRetweeter> getAll()
    {
        try
        {
            return Hibernate.session().createCriteria(HubRetweeter.class).list();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }
}
