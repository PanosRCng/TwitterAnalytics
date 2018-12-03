package TwitterAnalytics.Models;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "hubretweeters", uniqueConstraints = {@UniqueConstraint(columnNames = {"name","created_at","updated_at"})})
public class HubRetweeter extends AbstractTimestampEntity implements Serializable {

    public HubRetweeter(String name, String screenName, Double score, Integer numberOfEdges, String location) {
        this.name = name;
        this.screenName = screenName;
        this.score = score;
        this.numberOfEdges = numberOfEdges;
        this.location = location;
    }

    @Id
    @GeneratedValue(generator="increment")
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "screenName")
    private String screenName;

    @Column(name = "score")
    private Double score;

    @Column(name = "numberOfEdges")
    private Integer numberOfEdges;

    @Column(name = "location")
    private String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getNumberOfEdges() {
        return numberOfEdges;
    }

    public void setNumberOfEdges(Integer numberOfEdges) {
        this.numberOfEdges = numberOfEdges;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
