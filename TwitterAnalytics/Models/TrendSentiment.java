package TwitterAnalytics.Models;


import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "trend_sentiments", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})


public class TrendSentiment extends AbstractTimestampEntity implements Serializable
{

    public TrendSentiment()
    {
        //
    }


    public TrendSentiment(long trend_id, double anger, double disgust, double fear, double happiness, double sadness, double surprise)
    {
        this.trend_id = trend_id;
        this.anger = anger;
        this.disgust = disgust;
        this.fear = fear;
        this.happiness = happiness;
        this.sadness = sadness;
        this.surprise = surprise;
    }


    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "trend_id")
    private Long trend_id;

    @Column(name = "anger")
    private Double anger;

    @Column(name = "disgust")
    private Double disgust;

    @Column(name = "fear")
    private Double fear;

    @Column(name = "happiness")
    private Double happiness;

    @Column(name = "sadness")
    private Double sadness;

    @Column(name = "surprise")
    private Double surprise;


    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }


    public Long getTrend_id()
    {
        return this.trend_id;
    }

    public void setTrend_id(Long trend_id)
    {
        this.trend_id = trend_id;
    }


    public Double getAnger()
    {
        return this.anger;
    }

    public void setAnger(Double anger)
    {
        this.anger = anger;
    }


    public Double getDisgust()
    {
        return this.disgust;
    }

    public void setDisgust(Double disgust)
    {
        this.disgust = disgust;
    }


    public Double getFear()
    {
        return this.fear;
    }

    public void setFear(Double fear)
    {
        this.fear = fear;
    }


    public Double getHappiness()
    {
        return this.happiness;
    }

    public void setHappiness(Double happiness)
    {
        this.happiness = happiness;
    }


    public Double getSadness()
    {
        return this.sadness;
    }

    public void setSadness(Double sadness)
    {
        this.sadness = sadness;
    }


    public Double getSurprise()
    {
        return this.surprise;
    }

    public void setSurprise(Double surprise)
    {
        this.surprise = surprise;
    }
}
