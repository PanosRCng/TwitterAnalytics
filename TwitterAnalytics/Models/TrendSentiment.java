package TwitterAnalytics.Models;



public class TrendSentiment extends DBModel
{

    public String table = "trend_sentiments";

    public boolean timestamps = false;

    public int trend_id;
    public double anger;
    public double disgust;
    public double fear;
    public double happiness;
    public double sadness;
    public double surprise;



    public TrendSentiment()
    {
        //
    }


    public TrendSentiment(int trend_id, double anger, double disgust, double fear, double happiness, double sadness, double surprise)
    {
        this.trend_id = trend_id;
        this.anger = anger;
        this.disgust = disgust;
        this.fear = fear;
        this.happiness = happiness;
        this.sadness = sadness;
        this.surprise = surprise;
    }

}
