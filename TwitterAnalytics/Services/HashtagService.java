package TwitterAnalytics.Services;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Hashtag;

import javax.persistence.Query;
import java.util.List;


public class HashtagService
{

    public static Hashtag createHashtag(String name)
    {
        Query q = Hibernate.session().createQuery("from Hashtag where name = :name");
        q.setParameter("name", name);
        List hashtagsResults = q.getResultList();

        if(hashtagsResults.size() > 0)
        {
            return (Hashtag) hashtagsResults.get(0);
        }

        Hashtag hashtag = new Hashtag(name);
        Hibernate.save(hashtag);

        return hashtag;
    }

}
