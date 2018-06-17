package TwitterAnalytics.Models;


import java.sql.Timestamp;


public class Tweet extends DBModel
{

    public String table = "tweets";
    public int entry_id;

    public String created_at;
    public String updated_at;
    public boolean timestamps = false;


    public String text;
    public long id;
    public int trend_id;
    public Timestamp timestamp;



    public Tweet()
    {
        //
    }


    public Tweet(String text, long id, int trend_id, Timestamp timestamp)
    {
        this.text = text;
        this.id = id;
        this.trend_id = trend_id;
        this.timestamp = timestamp;
    }
}
