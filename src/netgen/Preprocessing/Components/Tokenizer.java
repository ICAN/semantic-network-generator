package netgen.Preprocessing.Components;

import java.util.ArrayList;

import netgen.Preprocessing.PreprocessingComponent;
import netgen.Preprocessing.Token;

public class Tokenizer implements PreprocessingComponent<ArrayList<Token>>
{
	private final ArrayList<Token> tokenizedCorpus;
	
	public Tokenizer(String inCorpus)
	{
		tokenizedCorpus = tokenize(inCorpus);		
	}

	
    //Takes a filtered sentence and returns its contents as a list of tokens
    //Possible alternative: return a token set rather than a token list?
    public ArrayList<Token> tokenize(String input) {
        ArrayList<Token> sentence = new ArrayList<>();
        String[] split = input.split("\\s+");
        for (String word : split) {
            word = word.trim();
            if (word.length() > 0) {
                sentence.add(new Token(word));
            }
        }
        return sentence;
    }
	
	
	public String showRequiredTypes()
	{
		return "String";
	}
	
	public ArrayList<Token> getProcessedCorpus()
	{
		return tokenizedCorpus;
		
	}
	
	
}