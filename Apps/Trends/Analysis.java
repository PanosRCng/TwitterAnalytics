package Apps.Trends;


import TwitterAnalytics.Hibernate;
import TwitterAnalytics.InvertedIndex.InvertedIndex;
import TwitterAnalytics.Models.Trend;
import TwitterAnalytics.Models.TrendSentiment;
import TwitterAnalytics.Models.TrendsList;
import TwitterAnalytics.Models.Tweet;
import TwitterAnalytics.Services.TrendService;
import TwitterAnalytics.Services.TrendsListService;
import TwitterAnalytics.TextAnalysis.Sentimenter.Sentimenter;
import TwitterAnalytics.TextAnalysis.Stemmer.Stemmer;
import TwitterAnalytics.TextAnalysis.Tokenizer.Tokenizer;
import TwitterAnalytics.TextAnalysis.Utils;

import java.util.*;


public class Analysis extends TimerTask
{

    private void trend_analysis()
    {
        Tokenizer tokenizer = new Tokenizer();

        Date fromDate = new Date(System.currentTimeMillis() - 30 * 60 * 1000);
        Date toDate = new Date(System.currentTimeMillis() - 15 * 60 * 1000);

        System.out.println(new Date().toString() + ": ANALYSIS FROM " + fromDate.toString() + " TO " + toDate.toString());

        for(Trend trend : this.getTrends(fromDate, toDate).values())
        {
            //System.out.println("----------------------- ------------------------------------");
            //System.out.println(trend.getName());

            Vector<Vector<Double>> t_matrix = new Vector<>();

            for(Tweet tweet : TrendService.tweetsByDateRange(trend.getId(), fromDate, toDate))
            {
                //System.out.println("-----------------------");
                //System.out.println(tweet.getText());

                Vector<String> tokens = tokenizer.tokenize(tweet.getCleanText());

                Vector<String> stems = Stemmer.stem( Utils.lowercase(tokens) );

                Vector<Double> t_vector = Sentimenter.sentimentVector( stems );

                if(t_vector == null)
                {
                    continue;
                }

                t_matrix.add( t_vector );
            }

            if(t_matrix.size() == 0)
            {
                continue;
            }

            Vector<Double> s = Sentimenter.trendVector(t_matrix);

            TrendSentiment trendSentiment = new TrendSentiment(trend.getId(), s.get(0), s.get(1), s.get(2), s.get(3), s.get(4), s.get(5));
            Hibernate.save(trendSentiment);

            //InvertedIndex invertedIndex = InvertedIndex.open(trend.getName());
            //for(String term : invertedIndex.topNerms(10))
            //{
            //    System.out.println(term);
            //}

        }

    }


    private Map<Long,Trend> getTrends(Date fromDate, Date toDate)
    {
        Map<Long,Trend> trends = new HashMap<>();

        for(TrendsList trendsList : TrendsListService.getByDateRange(fromDate, toDate))
        {
            for(Trend trend : trendsList.trends())
            {
                trends.put(trend.getId(), trend);
            }
        }

        return trends;
    }


    @Override
    public void run()
    {
        this.trend_analysis();
    }

}
