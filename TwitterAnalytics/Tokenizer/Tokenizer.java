package TwitterAnalytics.Tokenizer;


import java.util.Vector;

public class Tokenizer
{

    public Tokenizer()
    {
        Vector<String> lines = IO.readFile("stopwords_lucene.txt");

        for(String line : lines)
        {
            System.out.println(line);
        }
    }



    public Vector<String> tokenize(String text)
    {
        Vector<String> tokens = this.getTokens(text, " ");

        for(String token : tokens)
        {
            System.out.println("|" + token + "|");
        }

        return tokens;
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

}
