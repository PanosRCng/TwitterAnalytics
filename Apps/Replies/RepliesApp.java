package Apps.Replies;

import Apps.GeneralFunctions;
import twitter4j.Paging;

public class RepliesApp {

    private Paging paging;
    private int pageno = 1;

    DataCollection collectionWorker = new DataCollection();

    GeneralFunctions generalFunctions = new GeneralFunctions();

    public RepliesApp(boolean useTimePeriod)
    {

        String screenName = "olympiacos_org";

        if(useTimePeriod){

            collectionWorker.collectionOverTime(generalFunctions, screenName);

        }else{

            while(true){

                generalFunctions.checkRateLimitAndDelay();

                paging = new Paging(pageno++, 1000);

                collectionWorker.collectionOverTime(screenName,paging);
                System.out.println("Pageno : "+pageno);
                if(pageno==1000) pageno=1;

            }

        }
    }
}
