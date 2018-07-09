package TwitterAnalytics.TextAnalysis.Stemmer;

import org.apache.lucene.analysis.el.GreekStemmer;


public class Stemmer
{

    private GreekStemmer greekStemmer;


    public Stemmer()
    {
        this.greekStemmer = new GreekStemmer();
    }


    public String stem(String token)
    {
        char[] chars = token.toCharArray();

        int stemmed_length = this.greekStemmer.stem(chars, chars.length);

        return token.substring(0, stemmed_length);
    }

}
