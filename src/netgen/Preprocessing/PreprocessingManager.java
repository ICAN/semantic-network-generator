package netgen.Preprocessing;

import java.util.ArrayList;

import netgen.DataSources.IO;
import netgen.Preprocessing.Components.SentenceSplitter;
import netgen.Preprocessing.Components.StopwordRemoval;
import netgen.Preprocessing.Components.Tokenizer;

public class PreprocessingManager
{

	public ArrayList<Corpus> createTokenizedCorpii() throws Exception
	{
		// Reads every file in the Data Sources folder
		ArrayList<RawCorpus> allRawCorpii = IO.importEntireSourcesFolder();
		ArrayList<Corpus> finalCorpii = new ArrayList<Corpus>();
		
		ArrayList<ArrayList<Token>> tokenizedSentences = new ArrayList<ArrayList<Token>>();
		System.out.println(allRawCorpii.size() + " is the number of articles in the full corpus");
		SentenceSplitter sentences;
		for( RawCorpus rawCorpus : allRawCorpii) // Iterate over all the raw articles from the three imported DB files
		{											// Is this really happening? Yes
			
			//System.out.println(rawCorpus.getTitle() + " is the title of this article");
			//System.out.println(rawCorpus.getRawText().length() + " is the length of this article string");
			sentences = new SentenceSplitter(rawCorpus.getRawText());
			System.out.println(sentences.getProcessedCorpus().size() + " is the number of sentences in the article");
			System.out.println(sentences.getProcessedCorpus().get(0) + " is the first sentence in the article");
			System.out.println(sentences.getProcessedCorpus().get(1) + " is the second sentence in the article");
				
			for(String sentence : sentences.getProcessedCorpus())
			{
				
				Tokenizer tokens = new Tokenizer(sentence);
				//System.out.println(tokens.getProcessedCorpus());
				tokenizedSentences.add(tokens.getProcessedCorpus());
				System.out.println("Length of tokenized Sentences is " + tokenizedSentences.size());
				System.out.println(tokenizedSentences.get(0).size());
			}
			
			Corpus processedCorpus = new Corpus(rawCorpus);
			processedCorpus.setProcessedText(tokenizedSentences);
			finalCorpii.add(processedCorpus);			
			System.out.println("finalCorpii is this big: " + finalCorpii.size());
		}
		return finalCorpii;
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