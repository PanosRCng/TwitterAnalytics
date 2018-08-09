package TwitterAnalytics.InvertedIndex;



import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;



public class SimpleAnalyzer extends Analyzer
{


    @Override
    protected TokenStreamComponents createComponents(String fieldName)
    {
        return new TokenStreamComponents(new WhitespaceTokenizer());
    }


}
