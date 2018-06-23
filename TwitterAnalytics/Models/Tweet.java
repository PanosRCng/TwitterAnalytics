package TwitterAnalytics.Models;


import java.sql.Timestamp;
import java.util.ArrayList;


public class Tweet extends DBModel
{

    public String table = "tweets";

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


    public static ArrayList<Tweet> all()
    {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for(DBModel dbModel : new Tweet().all_ids())
        {
            tweets.add( (Tweet) new Tweet().get( dbModel.entry_id ) );
        }

        return tweets;
    }
}
