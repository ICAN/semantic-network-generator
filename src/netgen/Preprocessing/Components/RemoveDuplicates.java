package netgen.Preprocessing.Components;

import java.util.ArrayList;
import java.util.HashSet;

import netgen.Preprocessing.PreprocessingComponent;
import netgen.Preprocessing.Token;

public class RemoveDuplicates implements PreprocessingComponent<ArrayList<Token>>
{
	private final ArrayList<Token> nonDuplicatesCorpus;
	
	public RemoveDuplicates(ArrayList<Token> inCorpus)
	{
		nonDuplicatesCorpus = removeDuplicates(inCorpus);		
	}
	
    public ArrayList<Token> removeDuplicates(ArrayList<Token> tokenList) {
        HashSet<Token> tokenSet = new HashSet<>();
        tokenSet.addAll(tokenList);
        tokenList = new ArrayList<>();
        tokenList.addAll(tokenSet);
        return tokenList;
    }
    
    public String showRequiredTypes() {
    	return "ArrayList<Token>";
    }

	public ArrayList<Token> getProcessedCorpus() {
		return nonDuplicatesCorpus;
	}
}