package netgen.Preprocessing;

import java.util.ArrayList;

import netgen.DataSources.IO;

public class PreprocessingManager
{

	public ArrayList<ArrayList<Token>> createTokenizedCorpus() throws Exception
	{
		ArrayList<Corpus> allCorpii = IO.importEntireSourcesFolder();
		
		for( Corpus corpus : allCorpii)
		{
	//		corpus = new 
		}
		
	}
	
	
	public ArrayList<ArrayList<Token>> createTokenizedCorpus() 
	{
		ArrayList<ArrayList<Token>> tokenizedCorpus;
		
		
		
		return tokenizedCorpus;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* Will contain logic for running preprocessing components on the Corpus
	* object. Will provide an entry point to the network generator level.
	* 
	* Should provide convenience functions to hide the complexity of the level
	* or alternatively allow for the passing of a parameters list for more 
	* fine control over how preprocessing is conducted.
	* 
	* It is important to keep the seperation of responsibilties. Prerocessing 
	* component logic should not be in here, only the scheduling and execution 
	* of them. 
	* 
	* The concept of scheduling might be best handled with a requirements 
	* system. That is, a component can have a stated expectation of how the 
	* input corpus has been processed already and throw an exception if you 
	* are trying to perform actions in a wrong order.
	*/
	

    // This method is what I envision the manager class handling  
    // Conducts some processing activities on the corpus
    /*
    public void process(Stemmer stemmer, HashSet<Token> stopwords) {
        //Set stemmer and stopwords
        this.stemmer = stemmer;
        this.stopwords = stopwords;

        //Split on sentences, filter characters, and tokenize
        ArrayList<ArrayList<Token>> processed = Corpus.tokenize(makeFilteredStrings(Corpus.splitSentences(rawText)));

        for (ArrayList<Token> line : processed) {
            for (Token token : line) {
                token.setSignature(stemmer.stem(token.getSignature()));
            }
        }

        this.processedText = processed;

        //Filter stopwords, generate metadata
        //TODO: switch to tagging stopwords?
        this.removeStopwords();
        this.generateFrequencyMap();

    }
*/
	
	
	
	
}