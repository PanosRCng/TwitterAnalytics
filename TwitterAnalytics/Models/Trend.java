package TwitterAnalytics.Models;


import java.util.ArrayList;

public class Trend extends DBModel
{

    public String table = "trends";

    public boolean timestamps = true;


    public String name;
    public String query;



    public Trend()
    {
        //
    }


    public Trend(String name, String query)
    {
        this.name = name;
        this.query = query;
    }


    public ArrayList<Tweet> tweets()
    {
        ArrayList<Tweet> tweets = new ArrayList<>();

        for(DBModel dbModel : new Tweet().all_ids("trend_id=" + this.entry_id))
        {
            tweets.add( (Tweet) new Tweet().get( dbModel.entry_id ) );
        }

        return tweets;
    }


    public ArrayList<Hashtag> hashtags()
    {
        ArrayList<Hashtag> hashtags = new ArrayList<>();

        for(DBModel dbModel : new Hashtag().all_ids("trend_id=" + this.entry_id))
        {
            hashtags.add( (Hashtag) new Hashtag().get( dbModel.entry_id ) );
        }

        return hashtags;
    }



    public static ArrayList<Trend> all()
    {
        ArrayList<Trend> trends = new ArrayList<>();

        for(DBModel dbModel : new Trend().all_ids())
        {
            trends.add( (Trend) new Trend().get( dbModel.entry_id ) );
        }

        return trends;
    }

}
