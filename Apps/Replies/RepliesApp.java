package Apps.Replies;

import Apps.GeneralFunctions;
import twitter4j.Paging;
import twitter4j.Status;

import java.util.HashMap;
import java.util.Map;

public class RepliesApp {

    private Paging paging;
    private int pageno = 1;

    private Map<Status,Long> tweets = new HashMap<Status,Long>();
    private Map<Status,Integer> repliesCounter = new HashMap<Status,Integer>();

    DataCollection collectionWorker = new DataCollection();

    public RepliesApp()
    {

        while(true){

            GeneralFunctions generalFunctions = new GeneralFunctions();
            boolean checkLimit = generalFunctions.checkRateLimit();
            if(checkLimit) {
                try {
                    Thread.sleep(900000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            paging = new Paging(pageno++, 1000);

            collectionWorker.collectionOverTime("olympiacos_org",repliesCounter,paging, tweets);
            System.out.println("Pageno : "+pageno);
            if(pageno==1000) pageno=1;

        }
    }
}
