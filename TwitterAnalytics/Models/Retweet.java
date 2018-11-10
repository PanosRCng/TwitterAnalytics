package TwitterAnalytics.Models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "retweets", uniqueConstraints = {@UniqueConstraint(columnNames = {"retweet","retweetedTweet"})})
public class Retweet extends AbstractTimestampEntity implements Serializable {

    public Retweet(Tweet retweet, Tweet retweetedTweet, Timestamp timestamp) {
        this.retweet = retweet;
        this.retweetedTweet = retweetedTweet;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "retweet")
    private Tweet retweet;

    @ManyToOne
    @JoinColumn(name = "retweetedTweet")
    private Tweet retweetedTweet;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tweet getRetweet() {
        return retweet;
    }

    public void setRetweet(Tweet retweet) {
        this.retweet = retweet;
    }

    public Tweet getRetweetedTweet() {
        return retweetedTweet;
    }

    public void setRetweetedTweet(Tweet retweetedTweet) {
        this.retweetedTweet = retweetedTweet;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Retweet{" +
                "id=" + id +
                ", retweet=" + retweet +
                ", retweetedTweet=" + retweetedTweet +
                ", timestamp=" + timestamp +
                '}';
    }
}
