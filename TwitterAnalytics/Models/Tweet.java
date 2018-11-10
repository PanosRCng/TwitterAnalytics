package TwitterAnalytics.Models;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


@Entity
@Table(name = "tweets", uniqueConstraints = {@UniqueConstraint(columnNames = {"twitter_id"})})


public class Tweet extends AbstractTimestampEntity implements Serializable
{

    public Tweet()
    {
        //
    }


    public Tweet(String text, long twitter_id, Timestamp timestamp)
    {
        this.text = text;
        this.twitter_id = twitter_id;
        this.timestamp = timestamp;
    }


    public Tweet(String text, String clean_text, long twitter_id, Timestamp timestamp)
    {
        this.text = text;
        this.twitter_id = twitter_id;
        this.clean_text = clean_text;
        this.timestamp = timestamp;
    }


    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "twitter_id", unique = true)
    private Long twitter_id;

    @Column(name = "text", length = 1024)
    private String text;

    @Column(name = "clean_text", length = 1024)
    private String clean_text;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getTwitterId()
    {
        return this.twitter_id;
    }

    public void setTwitterId(Long twitter_id)
    {
        this.twitter_id = twitter_id;
    }

    public String getText()
    {
        return this.text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getCleanText()
    {
        return this.clean_text;
    }

    public void setCleanText(String clean_text)
    {
        this.clean_text = clean_text;
    }

    public Timestamp getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        this.timestamp = timestamp;
    }
}
