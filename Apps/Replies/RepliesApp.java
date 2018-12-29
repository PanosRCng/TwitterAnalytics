package Apps.Replies;

import Apps.GeneralFunctions;
import twitter4j.Paging;

public class RepliesApp {

    private Paging paging;
    private int pageno = 1;

    DataCollection collectionWorker = new DataCollection();

    GeneralFunctions generalFunctions = new GeneralFunctions();

    public RepliesApp()
    {

        while(true){

            generalFunctions.checkRateLimitAndDelay();

            paging = new Paging(pageno++, 1000);

            collectionWorker.collectionOverTime("olympiacos_org",paging);
            System.out.println("Pageno : "+pageno);
            if(pageno==1000) pageno=1;

        }
    }
}
