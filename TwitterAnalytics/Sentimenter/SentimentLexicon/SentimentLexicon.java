package TwitterAnalytics.Sentimenter.SentimentLexicon;


import java.util.Vector;
import java.util.HashMap;
import TwitterAnalytics.Sentimenter.IO;


public class SentimentLexicon
{
    public static final String SENTIMENT_LEXICON_FILE = "sentiment_lexicon.tsv";

    private HashMap<String, Entry> lexicon;



    public SentimentLexicon(HashMap<String, Entry> lexicon)
    {
        this.lexicon = lexicon;
    }



    public static SentimentLexicon load()
    {
        HashMap<String, Entry> lexicon = new HashMap<>();

        for(Vector<String> entry_parts : IO.readFile(SENTIMENT_LEXICON_FILE))
        {
            Entry entry = Entry.create(entry_parts);

            lexicon.put(entry.term, entry);
        }

        return new SentimentLexicon(lexicon);
    }

}
