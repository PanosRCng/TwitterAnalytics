package Apps.Retweets;

import Apps.GeneralFunctions;
import TwitterAnalytics.TwitterApi;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.api.TimelinesResources;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RetweetsApp {

    private Paging paging;
    private int pageno = 1;
    long userID=-1;

    DataCollection collectionWorker = new DataCollection();

    TimelinesResources timelinesResource = TwitterApi.client().timelines();

    GeneralFunctions generalFunctions = new GeneralFunctions();

    public RetweetsApp(boolean storeRetweeters, boolean useTimePeriod) {

        ResponseList<User> users = null;
        String screenName = "olympiacos_org";

        try {
            users = TwitterApi.client().searchUsers(screenName, -1);

            for (User user : users) {
                userID = user.getId();
                break;
            }

            if(useTimePeriod) {

                // 1 week
                String since = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()-7*24*60*60*1000));
                // today
                String until = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                collectionWorker.trackUserTimeLine(generalFunctions, screenName, since, until, storeRetweeters);

            }else {

                while(true){

                    generalFunctions.checkRateLimitAndDelay();

                    paging = new Paging(pageno++, 1000);

                    collectionWorker.trackUserTimeLine(userID, paging, timelinesResource, storeRetweeters);

                    System.out.println("Pageno : "+pageno);
                    if(pageno==1000) pageno=1;
                }

            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }
}
