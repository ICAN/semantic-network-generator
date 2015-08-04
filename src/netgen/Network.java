package netgen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Network {

    public HashMap<Pair, Double> edges = new HashMap<>();


    public Network(ArrayList<ArrayList<Token>> tokenizedCorpus) {
        this.edges = generateSentenceComplete(tokenizedCorpus);
    }

    //Forms a complete graph of a window which slides through each line 
    //Returns the sum of all of these graphs
    //Tokens will never be linked to identical tokens.
    //Tokens occurring more than once in a window will be weighted proportionally to the number of times they appear
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
    private static HashMap<Pair, Double> generateSentenceComplete(ArrayList<ArrayList<Token>> lines) {
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
    
    
    //Writes the graph to an .dl file, weighted edge list format
    public void writeEdgelist(String fileName) {

//        System.out.println("Writing edgelist...");
        
        ArrayList<Pair> unfilteredEdgeList = new ArrayList<>();
        ArrayList<Pair> edgeList = new ArrayList<>();
        unfilteredEdgeList.addAll(edges.keySet());

        for (Pair pair : unfilteredEdgeList) {
            if (edges.get(pair) > 1.5) {
                edgeList.add(pair);
            }
        }

        System.out.println("Filtered edges: " + edgeList.size());

        try {
            File file = new File(fileName + ".dl");
            FileWriter writer = null;
            writer = new FileWriter(file);
            writer.write("dl\nformat = edgelist1\t\nn=" + edges.size() + "\t\ndata:");

            for (Pair pair : edgeList) {
                writer.write("\n" + pair.getA().getSignature() + " " + pair.getB().getSignature() + " " + edges.get(pair) + "\t");
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
