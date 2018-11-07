package Apps.Replies;

import Apps.GeneralFunctions;
import twitter4j.Paging;

public class RepliesApp {

    private Paging paging;
    private int pageno = 1;

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

            collectionWorker.collectionOverTime("olympiacos_org",paging);
            System.out.println("Pageno : "+pageno);
            if(pageno==1000) pageno=1;

        }
    }
}
