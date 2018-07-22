package TwitterAnalytics.Models;


import java.util.ArrayList;
import java.util.HashSet;


public class Hashtag extends DBModel
{

    public String table = "hashtags";

    public boolean timestamps = false;

    public int trend_id;
    public String name;



    public Hashtag()
    {
        //
    }


    public Hashtag(String name, int trend_id)
    {
        this.name = name;
        this.trend_id = trend_id;
    }


    public static ArrayList<Hashtag> all()
    {
        ArrayList<Hashtag> hashtags = new ArrayList<Hashtag>();

        for(DBModel dbModel : new Hashtag().all_ids())
        {
            hashtags.add( (Hashtag) new Hashtag().get( dbModel.entry_id ) );
        }

        return hashtags;
    }

}
