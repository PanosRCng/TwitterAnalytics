package TwitterAnalytics.Tokenizer;



public class Settings
{

    private boolean remove_punctuations = true;
    private boolean remove_symbols = true;


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

}
