package TwitterAnalytics.Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import java.io.Serializable;

import TwitterAnalytics.Services.TrendsListService;


@Entity
@Table(name = "trends_lists", uniqueConstraints = {@UniqueConstraint(columnNames = "id")})


public class TrendsList implements Serializable
{

    public TrendsList()
    {
        //
    }


    public TrendsList(Date timestamp, Long trend_id_1, Long trend_id_2, Long trend_id_3, Long trend_id_4, Long trend_id_5, Long trend_id_6, Long trend_id_7, Long trend_id_8, Long trend_id_9, Long trend_id_10)
    {
        this.timestamp = timestamp;

        this.trend_id_1 = trend_id_1;
        this.trend_id_2 = trend_id_2;
        this.trend_id_3 = trend_id_3;
        this.trend_id_4 = trend_id_4;
        this.trend_id_5 = trend_id_5;
        this.trend_id_6 = trend_id_6;
        this.trend_id_7 = trend_id_7;
        this.trend_id_8 = trend_id_8;
        this.trend_id_9 = trend_id_9;
        this.trend_id_10 = trend_id_10;
    }


    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "timestamp")
    private Date timestamp;

    @Column(name = "trend_id_1")
    private Long trend_id_1;

    @Column(name = "trend_id_2")
    private Long trend_id_2;

    @Column(name = "trend_id_3")
    private Long trend_id_3;

    @Column(name = "trend_id_4")
    private Long trend_id_4;

    @Column(name = "trend_id_5")
    private Long trend_id_5;

    @Column(name = "trend_id_6")
    private Long trend_id_6;

    @Column(name = "trend_id_7")
    private Long trend_id_7;

    @Column(name = "trend_id_8")
    private Long trend_id_8;

    @Column(name = "trend_id_9")
    private Long trend_id_9;

    @Column(name = "trend_id_10")
    private Long trend_id_10;


    public List<Trend> trends()
    {
        return TrendsListService.trends(this.trendsIds());
    }


    public List<Long> trendsIds()
    {
        List<Long> trendsIds = new ArrayList<>();

        trendsIds.add( this.getTrend_id_1() );
        trendsIds.add( this.getTrend_id_2() );
        trendsIds.add( this.getTrend_id_3() );
        trendsIds.add( this.getTrend_id_4() );
        trendsIds.add( this.getTrend_id_5() );
        trendsIds.add( this.getTrend_id_6() );
        trendsIds.add( this.getTrend_id_7() );
        trendsIds.add( this.getTrend_id_8() );
        trendsIds.add( this.getTrend_id_9() );
        trendsIds.add( this.getTrend_id_10() );

        return trendsIds;
    }


    public Long getId()
    {
        return this.id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Date getTimestamp()
    {
        return this.timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

    public Long getTrend_id_1()
    {
        return this.trend_id_1;
    }

    public void setTrend_id_1(Long trend_id_1)
    {
        this.trend_id_1 = trend_id_1;
    }

    public Long getTrend_id_2()
    {
        return this.trend_id_2;
    }

    public void setTrend_id_2(Long trend_id_2)
    {
        this.trend_id_2 = trend_id_2;
    }

    public Long getTrend_id_3()
    {
        return this.trend_id_3;
    }

    public void setTrend_id_3(Long trend_id_3)
    {
        this.trend_id_3 = trend_id_3;
    }

    public Long getTrend_id_4()
    {
        return this.trend_id_4;
    }

    public void setTrend_id_4(Long trend_id_4)
    {
        this.trend_id_4 = trend_id_4;
    }

    public Long getTrend_id_5()
    {
        return this.trend_id_5;
    }

    public void setTrend_id_5(Long trend_id_5)
    {
        this.trend_id_5 = trend_id_5;
    }

    public Long getTrend_id_6()
    {
        return this.trend_id_6;
    }

    public void setTrend_id_6(Long trend_id_6)
    {
        this.trend_id_6 = trend_id_6;
    }

    public Long getTrend_id_7()
    {
        return this.trend_id_7;
    }

    public void setTrend_id_7(Long trend_id_7)
    {
        this.trend_id_7 = trend_id_7;
    }

    public Long getTrend_id_8()
    {
        return this.trend_id_8;
    }

    public void setTrend_id_8(Long trend_id_8)
    {
        this.trend_id_8 = trend_id_8;
    }

    public Long getTrend_id_9()
    {
        return this.trend_id_9;
    }

    public void setTrend_id_9(Long trend_id_9)
    {
        this.trend_id_9 = trend_id_9;
    }

    public Long getTrend_id_10()
    {
        return this.trend_id_10;
    }

    public void setTrend_id_10(Long trend_id_10)
    {
        this.trend_id_10 = trend_id_10;
    }
}
