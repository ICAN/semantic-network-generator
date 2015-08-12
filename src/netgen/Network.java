package netgen;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class Network {

    private Corpus source;
    public HashMap<Pair, Double> edges = new HashMap<>();
    public double minimumWeight = 0;
    
    public Network(ArrayList<ArrayList<Token>> tokenizedCorpus) {
        this.edges = generateSentenceCompleteNetwork(tokenizedCorpus);
    }

        
    /*
    Forms a complete graph of a window which slides through each line. Returns the sum of all of these graphs. 
    Tokens will never be linked to themselves (so multiple instances of a token in a sentence will not result in reflexive edges).
    Tokens occurring more than once in a window will be weighted proportionally to the number of times they appear
    */
    //TODO: Fix
    private static HashMap<Pair, Double> generateSlidingWindow(ArrayList<ArrayList<Token>> lines, int windowSize) {
        HashMap<Pair, Double> network = new HashMap<>();

        for (ArrayList<Token> line : lines) {
            for (int i = 0; i < line.size() - windowSize; i++) {
                for (int j = i + 1; j < i + windowSize; j++) {
                    if (!line.get(i).equals(line.get(j))) {
                        
                        Pair pair = new Pair(line.get(i).getSignature(), line.get(j).getSignature());
                        
                        if (network.containsKey(pair)) {
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
    //Tokens will never be linked to themselves.
    //Tokens occurring more than once in a line will be weighted proportionally to the number of times they appear
    private static HashMap<Pair, Double> generateSentenceCompleteNetwork(ArrayList<ArrayList<Token>> lines) {
        HashMap<Pair, Double> network = new HashMap<>();

        for (ArrayList<Token> line : lines) {
            for (int i = 0; i < line.size() - 1; i++) {
                for (int j = i + 1; j < line.size(); j++) {
                    if (!line.get(i).equals(line.get(j))) {
                        Pair pair = new Pair(line.get(i).getSignature(), line.get(j).getSignature());
                        if (network.containsKey(pair)) {
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
    
    
    public void filterEdges(int absoluteMinimumEdges, double proportionalMinimumEdges) {
        
        double totalCount = edges.size();
        double cumulativeCount = 0;
        
        for(int weight = 1; weight < 10; weight++) {
            double frequency = 0;
            for(Entry<Pair, Double> edge : edges.entrySet()) {
                if(edge.getValue() <= weight) {
                    frequency++;
                }
            }    
            cumulativeCount += frequency;
            
            //If removing edges in the current weight class will not result in
            //too few edges (absolute or proportional), then remove all edges 
            //in the current weight class
            if(cumulativeCount/totalCount < (1 - absoluteMinimumEdges) 
                    && totalCount - cumulativeCount > proportionalMinimumEdges) {
                
                for(Entry<Pair, Double> edge : edges.entrySet()) {
                    if(edge.getValue() <= weight) {
                        edges.remove(edge.getKey());
                    }    
                }
            //Otherwise we're done removing edges
            } else {
                break;                
            }
        }
        System.out.println("Unfiltered edges: " + totalCount);
        System.out.println("Filtered edges: " + edges.size());
        
    }
    
    
    //Writes the graph to an .dl file, weighted edge list format
    public void writeEdgelist(String fileName) {

//        System.out.println("Writing edgelist...");
        
        

        try {
            File file = new File(fileName + ".dl");
            FileWriter writer = null;
            writer = new FileWriter(file);
            writer.write("dl\nformat = edgelist1\t\nn=" + edges.size() + "\t\ndata:");

            for (Entry<Pair, Double> edge : edges.entrySet()) {
                writer.write("\n" + edge.getKey().getA().getSignature() + " " + edge.getKey().getB().getSignature() + " " + edges.get(edge.getKey()) + "\t");
                //Debug
//                System.out.println(pair.getA().getSignature() + " " + pair.getB().getSignature() + " " + edges.get(pair) + "\t");
            }
            
            writer.close();
            
        } catch (Exception e) {
            System.out.println("Failed to complete output file. Exiting.");
            System.exit(-1);
        }

    }

}
