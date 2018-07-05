package TwitterAnalytics.Tokenizer;


import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Tokenizer
{
    public static final String STOPWORDS_FILE = "stopwords_gr_nlp.txt";
    public static final String PUNCTUATIONS_FILE = "punctuations.txt";
    public static final String SYMBOLS_FILE = "symbols.txt";
    public static final String INTONATIONS_FILE = "intonations.txt";
    public static final String LOWERCASE_TO_UPPERCASE_FILE = "lowercase_to_uppercase.txt";
    public static final String[] WORD_DIVIDERS = {"\t", "\r", "\n"};

    private Settings settings;

    private Vector<String> stopwords;
    private Vector<String> punctuations;
    private Vector<String> symbols;
    private HashMap<String, String> intonationsMap;
    private HashMap<String, String> lowercaseToUppercaseMap;


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
        text = this.removeWordDividers(text);

        if( this.settings.removePunctuations() )
        {
            text = this.removePunctuations(text);
        }

        if( this.settings.removeSymbols() )
        {
            text = this.removeSymbols(text);
        }

        if( this.settings.removeIntonations() )
        {
            text = this.removeIntonations(text);
        }

        if( this.settings.all_uppercase() )
        {
            text = this.uppercase(text);
        }
        else if( this.settings.all_lowercase() )
        {
            text = this.lowercase(text);
        }

        Vector<String> tokens = this.getTokens(text, " ");

        if( this.settings.removeNumbers() )
        {
            tokens = this.removeNumbers(tokens);
        }

        if( this.settings.removeStopwords() )
        {
            tokens = this.removeStopwords(tokens);
        }

        if( this.settings.removeMinLength() )
        {
            tokens = this.removeMinLength(tokens, 2);
        }

        System.out.println(text);

        for(String token : tokens)
        {
            System.out.println("|" + token + "|");
        }

        //return tokens;


        return null;
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


    private String removeWordDividers(String text)
    {
        for(String wordDivider : WORD_DIVIDERS)
        {
            if(text.contains(wordDivider))
            {
                text = text.replace(wordDivider, " ");
            }
        }

        return text;
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


    private String removeIntonations(String text)
    {
        for(Map.Entry<String,String> innotation : this.intonationsMap.entrySet())
        {
            String innotated = innotation.getKey();
            String non_innotated = innotation.getValue();

            if(text.contains(innotated))
            {
                text = text.replace(innotated, non_innotated);
            }
        }

        return text;
    }


    private String uppercase(String text)
    {
        for(Map.Entry<String,String> lowercase_to_uppercase : this.lowercaseToUppercaseMap.entrySet())
        {
            String lowercase = lowercase_to_uppercase.getKey();
            String uppercase = lowercase_to_uppercase.getValue();

            if(text.contains(lowercase))
            {
                text = text.replace(lowercase, uppercase);
            }
        }

        return text;
    }


    private String lowercase(String text)
    {
        for(Map.Entry<String,String> lowercase_to_uppercase : this.lowercaseToUppercaseMap.entrySet())
        {
            String lowercase = lowercase_to_uppercase.getKey();
            String uppercase = lowercase_to_uppercase.getValue();

            if(text.contains(uppercase))
            {
                text = text.replace(uppercase, lowercase);
            }
        }

        return text;
    }


    private Vector<String> removeStopwords(Vector<String> tokens)
    {
        Vector<String> new_tokens = new Vector<>();

        for(String token : tokens)
        {
            boolean found = false;

            for(String stopword : this.stopwords)
            {
                if(token.equals(stopword))
                {
                    found = true;
                }
            }

            if(!found)
            {
                new_tokens.add(token);
            }
        }

        return new_tokens;
    }


    private Vector<String> removeNumbers(Vector<String> tokens)
    {
        Vector<String> new_tokens = new Vector<>();

        for(String token : tokens)
        {
            token = token.replaceAll("[0-9]","");

            if(token.length() == 0)
            {
                continue;
            }

            new_tokens.add(token);
        }

        return new_tokens;
    }


    private Vector<String> removeMinLength(Vector<String>tokens, int length)
    {
        Vector<String> new_tokens = new Vector<>();

        for(String token : tokens)
        {
            if(token.length() <= length)
            {
                continue;
            }

            new_tokens.add(token);
        }

        return new_tokens;
    }


    private void setup()
    {
        if(this.settings.removeStopwords())
        {
            this.stopwords = IO.readFile(STOPWORDS_FILE);
        }

        if(this.settings.removePunctuations())
        {
            this.punctuations = IO.readFile(PUNCTUATIONS_FILE);
        }

        if(this.settings.removeSymbols())
        {
            this.symbols = IO.readFile(SYMBOLS_FILE);
        }

        if(this.settings.removeIntonations())
        {
            this.intonationsMap = IO.readMapFile(INTONATIONS_FILE);
        }

        if( (this.settings.all_uppercase()) || (this.settings.all_lowercase()) )
        {
            this.lowercaseToUppercaseMap = IO.readMapFile(LOWERCASE_TO_UPPERCASE_FILE);
        }
    }
}
