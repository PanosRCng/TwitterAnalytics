package TwitterAnalytics.Models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.sql.Timestamp;

@Entity
@Table(name = "hubretweeters", uniqueConstraints = {@UniqueConstraint(columnNames = {"retweeter_id","retweeted_user_id","timestamp"})})
public class HubRetweeter extends Retweeter{

    public HubRetweeter(Long retweeter_id, Long retweeted_user_id, Timestamp timestamp) {
        super(retweeter_id, retweeted_user_id, timestamp);
    }
}
