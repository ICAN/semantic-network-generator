
package netgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import netgen.Preprocessing.PreprocessingManager;
import netgen.Preprocessing.Token;



public class Main {
    
	public static void main(String[] args) throws Exception 
	{
		PreprocessingManager manager = new PreprocessingManager();
		ArrayList<ArrayList<Token>> tokenCorpus = manager.createTokenizedCorpii();
		System.out.println(tokenCorpus.size() + " is the size");
		
		
	}

	
	
	//TODO: Fix
//    public static void makeIndividualNetworksForEachArticle(String name) {
//
//        //Initialize Stemmer
//        Stemmer stemmer = new LancasterStemmer();
//
//        //Import stopwords
//        HashSet<Token> stopwords = new HashSet<>();
//        stopwords.addAll(Corpus.tokenize(IO.readFileAsString("stopwords_combined.txt")));
//
//        //Import database dumps and 
//        System.out.println("Importing and splitting into corpora");
//        ArrayList<Corpus> corpora = IO.importCorpora(IO.readFileAsLines(name + ".csv"), name);
//        System.out.println("Split into " + corpora.size() + " corpora");
//
//        corpora.sort(null); //Puts the corpora in chronological order
//        
//        int i = 0;
//        for (Corpus corpus : corpora) {
//
//            corpus.process(stemmer, stopwords);
//
////            System.out.println("Unique tokens: " + corpus.getTokenSet().size());
//
////            System.out.println("Tokenized Sentences: " + corpus.getProcessedText().size());
//
//            Network network = new Network(corpus);
//            
////            System.out.println("Created network");
//
////            int end = corpus.title.length();
////            if (end > 30) {
////                end = 30;
////            }
//            
//            network.addNoise(0.01);
//            network.filterEdges(60, 80, 0.003);
//            network.normalizeToHighestEdge();
//            network.writeEdgelist("" + i);
//            i++;
//        }
//
//        System.out.println("Created " + i + " networks");
//    }
    
    
    //TODO: Finish
//    public static void makeNormalizedMonthlyNetworks(String name) {
//
//        //Initialize Stemmer
//        Stemmer stemmer = new LancasterStemmer();
//
//        //Import stopwords
//        HashSet<Token> stopwords = new HashSet<>();
//        stopwords.addAll(Corpus.tokenize(IO.readFileAsString("stopwords_combined.txt")));
//
//        //Import database dumps
//        System.out.println("Importing and splitting into corpora");
//        ArrayList<Corpus> corpora = IO.importCorpora(IO.readFileAsLines(name + ".csv"), name);
//        System.out.println("Split into " + corpora.size() + " corpora");    
//    
//        
//        HashMap<String, Network> slice = new HashMap<>();
//        
//        int i = 0;
//        for (Corpus corpus : corpora) {
//
//            corpus.process(stemmer, stopwords);
//
//            Network rawNetwork = Network.generateByMultiSentenceSlidingWindow(corpus.getProcessedText(), 3, 50);
//            
//            Network 
//            rawNetwork.addNoise(0.01);
//            
//            
//            
//            rawNetwork.normalizeToHighestEdge();
//            rawNetwork.writeEdgelist("" + i);
//            i++;
//        }
//
//        
//        
//        
//        
//        System.out.println("Created " + i + " networks");
//    }
    
    
    
    
    
    
}
