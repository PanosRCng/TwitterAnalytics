package Apps;

import TwitterAnalytics.TwitterApi;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.api.TimelinesResources;

import java.text.SimpleDateFormat;
import java.util.Date;

import static TwitterAnalytics.Services.RetweeterService.getRetweeters;

public class FetchDataOverTime {

    GeneralFunctions generalFunctions = new GeneralFunctions();
    TimelinesResources timelinesResource = TwitterApi.client().timelines();

    long userID=-1;
    private Paging paging;

    public FetchDataOverTime() {

        int mode = 1;

        String screenName = "olympiacos_org";

        boolean useTimePeriod = Boolean.TRUE;
        // 1 week
        String since = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()-7*24*60*60*1000));
        // today
        String until = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        ResponseList<User> users = null;
        try {
            users = TwitterApi.client().searchUsers(screenName, -1);

            for (User user : users) {
                userID = user.getId();
                break;
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }

        System.out.println("olympiacos_org : " + userID);

        Apps.Retweets.DataCollection collectionWorkerRetweets = new Apps.Retweets.DataCollection();
        Apps.Retweeters.DataCollection collectionWorkerRetweeters = new Apps.Retweeters.DataCollection();
        Apps.Replies.DataCollection collectionWorkerReplies = new Apps.Replies.DataCollection();

        while(true){

            generalFunctions.checkRateLimitAndDelay();
            paging = new Paging(1, 100);

            // Retweets
            if(mode==1){

                if(useTimePeriod){
                    collectionWorkerRetweets.trackUserTimeLine(generalFunctions, screenName, since, until, Boolean.TRUE);
                }else{
                    collectionWorkerRetweets.trackUserTimeLine(userID, paging, timelinesResource, Boolean.TRUE);
                }

                mode = 2;

                System.out.println("all ok : 1");

            // Retweeters
            }else if(mode==2){

                for(long retweeter_id : getRetweeters(Boolean.TRUE)){
                    collectionWorkerRetweeters.trackUserTimeLine(retweeter_id, Boolean.TRUE);
                }

                mode = 3;

                System.out.println("all ok : 2");

            // Replies
            }else{

                if(useTimePeriod){
                    collectionWorkerReplies.collectionOverTime(generalFunctions, screenName);
                }else{
                    collectionWorkerReplies.collectionOverTime(screenName,paging);
                }

                mode = 1;

                System.out.println("all ok : 3");

            }

        }
    }
}
