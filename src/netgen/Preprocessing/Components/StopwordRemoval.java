package netgen.Preprocessing.Components;

import java.util.ArrayList;
import java.util.HashSet;

import netgen.Preprocessing.PreprocessingComponent;
import netgen.Preprocessing.Token;

/*
 * I'd like to implement the concept of an expectation of what state the 
 * incoming text is in but that might be something better handled by a manager 
 * class.
 */


public class StopwordRemoval implements PreprocessingComponent<ArrayList<ArrayList<Token>>>
{
	private final ArrayList<ArrayList<Token>> corpusStopwordsRemoved;
	private HashSet<Token> stopwords;
	
	
	public StopwordRemoval(ArrayList<ArrayList<Token>> inCorpus)
	{
		tagStopwords(inCorpus);
		corpusStopwordsRemoved = removeStopwords(inCorpus);
	}
	
	//Removes stopwords from this processed text
    public ArrayList<ArrayList<Token>> removeStopwords(ArrayList<ArrayList<Token>> inCorpus) {
        for (ArrayList<Token> sentence : inCorpus) {
            sentence.removeAll(stopwords);
        }
        return inCorpus;
    }
	
    //Tags stopwords found in input Corpus
    public void tagStopwords(ArrayList<ArrayList<Token>> inCorpus) {
        for (ArrayList<Token> sentence : inCorpus) {
            for (Token token : sentence) {
                if (stopwords.contains(token)) {
                    token.tag(Token.Tag.STOPWORD);
                }
            }
        }
    }
	
    public void setStopwords(HashSet<Token> inStopwords) {
        stopwords = inStopwords;
    }
    
    // Preprocessing Component Interface
	public String showRequiredTypes()
	{
		return "ArrayList<ArrayList<Token>>";
	}
    
    public ArrayList<ArrayList<Token>> getProcessedCorpus()
	{
		return corpusStopwordsRemoved;
	}
	
	
	
}