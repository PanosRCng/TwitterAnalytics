import TwitterAnalytics.DB;
import TwitterAnalytics.TwitterApi;
import twitter4j.*;
import twitter4j.api.TimelinesResources;
import twitter4j.api.UsersResources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TwitterUser {

    public void findUsers(long userID)
    {

        TwitterUser twitterUser = new TwitterUser();

        try
        {
            UsersResources userResource = TwitterApi.client().users();

            ResponseList<User> users = userResource.lookupUsers(userID);

            for(User user : users)
            {
                System.out.println(user.getName());

                //this.userStats(user, false);
                //this.userStats(user, true);

                twitterUser.printUserStatsByDate(user);

                //this.userTimeline(user.getId());
            }
        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }
    }

    public void getAmplifiersStats(long user_id)
    {
        try
        {
            TimelinesResources timelinesResource = TwitterApi.client().timelines();

            ResponseList<Status> statuses = timelinesResource.getUserTimeline(user_id);

            Tweet tweet = new Tweet();

            for(Status status : statuses)
            {
                //System.out.println(status.getText());

                tweet.amplifiers(status.getId());
            }

        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }

    }

    public void printUserStatsByDate(User user){

        String query = " select * from userStats where userID=? order by submissionDate";

        PreparedStatement preparedStmt = null;
        try {
            preparedStmt = DB.conn().prepareStatement(query);

            preparedStmt.setInt(1, (int) user.getId());

            ResultSet rs = preparedStmt.executeQuery();

            while (rs.next())
            {
                int numStatuses = rs.getInt("numStatus");
                int numFriends = rs.getInt("numFriends");
                int numFavourites = rs.getInt("numFavourite");
                int numFollowers = rs.getInt("numFollowers");
                Date dateSubmitted = rs.getDate("submissionDate");
                // print the results
                System.out.format("Date : %s, Number of Statuses : %s, Number of friends : %s, " +
                                "Number of Favourites : %s, Number of Followers : %s\n",
                        dateSubmitted, numStatuses, numFriends, numFavourites, numFollowers);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void userTimeline(long user_id)
    {
        try
        {
            TimelinesResources timelinesResource = TwitterApi.client().timelines();

            ResponseList<Status> statuses = timelinesResource.getUserTimeline(user_id);

            for(Status status : statuses)
            {
                System.out.println(status.getText());
            }
        }
        catch(TwitterException twitterException)
        {
            twitterException.printStackTrace();
            System.out.println("Failed : " + twitterException.getMessage());
        }
    }

    public void userStats(User user, boolean insertInTable){

        System.out.println("Number of statuses : " + user.getStatusesCount());
        System.out.println("Number of favourites : " + user.getFavouritesCount());
        System.out.println("Number of followers : " + user.getFollowersCount());
        System.out.println("Number of friends : " + user.getFriendsCount());

        if(insertInTable){

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final String stringDate= dateFormat.format(new Date());
            final java.sql.Date sqlDate=  java.sql.Date.valueOf(stringDate);

            String query = " insert into userStats (userID, numStatus, numFavourite, numFollowers, numFriends, submissionDate)"
                    + " values (?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStmt = null;
            try {
                preparedStmt = DB.conn().prepareStatement(query);

                preparedStmt.setInt    (1, (int) user.getId());
                preparedStmt.setInt    (2, user.getStatusesCount());
                preparedStmt.setInt    (3, user.getFavouritesCount());
                preparedStmt.setInt    (4, user.getFollowersCount());
                preparedStmt.setInt    (5, user.getFriendsCount());
                preparedStmt.setDate   (6, sqlDate);

                // execute the preparedstatement
                preparedStmt.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

    }

    public void printGraphUserTweets(long userID){

        String stringUrl = "http://localhost:8888/userTweetGraphEdges/users?id="+userID;

        URL url;
        TwitterUser twitterUser = new TwitterUser();

        try {
            url = new URL(stringUrl);
            URLConnection uc;
            uc = url.openConnection();

            uc.setRequestProperty("X-Requested-With", "Curl");

            String userpass = "admin" + ":" + "admin";
            String basicAuth = "Basic " + new String(userpass.getBytes());
            uc.setRequestProperty("Authorization", basicAuth);

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));

            JSONObject obj;
            String line = null;
            while ((line = reader.readLine()) != null) {

                String[] splited = line.replaceAll("\\p{P}","").split("\\s+");

                for(String tweet : splited){
                    if(tweet.isEmpty()) continue;
                    try {

                        ResponseList<Status> tweets = TwitterApi.client().tweets().lookup(Long.parseLong(tweet));

                        for(Status tweetUser : tweets){
                            System.out.println(tweetUser.getText());
                        }
                    } catch (TwitterException e) {
                        e.printStackTrace();
                    }
                }
//                try {
//                    obj = new JSONObject(line);
//                    long id_str = Long.parseLong(obj.getString("id_str"));
//                    System.out.println(id_str);
//                    twitterUser.findUsers(id_str);
//                    int cnt = Integer.parseInt(obj.getString("cnt"));
//                    System.out.println("Count: "+cnt);
//                } catch (JSONException e) {
//                    //e.printStackTrace();
//                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
