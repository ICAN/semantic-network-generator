package netgen.Preprocessing.Components;

import java.util.ArrayList;

import netgen.Preprocessing.PreprocessingComponent;

public class FilterCharacters implements PreprocessingComponent<Object>
{
	private final Object filteredCorpus;
	
	public FilterCharacters(String inCorpus)
	{
		filteredCorpus = filterNonAlpha(inCorpus);
	}
	public FilterCharacters(ArrayList<String> inCorpus)
	{
		filteredCorpus = makeFilteredStrings(inCorpus);
		
	}
	
    //Filters out everything but spaces, letters
    //Trims and converts letters to lower-case
    public String filterNonAlpha(String input) {
        return input.replaceAll("[^a-zA-Z ]", " ").toLowerCase().trim();
    }

    //Filters out everything but spaces, letters
    //Trims and converts to lower-case
    public ArrayList<String> makeFilteredStrings(ArrayList<String> input) {
        ArrayList<String> output = new ArrayList<>();
        for (String line : input) {
            output.add(filterNonAlpha(line));
        }
        return output;
    }
	
	public Object getProcessedCorpus() 
	{
		return filteredCorpus;
	}
	public String showRequiredTypes()
	{
		return "String, ArrayList<String>";
	}
}