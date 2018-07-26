import TwitterAnalytics.ConfigManager.Config;
import TwitterAnalytics.DB;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StreamTwitterUser {

    TwitterStream twitterStream;
    FilterQuery fq;
    long userID;

    public StreamTwitterUser(String userName) {

        System.out.println(userName);

        try {

            User user  = TwitterApi.client().showUser(userName);

            userID = user.getId();

        } catch (TwitterException e) {
            e.printStackTrace();
        }

        System.out.println(userID);

        this.fq = getFilter(userID);

        ConfigurationBuilder confBuilder = new ConfigurationBuilder();

        confBuilder.setDebugEnabled(true)
                .setOAuthConsumerKey(Config.twitter_api().CONSUMER_KEY)
                .setOAuthConsumerSecret(Config.twitter_api().CONSUMER_SECRET)
                .setOAuthAccessToken(Config.twitter_api().ACCESS_TOKEN)
                .setOAuthAccessTokenSecret(Config.twitter_api().ACCESS_TOKEN_SECRET);

        this.twitterStream = new TwitterStreamFactory(confBuilder.build()).getInstance();
        this.twitterStream.addListener(listener);
        this.twitterStream.filter(this.fq);
    }

    StatusListener listener = new StatusListener() {

        public void onStatus(Status status) {
            System.out.println(status.getText());

            try {
                String query = " insert into tempStatus (userID, statusID, date)"
                        + " values (?, ?, ?)";

                PreparedStatement preparedStmt = DB.conn().prepareStatement(query);

                preparedStmt.setLong    (1, status.getUser().getId());
                preparedStmt.setLong    (2, status.getId());
                preparedStmt.setDate    (3, new java.sql.Date(status.getCreatedAt().getTime()));

                preparedStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

        public void onTrackLimitationNotice(int i) {}

        public void onScrubGeo(long l, long l1) {}

        public void onStallWarning(StallWarning stallWarning) {}

        public void onException(Exception e) {e.printStackTrace();}

    };

    private static FilterQuery getFilter(long userID) {

        FilterQuery filtro = new FilterQuery();
        return filtro.follow(userID);
    }
}
