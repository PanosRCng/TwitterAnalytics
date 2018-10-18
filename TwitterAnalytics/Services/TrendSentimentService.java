package TwitterAnalytics.Services;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.TrendsList;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class TrendSentimentService
{

    public static List<TrendsList> getAll()
    {
        try
        {
            return Hibernate.session().createCriteria(TrendsList.class).list();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }


    public static List<Long> removeTrendSentimentByDateRange(Date fromDate, Date toDate)
    {
        List<Long> toDeleteIds = new ArrayList<>();

        try
        {
            NativeQuery query = Hibernate.session().createSQLQuery("SELECT id from trend_sentiments where created_at between :fromDate and :toDate");
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            List<Integer> results = query.getResultList();

            for(Integer id: results)
            {
                toDeleteIds.add(id.longValue());
            }
        }
        catch (Exception e)
        {
            //
        }

        if(toDeleteIds.size() > 0)
        {
            try
            {
                Transaction transaction = Hibernate.session().getTransaction();
                transaction.begin();

                NativeQuery query = Hibernate.session().createSQLQuery("delete from trend_sentiments where id IN (:ids)");
                query.setParameterList("ids", toDeleteIds);
                query.executeUpdate();

                transaction.commit();
            }
            catch (Exception e)
            {
                //
            }
        }

        return toDeleteIds;
    }

}
