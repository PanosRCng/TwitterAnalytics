package TwitterAnalytics.Models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "replies", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
public class Replies implements Serializable {

    public Replies()
    {
        //
    }

    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;
}
