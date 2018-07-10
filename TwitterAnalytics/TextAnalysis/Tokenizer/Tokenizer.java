package TwitterAnalytics.TextAnalysis.Tokenizer;


import TwitterAnalytics.TextAnalysis.Utils;


import java.util.Vector;



public class Tokenizer
{
    private Settings settings;



    public Tokenizer()
    {
        this.settings = new Settings();
    }



    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }


    public Vector<String> tokenize(String text)
    {
        text = Utils.removeWordDividers(text);

        if( this.settings.all_uppercase() )
        {
            text = Utils.uppercase(text);
        }
        else if( this.settings.all_lowercase() )
        {
            text = Utils.lowercase(text);
        }

        if( this.settings.removePunctuations() )
        {
            text = Utils.removePunctuations(text);
        }

        if( this.settings.removeSymbols() )
        {
            text = Utils.removeSymbols(text);
        }

        if( this.settings.removeIntonations() )
        {
            text = Utils.removeIntonations(text);
        }

        Vector<String> tokens = this.getTokens(text, " ");

        if( this.settings.removeNumbers() )
        {
            tokens = Utils.removeNumbers(tokens);
        }

        if( this.settings.removeStopwords() )
        {
            tokens = Utils.removeStopwords(tokens);
        }

        if( this.settings.removeMinLength() )
        {
            tokens = Utils.removeMinLength(tokens, 2);
        }

        return tokens;
    }


    private Vector<String> getTokens(String text, String delimeter)
    {
        Vector<String> tokens = new Vector<>();

        for(String part : text.split(delimeter))
        {
            if(part.length() == 0)
            {
                continue;
            }

            tokens.add(part);
        }

        return tokens;
    }

}
