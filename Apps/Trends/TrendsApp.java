package Apps.Trends;


import java.util.Timer;



public class TrendsApp
{
    private Timer timer;

    private final static int DELAY = 0;
    private final static int PERIOD = 15;


    public TrendsApp()
    {
        timer = new Timer();

        Clean cleanWorker = new Clean();
        Analysis analysisWorker = new Analysis();
        Collection collectionWorker = new Collection();

        timer.schedule(cleanWorker, DELAY * 60 * 1000, PERIOD * 60 * 1000);
        timer.schedule(analysisWorker, DELAY * 60 * 1000, PERIOD * 60 * 1000);
        timer.schedule(collectionWorker, DELAY * 60 * 1000, PERIOD * 60 * 1000);
    }

}
