package netgen;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

public class Network {

    private Corpus corpus;
    public HashMap<Pair, Double> edges = new HashMap<>();
    public double minimumWeight = 0;

     
    public Network(Corpus corpus) {
        this.corpus = corpus;

        //Generate network
        this.edges = generateByMultiSentenceSlidingWindow(corpus.getProcessedText(), 4, 30);
    }

    //NETWORK GENERATION METHODS
    /*
     Forms a complete graph of a window which slides through each line. Returns the sum of all of these graphs. 
     Tokens will never be linked to themselves (so multiple instances of a token in a sentence will not result in reflexive edges).
     Tokens occurring more than once in a window will be weighted proportionally to the number of times they appear
     */
    //TODO: Fix
    private static HashMap<Pair, Double> generateByTokenwiseSlidingWindow(ArrayList<ArrayList<Token>> lines, int windowSize) {
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

    //Multi-sentence-complete sliding window
    private static HashMap<Pair, Double> generateByMultiSentenceSlidingWindow(ArrayList<ArrayList<Token>> lines, int maxWindowSentences, int maxWindowTokens) {
        HashMap<Pair, Double> network = new HashMap<>();

        int minWindowTokens = 1 + maxWindowTokens / 5;
        
        //For each window...
        //(includes smaller-size windows towards the beginning and end of the lines)
        for (int i = 1 - maxWindowSentences; i < lines.size(); i++) {
            ArrayList<Token> windowTokens = new ArrayList<>();
            //Add sentences to the current window until the sentence or token maximum is met
            for (int j = i; j < i + maxWindowSentences; j++) {
                if (j >= 0 && j < lines.size() //Don't add out-of-bounds sentences
                        && (windowTokens.size() + lines.get(j).size() < maxWindowTokens //Don't exceed max tokens
                        || windowTokens.size() < minWindowTokens)) {    //Unless necessary to reach min tokens
                    windowTokens.addAll(lines.get(j));
                } else {
                    break;
                }
            }

            //Generate and attenuate the window-level network based on window size
            HashMap<Pair, Double> windowNetwork = networkSentence(windowTokens);
            for (Entry entry : windowNetwork.entrySet()) {
                entry.setValue((double) entry.getValue() / (1 + windowNetwork.size()));
//                System.out.println("Value: " + entry.getValue());
            }

            //Add the window-level network to the main network
            for (Entry entry : windowNetwork.entrySet()) {
                network = sum(network, windowNetwork);
            }

        }

        return network;
    }

    //Forms a complete graph of a single sentence
    private static HashMap<Pair, Double> networkSentence(ArrayList<Token> line) {
        HashMap<Pair, Double> network = new HashMap<>();

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
        return network;
    }

    /*
     Forms a complete graph of every line and returns the sum of all of these graphs
     Tokens will never be linked to themselves.
     Tokens occurring more than once in a line will be weighted proportionally to the number of times they appear
     */
    private static HashMap<Pair, Double> generateBySingleSentenceWindow(ArrayList<ArrayList<Token>> lines) {
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

    //BASIC GRAPH METHODS
    //Returns a new graph which is sum of the two argument graphs
    public static HashMap<Pair, Double> sum(HashMap<Pair, Double> a, HashMap<Pair, Double> b) {
        HashMap<Pair, Double> sum = new HashMap<>();

        sum.putAll(a);

        for (Entry entry : b.entrySet()) {
            if (!sum.containsKey((Pair) entry.getKey())) {
                sum.put((Pair) entry.getKey(), (double) entry.getValue());
            } else {
                sum.put((Pair) entry.getKey(), (double) entry.getValue() + sum.get((Pair) entry.getKey()));
            }
        }

        return sum;
    }

    //Scales the graph argument by the specified scalar multiple
    public static void scale(HashMap<Pair, Double> graph, double scalar) {
        for (Entry entry : graph.entrySet()) {
            entry.setValue((double) entry.getValue() * scalar);
        }
    }

    //NETWORK MODIFIERS AND FILTERS
    private void modifyEdgeWeightByTokenIncidence(HashMap<Token, Double> weightMultipliers) {
        for (Entry entry : edges.entrySet()) {
            Token a = ((Pair) entry.getKey()).getA();
            Token b = ((Pair) entry.getKey()).getA();

            if (weightMultipliers.containsKey(a)) {
                entry.setValue((double) entry.getValue() * weightMultipliers.get(a));
            }

            if (weightMultipliers.containsKey(b)) {
                entry.setValue((double) entry.getValue() * weightMultipliers.get(b));
            }
        }
    }

    public void addNoise(double intensity) {
        Random random = new Random();

        for (Entry entry : edges.entrySet()) {
            entry.setValue((double) entry.getValue() + random.nextDouble() * intensity);
        }
    }

    //Normalizes all edge weights on a (0,10] scale, with 10 being reserved for the heaviest edge
    public void normalizeToHighestEdge() {

        double scale = 1.0 / this.heaviestEdge();

        scale(this.edges, scale);

    }
    
    public void normalizeToMeanEdge() {
        
        double scale = 1.0 / this.meanEdge();
        
        scale(this.edges, scale);
    }

    //TODO: maybe a combination of unique tokens and total token count?
    public void normalizeByCorpusTokens() {
        
    }
    
    public double meanEdge() {
        
        double mean = 0;
        
        for (Entry entry : this.edges.entrySet()) {
            mean += (double)entry.getValue();
        }
        mean /= this.edges.size();
        return mean;
    }
    
    
    public double heaviestEdge() {
        double highest = 0;
        for (Entry entry : this.edges.entrySet()) {
            if ((double) entry.getValue() > highest) {
                highest = (double) entry.getValue();
            }
        }
        return highest;
    }

    /*
     Finds an appropriate threshold within the specified level of precision, measured in edge weight
     Removes all edges below that threshold
     */
    public void filterEdges(int minEdges, int maxEdges, double precision) {

        double originalSize = edges.size();
        System.out.print("Unfiltered: " + originalSize);

        //Find the heaviest edge
        double threshold = 0;
        double lowest = Double.MAX_VALUE;
        for (Entry<Pair, Double> edge : edges.entrySet()) {
            if (edge.getValue() > threshold) {
                threshold = edge.getValue();
            }
            if (edge.getValue() < lowest) {
                lowest = edge.getValue();
            }
        }

        //Set the threshold halfway between the lowest and highest weights
        //Set the incrementor to half that
        threshold = (threshold + lowest) / 2;
        double incrementor = threshold / 2;

        //Find the correct threshold
        while (incrementor > precision) {

            double edgesBelowThreshold = 0;
            for (Entry<Pair, Double> edge : edges.entrySet()) {
                if (edge.getValue() <= threshold) {
                    edgesBelowThreshold++;
                }
            }

            //If we exceed the maximum number of edges, raise threshold
            if (originalSize - edgesBelowThreshold > maxEdges) {

                threshold += incrementor;
                //If we are below the minimum number of edges, lower threshold
            } else if (originalSize - edgesBelowThreshold < minEdges) {

                threshold -= incrementor;

            }
            incrementor /= 2; //Increment decays until precision is reached
        }

        //Remove edges below threshold
        HashMap<Pair, Double> unfilteredEdges = this.edges;
        this.edges = new HashMap<>();

        for (Entry<Pair, Double> edge : unfilteredEdges.entrySet()) {
            if (edge.getValue() > threshold) {
                this.edges.put(edge.getKey(), edge.getValue());
            }
        }

        System.out.println("  Filtered: " + edges.size());

    }

    //OUTPUT METHODS
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
