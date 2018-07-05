package TwitterAnalytics.Tokenizer;



public class Settings
{

    private boolean remove_punctuations = true;
    private boolean remove_symbols = true;
    private boolean remove_numbers = true;
    private boolean remove_min_length = false;
    private boolean remove_intonations = true;
    private boolean all_uppercase = true;
    private boolean all_lowercase = false;
    private boolean remove_stopwords = true;


    public Settings()
    {
        //
    }


    public boolean removePunctuations()
    {
        return this.remove_punctuations;
    }


    public void setRemovePunctuations(boolean remove_punctuations)
    {
        this.remove_punctuations = remove_punctuations;
    }

    public boolean removeSymbols()
    {
        return this.remove_symbols;
    }


    public void setRemoveSymbols(boolean remove_symbols)
    {
        this.remove_symbols = remove_symbols;
    }


    public boolean removeNumbers()
    {
        return this.remove_numbers;
    }


    public void setRemoveNumbers(boolean remove_numbers)
    {
        this.remove_numbers = remove_numbers;
    }


    public boolean removeMinLength()
    {
        return this.remove_min_length;
    }


    public void setRemoveMinLength(boolean remove_min_length)
    {
        this.remove_min_length = remove_min_length;
    }


    public boolean removeIntonations()
    {
        return this.remove_intonations;
    }


    public void setRemoveIntonations(boolean intonations)
    {
        this.remove_intonations = intonations;
    }


    public boolean all_uppercase()
    {
        return this.all_uppercase;
    }


    public void setAllUppercase(boolean all_uppercase)
    {
        this.all_uppercase = all_uppercase;
    }


    public boolean all_lowercase()
    {
        return this.all_lowercase;
    }


    public void setAllLowercase(boolean all_lowercase)
    {
        this.all_lowercase = all_lowercase;
    }


    public boolean removeStopwords()
    {
        return this.remove_stopwords;
    }


    public void setRemoveStowords(boolean remove_stopwords)
    {
        this.remove_stopwords = remove_stopwords;
    }
}
