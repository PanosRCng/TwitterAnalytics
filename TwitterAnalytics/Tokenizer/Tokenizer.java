package TwitterAnalytics.Tokenizer;


import java.util.Vector;

public class Tokenizer
{
    public static final String PUNCTUATIONS_FILE = "punctuations.txt";
    public static final String SYMBOLS_FILE = "symbols.txt";

    private Settings settings;

    private Vector<String> punctuations;
    private Vector<String> symbols;


    public Tokenizer()
    {
        this.settings = new Settings();

        this.setup();
    }


    public void setSettings(Settings settings)
    {
        this.settings = settings;
    }


    public Vector<String> tokenize(String text)
    {
        if( this.settings.removePunctuations() )
        {
            text = this.removePunctuations(text);
        }

        if( this.settings.removeSymbols() )
        {
            text = this.removeSymbols(text);
        }



        System.out.println(text);

        /*
        Vector<String> tokens = this.getTokens(text, " ");

        for(String token : tokens)
        {
            System.out.println("|" + token + "|");
        }

        return tokens;
        */

        return null;
    }



    private Vector<String> getTokens(String text, String delimeter)
    {
        Vector<String> tokens = new Vector<>();

        for(String part : text.split(delimeter))
        {
            tokens.add(part);
        }

        return tokens;
    }


    private String removePunctuations(String text)
    {
        for(String punctuation : this.punctuations)
        {
            if(text.contains(punctuation))
            {
                text = text.replace(punctuation, " ");
            }
        }

        return text;
    }


    private String removeSymbols(String text)
    {
        for(String symbol : this.symbols)
        {
            if(text.contains(symbol))
            {
                text = text.replace(symbol, " ");
            }
        }

        return text;
    }


    private void setup()
    {
        if(this.settings.removePunctuations())
        {
            this.punctuations = IO.readFile(PUNCTUATIONS_FILE);
        }

        if(this.settings.removeSymbols())
        {
            this.symbols = IO.readFile(SYMBOLS_FILE);
        }
    }
}
