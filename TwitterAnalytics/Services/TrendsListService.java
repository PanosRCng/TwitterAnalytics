package TwitterAnalytics.Services;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.TrendsList;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import TwitterAnalytics.Models.Trend;



public class TrendsListService
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


    public static List<TrendsList> getByDateRange(Date fromDate, Date toDate)
    {
        try
        {
            NativeQuery query = Hibernate.session().createSQLQuery("select * from trends_lists where timestamp between :fromDate and :toDate");
            query.setParameter("fromDate", fromDate);
            query.setParameter("toDate", toDate);
            query.addEntity(TrendsList.class);

            return query.getResultList();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }


    public static List<Trend> trends(List<Long> trendsIds)
    {
        try
        {
            Query query = Hibernate.session().createQuery("FROM Trend trend WHERE trend.id IN (:ids)");
            query.setParameterList("ids", trendsIds);

            return query.getResultList();
        }
        catch (Exception e)
        {
            return new ArrayList<>();
        }
    }


    public static List<Long> removeTrendsListsByDateRange(Date fromDate, Date toDate)
    {
        List<Long> toDeleteIds = new ArrayList<>();

        try
        {
            NativeQuery query = Hibernate.session().createSQLQuery("SELECT id from trends_lists where timestamp between :fromDate and :toDate");
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

                NativeQuery query = Hibernate.session().createSQLQuery("delete from trends_lists where id IN (:ids)");
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
