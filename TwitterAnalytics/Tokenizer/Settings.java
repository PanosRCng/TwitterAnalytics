package TwitterAnalytics.Tokenizer;



public class Settings
{

    private boolean remove_punctuations = true;
    private boolean remove_symbols = true;
    private boolean remove_numbers = true;
    private boolean remove_min_length = true;


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

}
