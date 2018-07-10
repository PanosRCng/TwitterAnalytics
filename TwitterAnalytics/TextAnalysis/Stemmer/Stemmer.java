package TwitterAnalytics.TextAnalysis.Stemmer;


import org.apache.lucene.analysis.el.GreekStemmer;


public class Stemmer
{

    private GreekStemmer greekStemmer;


    private Stemmer()
    {
        this.greekStemmer = new GreekStemmer();
    }



    private static class SingletonHelper
    {
        private static final Stemmer INSTANCE = new Stemmer();
    }



    public static String stem(String token)
    {
        char[] chars = token.toCharArray();

        int stemmed_length = SingletonHelper.INSTANCE.greekStemmer.stem(chars, chars.length);

        return token.substring(0, stemmed_length);
    }

}
