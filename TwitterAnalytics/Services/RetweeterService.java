package TwitterAnalytics.Services;

import TwitterAnalytics.Hibernate;

import javax.persistence.Query;
import java.util.List;

public class RetweeterService {

    public static List<Long> getRetweeters() {

        Query q = Hibernate.session().createQuery("select retweeter_id from Retweeter");

        List tweetsResults = q.getResultList();

        return tweetsResults;

    }
}
