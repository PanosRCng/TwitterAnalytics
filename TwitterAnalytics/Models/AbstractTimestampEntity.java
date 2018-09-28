package TwitterAnalytics.Models;


import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;


@MappedSuperclass
public class AbstractTimestampEntity
{

    private Date created_at;
    private Date updated_at;


    @PrePersist
    protected void onCreate()
    {
        this.created_at = new Date();
        this.updated_at = new Date();
    }

    @PreUpdate
    protected void onUpdate()
    {
        this.updated_at = new Date();
    }

    public Date getCreated_at()
    {
        return this.created_at;
    }

    public Date getUpdated_at()
    {
        return this.updated_at;
    }

    public void setCreated_at(Date created_at)
    {
        this.created_at = created_at;
    }

    public void setUpdated_at(Date updated_at)
    {
        this.updated_at = updated_at;
    }
}
