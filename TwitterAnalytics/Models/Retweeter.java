package TwitterAnalytics.Models;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Entity
@Table(name = "retweeters", uniqueConstraints = {@UniqueConstraint(columnNames = {"retweeter_id","retweeted_user_id","timestamp"})})
@Inheritance(strategy = InheritanceType.JOINED)
public class Retweeter extends AbstractTimestampEntity implements Serializable {

    public Retweeter(Long retweeter_id, Long retweeted_user_id, Timestamp timestamp) { ;
        this.retweeter_id = retweeter_id;
        this.retweeted_user_id = retweeted_user_id;
        this.timestamp = timestamp;
    }

    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "retweeter_id")
    private Long retweeter_id;

    @Column(name = "retweeted_user_id")
    private Long retweeted_user_id;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRetweeter_id() {
        return retweeter_id;
    }

    public void setRetweeter_id(Long retweeter_id) {
        this.retweeter_id = retweeter_id;
    }

    public Long getRetweeted_user_id() {
        return retweeted_user_id;
    }

    public void setRetweeted_user_id(Long retweeted_user_id) {
        this.retweeted_user_id = retweeted_user_id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Retweeter{" +
                "id=" + id +
                ", retweeter_id=" + retweeter_id +
                ", retweeted_user_id=" + retweeted_user_id +
                ", timestamp=" + timestamp +
                '}';
    }
}
