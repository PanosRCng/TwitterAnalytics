package TwitterAnalytics.Models;


import TwitterAnalytics.Services.TrendService;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


@Entity
@Table(name = "trends", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})


public class Trend extends AbstractTimestampEntity implements Serializable
{

    public Trend()
    {
        //
    }


    public Trend(String name, String query)
    {
        this.name = name;
        this.query = query;
    }


    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "name", unique = true, length = 256)
    private String name;

    @Column(name = "query", length = 2048)
    private String query;


    public List<Tweet> tweets()
    {
        return TrendService.tweets(this.id);
    }

    public void addTweet(Tweet tweet)
    {
        TrendService.addTweet(this.getId(), tweet.getId());
    }

    public void addHashtag(Hashtag hashtag)
    {
        TrendService.addHashtag(this.getId(), hashtag.getId());
    }


    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }


    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }


    public String getQuery()
    {
        return this.query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }
}
