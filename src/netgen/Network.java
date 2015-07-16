
package netgen;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Neal
 */
public class Network {
    
    HashMap<Pair, Double> edges = new HashMap<>();
    HashMap<Token, Integer> frequency = new HashMap<>();
    
    public Network(String fileName) {
        ArrayList<Token[]> tokenizedCorpus = load(fileName);
        this.frequency = getFrequency(tokenizedCorpus);
        this.edges = slidingWindow(tokenizedCorpus, 3);
        
    }
    
    //Takes input from a text file and produces an arraylist of lines
    private ArrayList<Token[]> load(String fileName) {
        Scanner inFile = null;
        ArrayList<Token[]> tokenizedLines = new ArrayList<>();
        
        try {
            inFile = new Scanner(new FileReader(fileName));
        } catch(Exception e) {
            System.out.println("Failed to open input file. Exiting.");
            System.exit(-1);
        }
        
        while(inFile.hasNextLine()) {
            String[] line = inFile.nextLine().trim().toLowerCase().split(" ");
            Token[] tokens = new Token[line.length];
            for (int i = 0; i < line.length; i++) {
                tokens[i] = new Token(line[i]);
            }
            tokenizedLines.add(tokens);
        }
        return tokenizedLines;
    }
    
    //Forms a complete graph of a window which slides through each line 
    //Returns the sum of all of these graphs
    //Tokens will not be linked to themselves.
    //Tokens occurring more than once in a window will be weighted proportionally to the number of times they appear
    private HashMap<Pair,Double> slidingWindow(ArrayList<Token[]> lines, int windowSize) {
        HashMap<Pair,Double> network = new HashMap<>();
        
        for(Token[] line : lines) {
            for(int i = 0; i < line.length - windowSize; i++) {
                for(int j = i + 1; j < i + windowSize; j++) {
                    if (!line[i].equals(line[j])) {
                        Pair pair = new Pair(line[i].getSignature(), line[j].getSignature());
                        if(network.containsKey(pair)) {
                            network.put(pair, network.get(pair) + 1);
                        } else {
                            network.put(pair, 1.0);
                        }
                    }
                }
            }
        }
        return network;
    }
    
    //Forms a complete graph of every line and returns the sum of all of these graphs
    //Tokens will not be linked to themselves.
    //Tokens occurring more than once in a line will be weighted proportionally to the number of times they appear
    private HashMap<Pair,Double> completeLine(ArrayList<Token[]> lines) {
        HashMap<Pair,Double> network = new HashMap<>();
     
        for(Token[] line : lines) {
            for(int i = 0; i < line.length - 1; i++) {
                for(int j = i + 1; j < line.length; j++) {
                    if (!line[i].equals(line[j])) {
                        Pair pair = new Pair(line[i].getSignature(), line[j].getSignature());
                        if(network.containsKey(pair)) {
                            network.put(pair, network.get(pair) + 1);
                        } else {
                            network.put(pair, 1.0);
                        }
                    }
                }
            }
        }
        return network;
    }
    
    
    private HashMap<Token,Integer> getFrequency(ArrayList<Token[]> lines) {
        
        HashMap<Token, Integer> count = new HashMap<>();
        
        for (Token[] tokens : lines) {
            for(int i = 0; i < tokens.length; i++) {
                if(count.containsKey(tokens[i])) {
                    int value = count.get(tokens[i]) + 1;
                    count.put(tokens[i], value);
                } else {
                    count.put(tokens[i], 1);
                }
            }
        }        
        
        return count;
    }
    
    
    
}
