package Apps.Retweeters;

import Apps.GeneralFunctions;
import twitter4j.Paging;

import static TwitterAnalytics.Services.RetweeterService.getRetweeters;

public class RetweetersApp {

    DataCollection collectionWorker = new DataCollection();

    public RetweetersApp() {

        for(long retweeter_id : getRetweeters()){

            GeneralFunctions generalFunctions = new GeneralFunctions();
            boolean checkLimit = generalFunctions.checkRateLimit();
            if(checkLimit) {
                try {
                    Thread.sleep(900000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            collectionWorker.trackUserTimeLine(retweeter_id);

        }
    }
}
