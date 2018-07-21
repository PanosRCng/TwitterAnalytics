package TwitterAnalytics.TextAnalysis.Sentimenter;


import java.util.Vector;

import TwitterAnalytics.TextAnalysis.Sentimenter.SentimentLexicon.Entry;
import TwitterAnalytics.TextAnalysis.Sentimenter.SentimentLexicon.SentimentLexicon;



public class Sentimenter
{

    private SentimentLexicon sentimentLexicon;



    public Sentimenter()
    {
        this.sentimentLexicon = SentimentLexicon.load();
    }



    private static class SingletonHelper
    {
        private static final Sentimenter INSTANCE = new Sentimenter();
    }


    public static Vector<Double> sentimentVector(Vector<String> tokens)
    {
        Vector<String> found_tokens = new Vector<>();

        for(String token : tokens)
        {
            if( SingletonHelper.INSTANCE.sentimentLexicon.entry(token) != null )
            {
                found_tokens.add(token);
            }
        }

        if(found_tokens.size() == 0)
        {
            return null;
        }

        return SingletonHelper.INSTANCE.t_vector( found_tokens );
    }


    public static Vector<Double> trendVector(Vector<Vector<Double>> t_matrix)
    {
        Vector<Double> h_vector = new Vector<>();

        for(int i=0; i<6; i++)
        {
            Vector<Double> parts = new Vector<>();

            for(Vector<Double> t_vector : t_matrix)
            {
                parts.add( t_vector.get(i) );
            }

            h_vector.add( SingletonHelper.INSTANCE.quadtratic_mean(parts) );
        }

        return h_vector;
    }


    private Vector<Double> t_vector(Vector<String> tokens)
    {
        Vector<Double> t_vector = new Vector<>();

        Vector<Vector<Double>> w_matrix = this.w_matrix(tokens);

        for(int i=0; i<6; i++)
        {
            Vector<Double> parts = new Vector<>();

            for(Vector<Double> w_vector : w_matrix)
            {
                parts.add( w_vector.get(i) );
            }

            t_vector.add( this.quadtratic_mean(parts) );
        }

        return t_vector;
    }


    private Vector<Vector<Double>> w_matrix(Vector<String> tokens)
    {
        Vector<Vector<Double>> matrix = new Vector<>();

        for(String token : tokens)
        {
            matrix.add(this.w_vector(token));
        }

        return matrix;
    }


    private Vector<Double> w_vector(String token)
    {
        Vector<Double> vector = new Vector<>();

        Entry entry = this.sentimentLexicon.entry(token);

        vector.add( entry.anger() );
        vector.add( entry.disgust() );
        vector.add( entry.fear() );
        vector.add( entry.happiness() );
        vector.add( entry.sadness() );
        vector.add( entry.surpise() );

        return vector;
    }


    private Double quadtratic_mean(Vector<Double> numbers)
    {
        Double sum = 0.0;

        for(Double number : numbers)
        {
            sum += Math.pow(number, 2);
        }

        return Math.sqrt( (sum / numbers.size()) );
    }

}
