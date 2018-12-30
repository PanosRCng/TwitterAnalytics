package Apps.Retweeters;

import Apps.GeneralFunctions;
import twitter4j.Paging;

import static TwitterAnalytics.Services.RetweeterService.getRetweeters;

public class RetweetersApp {

    GeneralFunctions generalFunctions = new GeneralFunctions();

    DataCollection collectionWorker = new DataCollection();

    public RetweetersApp(boolean useTimePeriod) {

        for(long retweeter_id : getRetweeters(Boolean.TRUE)){

            generalFunctions.checkRateLimitAndDelay();

            collectionWorker.trackUserTimeLine(retweeter_id, useTimePeriod);

        }
    }
}
