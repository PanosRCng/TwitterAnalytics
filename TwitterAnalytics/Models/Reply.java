package TwitterAnalytics.Models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "replies", uniqueConstraints = {@UniqueConstraint(columnNames = {"id","reply","repliedTweet"})})
public class Reply extends AbstractTimestampEntity implements Serializable {

    public Reply(Tweet reply, Tweet repliedTweet, Timestamp timestamp) {
        this.reply = reply;
        this.repliedTweet = repliedTweet;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reply")
    private Tweet reply;

    @ManyToOne
    @JoinColumn(name = "repliedTweet")
    private Tweet repliedTweet;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tweet getReply() {
        return reply;
    }

    public void setReply(Tweet reply) {
        this.reply = reply;
    }

    public Tweet getRepliedTweet() {
        return repliedTweet;
    }

    public void setRepliedTweet(Tweet repliedTweet) {
        this.repliedTweet = repliedTweet;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", reply=" + reply +
                ", repliedTweet=" + repliedTweet +
                ", timestamp=" + timestamp +
                '}';
    }
}
