package TwitterAnalytics.Models;


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

}
