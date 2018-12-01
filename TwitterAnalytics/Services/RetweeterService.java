package TwitterAnalytics.Services;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Retweeter;

import javax.persistence.Query;
import java.util.List;

public class RetweeterService {

    public static List<Long> getRetweeters(Boolean appUserFlag) {

        Query q = Hibernate.session().createQuery("select retweeter_id from Retweeter where appUserFlag = :appUserFlag");
        q.setParameter("appUserFlag", appUserFlag);

        List tweetsResults = q.getResultList();

        return tweetsResults;

    }

    public static List<Retweeter> getRetweets(Boolean appUserFlag) {

        Query q = Hibernate.session().createQuery("from Retweeter where appUserFlag = :appUserFlag");
        q.setParameter("appUserFlag", appUserFlag);

        List tweetsResults = q.getResultList();

        return tweetsResults;

    }
}
