package TwitterAnalytics.Sentimenter.SentimentLexicon;


import java.util.Vector;



public class Entry
{

    public String term;

    public String pos1;
    public String pos2;
    public String pos3;
    public String pos4;

    public String subjectivity1;
    public String subjectivity2;
    public String subjectivity3;
    public String subjectivity4;

    public String polarity1;
    public String polarity2;
    public String polarity3;
    public String polarity4;

    public String anger1;
    public String anger2;
    public String anger3;
    public String anger4;

    public String disgust1;
    public String disgust2;
    public String disgust3;
    public String disgust4;

    public String fear1;
    public String fear2;
    public String fear3;
    public String fear4;

    public String happiness1;
    public String happiness2;
    public String happiness3;
    public String happiness4;

    public String sadness1;
    public String sadness2;
    public String sadness3;
    public String sadness4;

    public String surprise1;
    public String surprise2;
    public String surprise3;
    public String surprise4;

    public String aditional1;
    public String aditional2;
    public String aditional3;
    public String aditional4;

    public String comments1;
    public String comments2;
    public String comments3;
    public String comments4;


    public Entry()
    {

    }


    public static Entry create(Vector<String> parts)
    {
        Entry entry = new Entry();

        entry.term = parts.get(0);

        entry.pos1 = parts.get(1);
        entry.pos2 = parts.get(2);
        entry.pos3 = parts.get(3);
        entry.pos4 = parts.get(4);

        entry.subjectivity1 = parts.get(5);
        entry.subjectivity2 = parts.get(6);
        entry.subjectivity3 = parts.get(7);
        entry.subjectivity4 = parts.get(8);

        System.out.println(entry.subjectivity1);

        return entry;
    }

}
