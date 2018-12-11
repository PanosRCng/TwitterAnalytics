package TwitterAnalytics.Models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "replies", uniqueConstraints = {@UniqueConstraint(columnNames = {"replyId","repliedTweetId"})})
public class Reply extends AbstractTimestampEntity implements Serializable {

    public Reply(Long replyId, Long repliedTweetId, Timestamp timestamp, Long userIdOfRepliedTweet) {
        this.replyId = replyId;
        this.repliedTweetId = repliedTweetId;
        this.timestamp = timestamp;
        this.userIdOfRepliedTweet = userIdOfRepliedTweet;
    }

    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "replyId")
    private Long replyId;

    @Column(name = "repliedTweetId")
    private Long repliedTweetId;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "userIdOfRepliedTweet")
    private Long userIdOfRepliedTweet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public Long getRepliedTweetId() {
        return repliedTweetId;
    }

    public void setRepliedTweetId(Long repliedTweetId) {
        this.repliedTweetId = repliedTweetId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Long getUserIdOfRepliedTweet() {
        return userIdOfRepliedTweet;
    }

    public void setUserIdOfRepliedTweet(Long userIdOfRepliedTweet) {
        this.userIdOfRepliedTweet = userIdOfRepliedTweet;
    }

    @Override
    public String toString() {
        return "Reply{" +
                "id=" + id +
                ", replyId=" + replyId +
                ", repliedTweetId=" + repliedTweetId +
                ", timestamp=" + timestamp +
                ", userIdOfRepliedTweet=" + userIdOfRepliedTweet +
                '}';
    }
}
