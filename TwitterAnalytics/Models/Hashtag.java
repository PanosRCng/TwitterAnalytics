package TwitterAnalytics.Models;


import javax.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.SQLInsert;


@Entity
@Table(name = "hashtags", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})
@SQLInsert(sql="insert ignore into hashtags (name, trend_id, id) values (?, ?, ?)")


public class Hashtag implements Serializable
{

    public Hashtag()
    {
        //
    }


    public Hashtag(String name, Long trend_id)
    {
        this.name = name;
        this.trend_id = trend_id;
    }


    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "name", unique = true, length = 50)
    private String name;

    @Column(name = "trend_id")
    private Long trend_id;

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


    public Long getTrend_id()
    {
        return this.trend_id;
    }

    public void setTrend_id(Long trend_id)
    {
        this.trend_id = trend_id;
    }
}
