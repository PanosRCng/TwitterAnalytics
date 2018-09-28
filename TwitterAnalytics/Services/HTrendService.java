package TwitterAnalytics.Services;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Trend;
import TwitterAnalytics.Models.Tweet;

import java.util.List;
import java.util.ArrayList;
import javax.persistence.Query;


public class HTrendService
{


    public static List<Trend> getAll()
    {
        try
        {
            return Hibernate.session().createCriteria(Trend.class).list();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }



    public static List<Tweet> tweets(Long trend_id)
    {
        try
        {
            Query query = Hibernate.session().createQuery("from Tweet where trend_id = :trend_id ");
            query.setParameter("trend_id", trend_id);
            return query.getResultList();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }

}
