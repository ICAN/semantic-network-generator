package netgen.Preprocessing.Components;

import java.util.ArrayList;
import java.util.HashSet;

import netgen.DataSources.IO;
import netgen.Preprocessing.PreprocessingComponent;
import netgen.Preprocessing.Token;


public class StopwordRemoval implements PreprocessingComponent<ArrayList<ArrayList<Token>>>
{
	private final ArrayList<ArrayList<Token>> corpusStopwordsRemoved;
	private HashSet<Token> stopwords;
	
	
	public StopwordRemoval(ArrayList<ArrayList<Token>> inCorpus)
	{
		tagStopwords(inCorpus);
		
		//Import stopwords
		stopwords = new HashSet<>();
		String filepath = "src/netgen/Preprocessing/Stopwords/stopwords_combined.txt";
		stopwords.addAll(new Tokenizer(IO.readFileAsString(filepath)).getProcessedCorpus());
		
		corpusStopwordsRemoved = removeStopwords(inCorpus);
	}
	
	//Removes stopwords from this processed text
    public ArrayList<ArrayList<Token>> removeStopwords(ArrayList<ArrayList<Token>> inCorpus) {
        for (ArrayList<Token> sentence : inCorpus) 
        {

            sentence.removeAll(stopwords);
        }
        return inCorpus;
    }
	
    //Tags stopwords found in input Corpus
    public void tagStopwords(ArrayList<ArrayList<Token>> inCorpus) {
        for (ArrayList<Token> sentence : inCorpus) {
            for (Token token : sentence) {
               // /*  Error for some reason
            	if (stopwords.contains(token)) {
                    token.tag(Token.Tag.STOPWORD);
                }
                //*/
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