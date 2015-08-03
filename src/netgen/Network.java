package netgen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Network {

    HashMap<Pair, Double> edges = new HashMap<>();
    HashMap<Token, Integer> frequency = new HashMap<>();

    public Network(ArrayList<ArrayList<Token>> tokenizedCorpus) {
        this.frequency = getFrequencyMap(tokenizedCorpus);
        this.edges = generateSentenceComplete(tokenizedCorpus);
    }

    //Forms a complete graph of a window which slides through each line 
    //Returns the sum of all of these graphs
    //Tokens will never be linked to identical tokens.
    //Tokens occurring more than once in a window will be weighted proportionally to the number of times they appear
    private HashMap<Pair, Double> generateSlidingWindow(ArrayList<ArrayList<Token>> lines, int windowSize) {
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
    private HashMap<Pair, Double> generateSentenceComplete(ArrayList<ArrayList<Token>> lines) {
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

    //Counts the number of occurrences of each unique token in the corpus
    private HashMap<Token, Integer> getFrequencyMap(ArrayList<ArrayList<Token>> lines) {

        HashMap<Token, Integer> count = new HashMap<>();

        for (ArrayList<Token> tokens : lines) {
            for (int i = 0; i < tokens.size(); i++) {
                if (count.containsKey(tokens.get(i))) {
                    int value = count.get(tokens.get(i)) + 1;
                    count.put(tokens.get(i), value);
                } else {
                    count.put(tokens.get(i), 1);
                }
            }
        }

        return count;
    }

    //Writes the graph to an .dl file, weighted edge list format
    public void writeEdgelist(String fileName) {

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
            File file = new File(fileName);
            FileWriter writer = null;
            writer = new FileWriter(file);
            writer.write("dl\nformat = edgelist1\t\nn=" + edges.size() + "\t\ndata:");

            for (Pair pair : edgeList) {
                writer.write("\n" + pair.getA().getSignature() + " " + pair.getB().getSignature() + " " + edges.get(pair) + "\t");
                System.out.println(pair.getA().getSignature() + " " + pair.getB().getSignature() + " " + edges.get(pair) + "\t");
            }
            
            writer.close();
            
        } catch (Exception e) {
            System.out.println("Failed to complete output file. Exiting.");
            System.exit(-1);
        }

    }

}
