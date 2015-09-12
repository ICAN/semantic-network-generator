package netgen.Preprocessing;

import java.util.ArrayList;

import netgen.DataSources.IO;
import netgen.Preprocessing.Components.SentenceSplitter;
import netgen.Preprocessing.Components.StopwordRemoval;
import netgen.Preprocessing.Components.Tokenizer;

public class PreprocessingManager
{

	public ArrayList<Article> createTokenizedCorpii() throws Exception
	{
		/*	This is not as simple as a manager as I had hoped. The problem
		 * 	lies in the conversion of values passed between the components.
		 * 	This input handling is something the components themselves could 
		 * 	do. 
		 * 	
		 * 	I would like a pipeline instantiation to look something like this
		 * 	ArrayList<Article> tokenizedSentences = new new Tokenizer(new SentenceSplitter
		 * 			(IO.importEntireSourcesFolder()).getProcessedText();	
		 * 
		 * 
		 */
		
		
		// Reads every file in the Data Sources folder
		ArrayList<RawArticle> allRawArticles = IO.importEntireSourcesFolder();
		ArrayList<Article> finalArticles = new ArrayList<Article>();
		
		ArrayList<ArrayList<Token>> tokenizedSentences = new ArrayList<ArrayList<Token>>();
		System.out.println(allRawArticles.size() + " is the number of articles in the full corpus");
		SentenceSplitter sentences;
		for( RawArticle rawCorpus : allRawArticles) // Iterate over all the raw articles from the three imported DB files
		{
			sentences = new SentenceSplitter(rawCorpus.getRawText());	
			for(String sentence : sentences.getProcessedCorpus())
			{
				
				Tokenizer tokens = new Tokenizer(sentence);
				tokenizedSentences.add(tokens.getProcessedCorpus());
			}
			
			Article processedCorpus = new Article(rawCorpus);
			processedCorpus.setProcessedText(tokenizedSentences);
			finalArticles.add(processedCorpus);
			System.out.println("finalCorpii is this big: " + finalArticles.size());
		}
		return finalArticles;
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