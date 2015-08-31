package netgen.NetworkGeneration.NetworkTypes;

import java.util.ArrayList;

import netgen.NetworkGeneration.Network;
import netgen.NetworkGeneration.TokenPair;
import netgen.Preprocessing.Corpus;
import netgen.Preprocessing.Token;

public class TokenwiseNetwork extends Network
{
	public TokenwiseNetwork(Corpus inCorpus, int windowSize)
	{
		super(inCorpus);
		generateByTokenwiseSlidingWindow(inCorpus.getProcessedText(), windowSize);
		
	}
	
	/*
    Forms a complete graph of a window which slides through each line. Returns the sum of all of these graphs. 
    Tokens will never be linked to themselves (so multiple instances of a token in a sentence will not result in reflexive edges).
    Tokens occurring more than once in a window will be weighted proportionally to the number of times they appear
    */
   //TODO: Fix
   private static Network generateByTokenwiseSlidingWindow(ArrayList<ArrayList<Token>> lines, int windowSize) {

       //Network network = new Network();

       //HashMap<TokenPair, Double> edgeset = new HashMap<>();
   	
   	
       for (ArrayList<Token> line : lines) {
           for (int i = 0; i < line.size() - windowSize; i++) {
               for (int j = i + 1; j < i + windowSize; j++) {
                   if (!line.get(i).equals(line.get(j))) {

                       TokenPair pair = new TokenPair(line.get(i).getSignature(), line.get(j).getSignature());

                       if (edgeset.containsKey(pair)) {
                           edgeset.put(pair, edgeset.get(pair) + 1);
                       } else {
                           edgeset.put(pair, 1.0);
                       }
                   }
               }
           }
       }

       network.setEdgeset(edgeset);

       return network;
   }
	
	
	
}