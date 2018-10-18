package TwitterAnalytics.Models;


import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "hashtags", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})


public class Hashtag extends AbstractTimestampEntity implements Serializable
{

    public Hashtag()
    {
        //
    }


    public Hashtag(String name)
    {
        this.name = name;
    }


    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "name", unique = true, length = 50)
    private String name;

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
}
