package Apps.Retweeters;

import TwitterAnalytics.Hibernate;
import TwitterAnalytics.Models.Retweeter;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TimelinesResources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataCollection {

    TimelinesResources timelinesResource = TwitterApi.client().timelines();

    public DataCollection()
    {

    }

    public void trackUserTimeLine(long userID, boolean useTimePeriod){

        IDs retweeter_ids;

        if(useTimePeriod){

            // 1 week
            String since = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()-7*24*60*60*1000));
            // today
            String until = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            try {

                User user = TwitterApi.client().showUser(userID);

                Query query = new Query("from:"+user.getScreenName() + " since:" + since + " until:" + until);

                QueryResult results;

                do {

                    results = TwitterApi.client().search(query);

                    List<Status> tweets = results.getTweets();

                    for (Status status : tweets) {

                        long retweeter_cursor = -1;

                        do {

                            retweeter_ids = TwitterApi.client().tweets().getRetweeterIds(status.getId(), retweeter_cursor);

                            for (long retweeter_id : retweeter_ids.getIDs()) {

                                Retweeter retweeter = new Retweeter(retweeter_id, userID, new java.sql.Timestamp(status.getCreatedAt().getTime()), Boolean.FALSE);
                                Hibernate.save(retweeter);
                            }

                        } while ((retweeter_cursor = retweeter_ids.getNextCursor()) != 0);

                    }

                } while ((query = results.nextQuery()) != null);

            }catch (Exception e) {
                e.printStackTrace();
            }

        }else {

            try {

                ResponseList<Status> tweets = timelinesResource.getUserTimeline(userID);

                for (Status status : tweets) {

                    long retweeter_cursor = -1;

                    do {

                        retweeter_ids = TwitterApi.client().tweets().getRetweeterIds(status.getId(), retweeter_cursor);

                        for (long retweeter_id : retweeter_ids.getIDs()) {

                            Retweeter retweeter = new Retweeter(retweeter_id, userID, new java.sql.Timestamp(status.getCreatedAt().getTime()), Boolean.FALSE);
                            Hibernate.save(retweeter);
                        }

                    } while ((retweeter_cursor = retweeter_ids.getNextCursor()) != 0);
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }

        }
    }
}
