package TwitterAnalytics.TextAnalysis.Sentimenter.SentimentLexicon;


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

    public String additional1;
    public String additional2;
    public String additional3;
    public String additional4;

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

        entry.polarity1 = parts.get(9);
        entry.polarity2 = parts.get(10);
        entry.polarity3 = parts.get(11);
        entry.polarity4 = parts.get(12);

        entry.anger1 = parts.get(13);
        entry.anger2 = parts.get(14);
        entry.anger3 = parts.get(15);
        entry.anger4 = parts.get(16);

        entry.disgust1 = parts.get(17);
        entry.disgust2 = parts.get(18);
        entry.disgust3 = parts.get(19);
        entry.disgust4 = parts.get(20);

        entry.fear1 = parts.get(21);
        entry.fear2 = parts.get(22);
        entry.fear3 = parts.get(23);
        entry.fear4 = parts.get(24);

        entry.happiness1 = parts.get(25);
        entry.happiness2 = parts.get(26);
        entry.happiness3 = parts.get(27);
        entry.happiness4 = parts.get(28);

        entry.sadness1 = parts.get(29);
        entry.sadness2 = parts.get(30);
        entry.sadness3 = parts.get(31);
        entry.sadness4 = parts.get(32);

        entry.surprise1 = parts.get(33);
        entry.surprise2 = parts.get(34);
        entry.surprise3 = parts.get(35);
        entry.surprise4 = parts.get(36);

        entry.additional1 = parts.get(37);
        entry.additional2 = parts.get(38);
        entry.additional3 = parts.get(39);
        entry.additional4 = parts.get(40);

        entry.comments1 = parts.get(41);
        entry.comments2 = parts.get(42);
        entry.comments3 = parts.get(43);
        entry.comments4 = parts.get(44);

        return entry;
    }

}
