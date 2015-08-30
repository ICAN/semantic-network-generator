package netgen.Preprocessing;

import java.util.ArrayList;
import java.util.HashSet;

/*
 * I'd like to implement the concept of an expectation of what state the 
 * inoming text is in but that might be something better handled by a manager 
 * class.
 */


public class StopwordRemoval implements PreprocessingComponent
{
	private String CorpusStopwordsRemoved;
	
	public StopwordRemoval()
	{
		tagStopwords();
		removeStopwords();
	}
	
	//Removes stopwords from this processed text
    public void removeStopwords() {
        for (ArrayList<Token> sentence : this.processedText) {
            sentence.removeAll(this.stopwords);
        }
    }
	
    //Tags stopwords as such
    public void tagStopwords() {
        for (ArrayList<Token> sentence : this.processedText) {
            for (Token token : sentence) {
                if (stopwords.contains(token)) {
                    token.tag(Token.Tag.STOPWORD);
                }
            }
        }
    }
	
    public void setStopwords(HashSet<Token> stopwords) {
        this.stopwords = stopwords;
    }
    
    // Preprocessing Component Interface
	public String getProcessedCorpus()
	{
		return CorpusStopwordsRemoved;
	}
	
	
	
}