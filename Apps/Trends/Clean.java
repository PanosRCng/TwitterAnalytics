package Apps.Trends;


import TwitterAnalytics.Services.TrendsListService;
import TwitterAnalytics.Services.TweetService;
import TwitterAnalytics.Services.TrendService;
import TwitterAnalytics.Services.HashtagService;
import TwitterAnalytics.Services.TrendSentimentService;

import java.util.*;



public class Clean extends TimerTask
{

    private void clean()
    {
        Date fromDate = new Date(System.currentTimeMillis() - 120 * 60 * 1000);
        Date toDate = new Date(System.currentTimeMillis() - 60 * 60 * 1000);

        System.out.println(new Date().toString() + ": CLEAN FROM " + fromDate.toString() + " TO " + toDate.toString());

        int trendsDeleted = TrendService.removeTrendsByDateRange(fromDate, toDate).size();
        int trendsListsDeleted = TrendsListService.removeTrendsListsByDateRange(fromDate, toDate).size();
        int tweetsDeleted = TweetService.removeTweetsByDateRange(fromDate, toDate).size();
        int hashtagsDeleted = HashtagService.removeHashtagsByDateRange(fromDate, toDate).size();
        int trendsSentimentsDeleted = TrendSentimentService.removeTrendSentimentByDateRange(fromDate, toDate).size();

        System.out.println("deleted trends: " + trendsDeleted);
        System.out.println("deleted trendsLists: " + trendsListsDeleted);
        System.out.println("deleted tweets: " + tweetsDeleted);
        System.out.println("deleted hashtags: " + hashtagsDeleted);
        System.out.println("deleted trendsSentiments: " + trendsSentimentsDeleted);
    }


    @Override
    public void run()
    {
        this.clean();
    }
}
