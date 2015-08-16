package netgen;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class Network {

    private Corpus source;
    public HashMap<Pair, Double> edges = new HashMap<>();
    public double minimumWeight = 0;

    public Network(ArrayList<ArrayList<Token>> tokenizedCorpus) {
        this.edges = generateBySingleSentenceWindow(tokenizedCorpus);
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
    private static HashMap<Pair, Double> generateByMultiSentenceSlidingWindow(ArrayList<ArrayList<Token>> lines, int windowSize) {
        HashMap<Pair, Double> network = new HashMap<>();

        //For each window
        //Includes smaller-size windows towards the beginning and end of the lines
        for (int i = 1 - windowSize; i < lines.size(); i++) {
            ArrayList<Token> windowTokens = new ArrayList<>();

            //Add all tokens in window to windowTokens
            for (int j = i + 0; j < i + windowSize; j++) {
                if (j >= 0 && j < lines.size()) {
                    windowTokens.addAll(lines.get(j));
                }
            }

            //Generate and attenuate the network based on window size
            HashMap<Pair, Double> windowNetwork = networkSentence(windowTokens);
            for (Entry entry : windowNetwork.entrySet()) {
                entry.setValue((double) entry.getValue() / (1 + windowNetwork.size()));
                System.out.println("Value: " + entry.getValue());
            }

            //Add the windowEdges to the main network
            for (Entry entry : windowNetwork.entrySet()) {
                if (network.containsKey(entry.getKey())) {
                    network.put((Pair) entry.getKey(), (double) network.get(entry.getKey()) + (double) entry.getValue());
                } else {
                    network.put((Pair) entry.getKey(), (double) entry.getValue());
                }
            }

        }

        return network;
    }

    //Forms a complete graph of a single sentence
    private static HashMap<Pair, Double> networkSentence (ArrayList<Token> line) {
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
    
    //Returns a new graph which is sum of the two arguments
    public static HashMap<Pair, Double> add(HashMap<Pair, Double> a, HashMap<Pair, Double> b) {
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
    
     
     
    //Filters edges below a threshold such that the absolute 
    //and proportional minimum numbers of edges are retained
    //Works upward incrementally, so it's not great
    //TODO: probably actually broken
//    public void filterEdgesOld(int absoluteMinimumEdges, double proportionalMinimumEdges) {
//
//        double totalCount = edges.size();
//        double cumulativeCount = 0;
//
//        for (int weight = 1; weight < 10; weight++) {
//            double frequency = 0;
//            for (Entry<Pair, Double> edge : edges.entrySet()) {
//                if (edge.getValue() <= weight) {
//                    frequency++;
//                }
//            }
//            cumulativeCount += frequency;
//
//            //If removing edges in the current weight class will not result in
//            //too few edges (absolute or proportional), then remove all edges 
//            //in the current weight class
//            if (cumulativeCount / totalCount < (1 - absoluteMinimumEdges)
//                    && totalCount - cumulativeCount > proportionalMinimumEdges) {
//
//                for (Entry<Pair, Double> edge : edges.entrySet()) {
//                    if (edge.getValue() <= weight) {
//                        edges.remove(edge.getKey());
//                    }
//                }
//                //Otherwise we're done removing edges
//            } else {
//                break;
//            }
//        }
//        System.out.println("Unfiltered edges: " + totalCount);
//        System.out.println("Filtered edges: " + edges.size());
//
//    }

    /*
        Finds an appropriate threshold within the specified level of precision, measured in edge weight
        Removes all edges below that threshold
    */
    public void filterEdges(int absoluteMinimumEdges, double proportionalMinimumEdges, double precision) {

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
        double originalSize = edges.size();

        //Find the correct threshold
        while (incrementor > precision) {

            double frequency = 0;
            for (Entry<Pair, Double> edge : edges.entrySet()) {
                if (edge.getValue() <= threshold) {
                    frequency++;
                }
            }

            //If removing edges in the current weight class will not result in
            //too few edges (absolute or proportional), increase the threshold by toMove
            if ((originalSize - frequency) / originalSize < (proportionalMinimumEdges)
                    && originalSize - frequency > absoluteMinimumEdges) {

                threshold += incrementor;

                //Otherwise decrease
            } else {
                threshold -= incrementor;
            }
            incrementor /= 2; //Increment decays until precision is reached
        }

        //Remove edges below threshold
        for (Entry<Pair, Double> edge : edges.entrySet()) {
            if (edge.getValue() <= threshold) {
                edges.remove(edge.getKey());
            }
        }

        System.out.println("Unfiltered edges: " + originalSize);
        System.out.println("Filtered edges: " + edges.size());

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
