package TwitterAnalytics.Models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "replies", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Reply implements Serializable {

    public Reply(Tweet reply, Tweet repliedTweet) {
        this.reply = reply;
        this.repliedTweet = repliedTweet;
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

    @Override
    public String toString() {
        return "Replies{" +
                "id=" + id +
                ", reply=" + reply +
                ", repliedTweet=" + repliedTweet +
                '}';
    }
}
