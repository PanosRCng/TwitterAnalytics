import TwitterAnalytics.TwitterApi;
import twitter4j.*;

public class StreamTestApp implements StatusListener
{

    public StreamTestApp()
    {
        TwitterStream stream = TwitterApi.getStream();

        stream.addListener(this);

        FilterQuery tweetFilterQuery = new FilterQuery();

        tweetFilterQuery.track(new String[]{"test"});

        stream.filter(tweetFilterQuery);
    }


    @Override
    public void onStatus(Status status)
    {
        System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText());
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice)
    {
        System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses)
    {
        System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId)
    {
        System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning)
    {
        System.out.println("Got stall warning:" + warning);
    }

    @Override
    public void onException(Exception ex)
    {
        ex.printStackTrace();
    }
}
