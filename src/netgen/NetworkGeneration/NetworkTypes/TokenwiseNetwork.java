package netgen.NetworkGeneration.NetworkTypes;

import java.util.ArrayList;

import netgen.NetworkGeneration.Network;
import netgen.NetworkGeneration.TokenPair;
import netgen.Preprocessing.Corpus;
import netgen.Preprocessing.Token;

public class TokenwiseNetwork extends Network
{
	
	private int windowSize;
	
	public TokenwiseNetwork(ArrayList<Corpus> inCorpii, int inWindowSize)
	{
		super(inCorpii);
		windowSize = inWindowSize;
		for(Corpus inCorpus : inCorpii)
		{
			
			System.out.println(inCorpus.getProcessedText().size() + " is the number of sentences in this article and its title is " + inCorpus.getTitle());
			generateByTokenwiseSlidingWindow(inCorpus.getProcessedText(), windowSize);
		}
	}
	
	public int getWindowSize()
	{
		return windowSize;
	}
	
	/*
    Forms a complete graph of a window which slides through each line. Returns the sum of all of these graphs. 
    Tokens will never be linked to themselves (so multiple instances of a token in a sentence will not result in reflexive edges).
    Tokens occurring more than once in a window will be weighted proportionally to the number of times they appear
    */
   //TODO: Fix
   private void generateByTokenwiseSlidingWindow(ArrayList<ArrayList<Token>> lines, int windowSize) {
	   System.out.println("The corpii size is " + lines.size() + " articles long");
	   for (ArrayList<Token> line : lines) {
           for (int i = 0; i < line.size() - windowSize; i++) { //For Token in Sentences(Line)
               for (int j = i + 1; j < i + windowSize; j++) {	// Look forward comparing current token to all within the windowsize
                   if (!line.get(i).equals(line.get(j))) {		// Only record a Pair if they are different words. Does equals() actually compare string text here?

                       TokenPair pair = new TokenPair(line.get(i).getSignature(), line.get(j).getSignature());

                       if (edgeSet.containsKey(pair)) {
                           edgeSet.put(pair, edgeSet.get(pair) + 1);    // Increment edge weight by one
                       } else {
                           edgeSet.put(pair, 1.0);						// Or create a new entry if first occurrence
                       }
                   }
               }
           }
       }
   }
	
	
	
}