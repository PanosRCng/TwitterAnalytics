package Apps.Retweeters;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Retweeter;
import TwitterAnalytics.TwitterApi;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.api.TimelinesResources;

public class DataCollection {

    TimelinesResources timelinesResource = TwitterApi.client().timelines();

    public DataCollection()
    {

    }

    public void trackUserTimeLine(long userID){

        IDs retweeter_ids;

        try {

            ResponseList<Status> tweets = timelinesResource.getUserTimeline(userID);

            for(Status status : tweets){

                long retweeter_cursor = -1;

                do {

                    retweeter_ids = TwitterApi.client().tweets().getRetweeterIds(status.getId(), retweeter_cursor);

                    for (long retweeter_id : retweeter_ids.getIDs()) {

                        Retweeter retweeter = new Retweeter (retweeter_id, userID, new java.sql.Timestamp(status.getCreatedAt().getTime()), Boolean.FALSE);
                        Hibernate.save(retweeter);
                    }

                }while ((retweeter_cursor = retweeter_ids.getNextCursor()) != 0);
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
}
