package Apps.Retweets;

import Apps.GeneralFunctions;
import TwitterAnalytics.TwitterApi;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.api.TimelinesResources;

public class RetweetsApp {

    private Paging paging;
    private int pageno = 1;
    long userID=-1;

    DataCollection collectionWorker = new DataCollection();

    TimelinesResources timelinesResource = TwitterApi.client().timelines();

    public RetweetsApp() {

        ResponseList<User> users = null;
        try {
            users = TwitterApi.client().searchUsers("olympiacos_org", -1);

            for (User user : users) {
                userID = user.getId();
                break;
            }

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

                collectionWorker.trackUserTimeLine(userID, paging, timelinesResource);

                System.out.println("Pageno : "+pageno);
                if(pageno==1000) pageno=1;
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }
}
