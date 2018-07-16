package TwitterAnalytics.TextAnalysis.Sentimenter;


import java.util.HashMap;
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


    public Vector<Double> t_vector(Vector<String> tokens)
    {
        Vector<Double> t_vector = new Vector<>();

        HashMap<String,Vector<Double>> w_matrix = this.w_matrix(tokens);



        return t_vector;
    }


    public HashMap<String,Vector<Double>> w_matrix(Vector<String> tokens)
    {
        HashMap<String,Vector<Double>> matrix = new HashMap<String, Vector<Double>>();

        for(String token : tokens)
        {
            matrix.put(token, this.w_vector(token));
        }

        return matrix;
    }


    public Vector<Double> w_vector(String token)
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

}
