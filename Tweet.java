import TwitterAnalytics.TwitterApi;
import twitter4j.IDs;
import twitter4j.TwitterException;

public class Tweet {

    public void amplifiers(long status_id)
    {

        IDs ids;
        long cursor = -1;

        try {

            do {
                ids = TwitterApi.client().tweets().getRetweeterIds(status_id, cursor);

                for (long id : ids.getIDs()) {
                    //System.out.println(TwitterApi.client().users().showUser(id).getScreenName());
                }

            }while ((cursor = ids.getNextCursor()) != 0);

        } catch (TwitterException e) {

            e.printStackTrace();

        }
    }
}
