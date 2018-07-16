package TwitterAnalytics.TextAnalysis.Sentimenter.SentimentLexicon;


import java.util.Vector;
import java.util.HashMap;
import TwitterAnalytics.TextAnalysis.Sentimenter.IO;
import TwitterAnalytics.TextAnalysis.Stemmer.Stemmer;
import TwitterAnalytics.TextAnalysis.Utils;


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

            entry.term = SentimentLexicon.clean_term(entry.term);

            entry.term = Utils.removeIntonations(entry.term);
            entry.term = Utils.removePunctuations(entry.term);

            entry.term = Utils.lowercase(entry.term);
            entry.term = Stemmer.stem(entry.term);

            lexicon.put(entry.term, entry);
        }

        return new SentimentLexicon(lexicon);
    }


    public Entry entry(String token)
    {
        return this.lexicon.get(token);
    }


    private static String clean_term(String term)
    {
        String clean_term = "";

        clean_term = term.split("\\s+")[0];

        clean_term = clean_term.replaceAll("-", "");

        return clean_term;
    }

}
