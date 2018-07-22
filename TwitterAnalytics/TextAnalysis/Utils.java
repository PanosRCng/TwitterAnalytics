package TwitterAnalytics.TextAnalysis;


import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class Utils
{
    public static final String STOPWORDS_FILE = "stopwords_gr_nlp.txt";
    public static final String PUNCTUATIONS_FILE = "punctuations.txt";
    public static final String SYMBOLS_FILE = "symbols.txt";
    public static final String INTONATIONS_FILE = "intonations.txt";
    public static final String LOWERCASE_TO_UPPERCASE_FILE = "lowercase_to_uppercase.txt";
    public static final String[] WORD_DIVIDERS = {"\t", "\r", "\n"};


    private Vector<String> stopwords;
    private Vector<String> punctuations;
    private Vector<String> symbols;
    private HashMap<String, String> intonationsMap;
    private HashMap<String, String> lowercaseToUppercaseMap;



    private Utils()
    {
        //
    }



    private static class SingletonHelper
    {
        private static final Utils INSTANCE = new Utils();
    }


    public static Vector<String> removeStopwords(Vector<String> tokens)
    {
        if(SingletonHelper.INSTANCE.stopwords == null)
        {
            SingletonHelper.INSTANCE.stopwords = IO.readFile(STOPWORDS_FILE);
        }

        Vector<String> new_tokens = new Vector<String>();

        for(String token : tokens)
        {
            boolean found = false;

            for(String stopword : SingletonHelper.INSTANCE.stopwords)
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


    public static String removeWordDividers(String text)
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


    public static String removeSymbols(String text)
    {
        if(SingletonHelper.INSTANCE.symbols == null)
        {
            SingletonHelper.INSTANCE.symbols = IO.readFile(SYMBOLS_FILE);
        }

        for(String symbol : SingletonHelper.INSTANCE.symbols)
        {
            if(text.contains(symbol))
            {
                text = text.replace(symbol, " ");
            }
        }

        return text;
    }


    public static String removePunctuations(String text)
    {
        if(SingletonHelper.INSTANCE.punctuations == null)
        {
            SingletonHelper.INSTANCE.punctuations = IO.readFile(PUNCTUATIONS_FILE);
        }

        for(String punctuation : SingletonHelper.INSTANCE.punctuations)
        {
            if(text.contains(punctuation))
            {
                text = text.replace(punctuation, " ");
            }
        }

        return text;
    }


    public static String removeIntonations(String text)
    {
        if(SingletonHelper.INSTANCE.intonationsMap == null)
        {
            SingletonHelper.INSTANCE.intonationsMap = IO.readMapFile(INTONATIONS_FILE);
        }

        for(Map.Entry<String,String> innotation : SingletonHelper.INSTANCE.intonationsMap.entrySet())
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


    public static Vector<String> removeNumbers(Vector<String> tokens)
    {
        Vector<String> new_tokens = new Vector<String>();

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


    public static Vector<String> removeMinLength(Vector<String>tokens, int length)
    {
        Vector<String> new_tokens = new Vector<String>();

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


    public static String lowercase(String text)
    {
        if(SingletonHelper.INSTANCE.lowercaseToUppercaseMap == null)
        {
            SingletonHelper.INSTANCE.lowercaseToUppercaseMap = IO.readMapFile(LOWERCASE_TO_UPPERCASE_FILE);
        }

        for(Map.Entry<String,String> lowercase_to_uppercase : SingletonHelper.INSTANCE.lowercaseToUppercaseMap.entrySet())
        {
            String lowercase = lowercase_to_uppercase.getKey();
            String uppercase = lowercase_to_uppercase.getValue();

            if(text.contains(uppercase))
            {
                text = text.replace(uppercase, lowercase);
            }

            text = Utils.covertSigma(text);
        }

        return text;
    }


    public static String uppercase(String text)
    {
        if(SingletonHelper.INSTANCE.lowercaseToUppercaseMap == null)
        {
            SingletonHelper.INSTANCE.lowercaseToUppercaseMap = IO.readMapFile(LOWERCASE_TO_UPPERCASE_FILE);
        }

        for(Map.Entry<String,String> lowercase_to_uppercase : SingletonHelper.INSTANCE.lowercaseToUppercaseMap.entrySet())
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



    public static Vector<String> lowercase(Vector<String> tokens)
    {
        if(SingletonHelper.INSTANCE.lowercaseToUppercaseMap == null)
        {
            SingletonHelper.INSTANCE.lowercaseToUppercaseMap = IO.readMapFile(LOWERCASE_TO_UPPERCASE_FILE);
        }

        Vector<String> new_tonens = new Vector<String>();

        for(String token : tokens)
        {
            for(Map.Entry<String,String> lowercase_to_uppercase : SingletonHelper.INSTANCE.lowercaseToUppercaseMap.entrySet())
            {
                String lowercase = lowercase_to_uppercase.getKey();
                String uppercase = lowercase_to_uppercase.getValue();

                if(token.contains(uppercase))
                {
                    token = token.replace(uppercase, lowercase);
                }

                token = Utils.covertSigma(token);
            }

            new_tonens.add(token);
        }

        return new_tonens;
    }


    public static Vector<String> uppercase(Vector<String> tokens)
    {
        if(SingletonHelper.INSTANCE.lowercaseToUppercaseMap == null)
        {
            SingletonHelper.INSTANCE.lowercaseToUppercaseMap = IO.readMapFile(LOWERCASE_TO_UPPERCASE_FILE);
        }

        Vector<String> new_tonens = new Vector<String>();

        for(String token : tokens)
        {
            for(Map.Entry<String,String> lowercase_to_uppercase : SingletonHelper.INSTANCE.lowercaseToUppercaseMap.entrySet())
            {
                String lowercase = lowercase_to_uppercase.getKey();
                String uppercase = lowercase_to_uppercase.getValue();

                if(token.contains(lowercase))
                {
                    token = token.replace(lowercase, uppercase);
                }
            }

            new_tonens.add(token);
        }

        return new_tonens;
    }


    public static String covertSigma(String text)
    {
        if(text.contains("ς"))
        {
            text = text.replace("ς", "σ");
        }

        return text;
    }


}
