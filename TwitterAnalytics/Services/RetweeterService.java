package TwitterAnalytics.Services;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Retweeter;

import javax.persistence.Query;
import java.util.List;

public class RetweeterService {

    public static List<Long> getRetweeters(Boolean app_user_flag) {

        Query q = Hibernate.session().createQuery("select retweeter_id from Retweeter where app_user_flag = :app_user_flag");
        q.setParameter("app_user_flag", app_user_flag);

        List tweetsResults = q.getResultList();

        return tweetsResults;

    }

    public static List<Retweeter> getRetweets(Boolean app_user_flag) {

        Query q = Hibernate.session().createQuery("from Retweeter where app_user_flag = :app_user_flag");
        q.setParameter("app_user_flag", app_user_flag);
        System.out.println(q.toString());
        List tweetsResults = q.getResultList();

        return tweetsResults;

    }

    public static List<Object> getDistinctRetweetersSelectedDates(Boolean app_user_flag,
                                                                  String startDate,
                                                                  String endDate) {

        Query q = Hibernate.session().createQuery("select retweeter_id, count(retweeted_user_id) from Retweeter where app_user_flag = :app_user_flag and DATE(timestamp) BETWEEN DATE(:startDate) and DATE(:endDate) group by retweeter_id");
        q.setParameter("app_user_flag", app_user_flag);
        q.setParameter("startDate", startDate);
        q.setParameter("endDate", endDate);

        List tweetsResults = q.getResultList();

        return tweetsResults;

    }
}
