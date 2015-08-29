package netgen;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import netgen.Preprocessing.Token;
import netgen.Preprocessing.TokenPair;

import java.util.Random;

public class Network {

    private HashMap<TokenPair, Double> edgeset;

    //Simple constructor
    public Network() {
        edgeset = new HashMap<>();
    }

    //NETWORK GENERATION METHODS
    /*
     Forms a complete graph of a window which slides through each line. Returns the sum of all of these graphs. 
     Tokens will never be linked to themselves (so multiple instances of a token in a sentence will not result in reflexive edges).
     Tokens occurring more than once in a window will be weighted proportionally to the number of times they appear
     */
    //TODO: Fix
    private static Network generateByTokenwiseSlidingWindow(ArrayList<ArrayList<Token>> lines, int windowSize) {

        Network network = new Network();

        HashMap<TokenPair, Double> edgeset = new HashMap<>();

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

    //Multi-sentence-complete sliding window
    public static Network generateByMultiSentenceSlidingWindow(ArrayList<ArrayList<Token>> lines, int maxWindowSentences, int maxWindowTokens) {

        Network network = new Network();

        HashMap<TokenPair, Double> edgeset = new HashMap<>();

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
            HashMap<TokenPair, Double> windowNetwork = networkSentence(windowTokens);
            for (Entry edge : windowNetwork.entrySet()) {
                edge.setValue((double)edge.getValue() / (1 + windowNetwork.size()));
            }

            //Add the window-level network to the main network
            for (Entry entry : windowNetwork.entrySet()) {
                edgeset = sum(edgeset, windowNetwork);
            }

        }

        network.setEdgeset(edgeset);

        return network;
    }

    //Forms a complete graph of a single sentence
    private static HashMap<TokenPair, Double> networkSentence(ArrayList<Token> line) {
        HashMap<TokenPair, Double> edgeset = new HashMap<>();

        for (int i = 0; i < line.size() - 1; i++) {
            for (int j = i + 1; j < line.size(); j++) {
                if (!line.get(i).equals(line.get(j))) {
                    TokenPair pair = new TokenPair(line.get(i).getSignature(), line.get(j).getSignature());
                    if (edgeset.containsKey(pair)) {
                        edgeset.put(pair, (double)edgeset.get(pair) + 1.0);
                    } else {
                        edgeset.put(pair, 1.0);
                    }
                }
            }
        }
        return edgeset;
    }

    /*
     Forms a complete graph of every line and returns the sum of all of these graphs
     Tokens will never be linked to themselves.
     Tokens occurring more than once in a line will be weighted proportionally to the number of times they appear
     */
    private static Network generateBySingleSentenceWindow(ArrayList<ArrayList<Token>> lines) {

        Network network = new Network();

        HashMap<TokenPair, Double> edgeset = new HashMap<>();

        for (ArrayList<Token> line : lines) {
            for (int i = 0; i < line.size() - 1; i++) {
                for (int j = i + 1; j < line.size(); j++) {
                    if (!line.get(i).equals(line.get(j))) {
                        TokenPair pair = new TokenPair(line.get(i).getSignature(), line.get(j).getSignature());
                        if (edgeset.containsKey(pair)) {
                            edgeset.put(pair, (double)edgeset.get(pair) + 1.0);
                        } else {
                            edgeset.put(pair, 1.0);
                        }
                    }
                }
            }
        }
        return network;
    }

    //BASIC GRAPH METHODS
    //Returns a new graph which is sum of the two argument graphs
    private static HashMap<TokenPair, Double> sum(HashMap<TokenPair, Double> a, HashMap<TokenPair, Double> b) {
        HashMap<TokenPair, Double> sum = new HashMap<>();

        sum.putAll(a);

        for (Entry entry : b.entrySet()) {
            if (!sum.containsKey((TokenPair) entry.getKey())) {
                sum.put((TokenPair) entry.getKey(), (double) entry.getValue());
            } else {
                sum.put((TokenPair) entry.getKey(), (double) entry.getValue() + sum.get((TokenPair) entry.getKey()));
            }
        }

        return sum;
    }
    
    //Returns a single unlabeled network consisting of all edges in the networks
    //with weights set equal to the sum of the weights in those networks
    public static Network sum(Network a, Network b) {
        Network sum = new Network();
        HashMap<TokenPair, Double> edgeset = sum(a.getEdgeset(), b.getEdgeset());
        sum.setEdgeset(edgeset);
        return sum;
    }
    
    //Returns a single unlabeled network consisting of all edges in the list of networks
    //with weights set equal to the sum of the weights in those networks
    public static Network sum(ArrayList<Network> networks) {
        Network sum = new Network();
        HashMap<TokenPair, Double> edgeset = new HashMap<>();
        
        for(Network network : networks) {
            edgeset = sum(edgeset, network.getEdgeset());
        }
        
        sum.setEdgeset(edgeset);
        return sum;
    }
    
    //Scales the graph argument by the specified scalar multiple
    public static void scale(HashMap<TokenPair, Double> graph, double scalar) {
        for (Entry entry : graph.entrySet()) {
            entry.setValue((double) entry.getValue() * scalar);
        }
    }

    //NETWORK MODIFIERS AND FILTERS
    private void modifyEdgeWeightByTokenIncidence(HashMap<Token, Double> weightMultipliers) {
        for (Entry entry : getEdgeset().entrySet()) {
            Token a = ((TokenPair) entry.getKey()).getA();
            Token b = ((TokenPair) entry.getKey()).getA();

            if (weightMultipliers.containsKey(a)) {
                entry.setValue((double) entry.getValue() * weightMultipliers.get(a));
            }

            if (weightMultipliers.containsKey(b)) {
                entry.setValue((double) entry.getValue() * weightMultipliers.get(b));
            }
        }
    }

    //Todo
    public void addAll(HashMap<TokenPair, Double> edgeset) {

    }

    //TODO
    public void addAll(HashSet<Edge> edgeset) {

    }

    public void addAll(ArrayList<Edge> edgelist) {

    }

    public void addNoise(double intensity) {
        Random random = new Random();

        for (Entry entry : this.edgeset.entrySet()) {
            entry.setValue((double) entry.getValue() + random.nextDouble() * intensity);
        }
    }

    //Normalizes all edge weights on a (0,10] scale, with 10 being reserved for the heaviest edge
    public void normalizeToHighestEdge() {

        double scale = 1.0 / this.heaviestEdge();

        scale(this.edgeset, scale);

    }

    public void normalizeToMeanEdge() {

        double scale = 1.0 / this.meanEdge();

        scale(this.edgeset, scale);
    }

    //TODO: maybe a combination of unique tokens and total token count?
    public void normalizeByCorpusTokens() {

    }

    public double meanEdge() {

        double mean = 0;

        for (Entry entry : this.edgeset.entrySet()) {
            mean += (double) entry.getValue();
        }
        mean /= this.edgeset.size();
        return mean;
    }

    public double heaviestEdge() {
        double highest = 0;
        for (Entry entry : this.edgeset.entrySet()) {
            if ((double) entry.getValue() > highest) {
                highest = (double) entry.getValue();
            }
        }
        return highest;
    }
    
    //Returns a list of the heaviest <size> edges
    //Will return entire graph if there the graph is smaller than <size>
    public ArrayList<Edge> getEdgesAsFilteredList(int size) {

        double originalSize = getEdgeset().size();
        System.out.print("Unfiltered: " + originalSize);

        ArrayList<Edge> edgelist = this.getEdgesAsList();

        edgelist.sort(null); //Sorts by edge weight

        System.out.println("  Filtered: " + getEdgeset().size());
        
        for(int i = size; i < edgelist.size(); i++) {
            edgelist.remove(i);
        }
        
        edgelist.trimToSize();
        
        return edgelist;
    }

    //OUTPUT METHODS
    //Writes the graph to an .dl file, weighted edge list format
    public void writeEdgelist(String fileName) {

//        System.out.println("Writing edgelist...");
        try {
            File file = new File(fileName + ".dl");
            FileWriter writer = null;
            writer = new FileWriter(file);
            writer.write("dl\nformat = edgelist1\t\nn=" + getEdgeset().size() + "\t\ndata:");

            for (Entry<TokenPair, Double> edge : getEdgeset().entrySet()) {
                writer.write("\n" + edge.getKey().getA().getSignature() + " " + edge.getKey().getB().getSignature() + " " + getEdgeset().get(edge.getKey()) + "\t");
                //Debug
//                System.out.println(pair.getA().getSignature() + " " + pair.getB().getSignature() + " " + edges.get(pair) + "\t");
            }

            writer.close();

        } catch (Exception e) {
            System.out.println("Failed to complete output file. Exiting.");
            System.exit(-1);
        }

    }

    //ACCESSORS AND MUTATORS
    /**
     * @return the edgeset
     */
    public HashMap<TokenPair, Double> getEdgeset() {
        return this.edgeset;
    }
    
    /*
    * @return a list of Edge objects consisting of all edges in this network
    */
    public ArrayList<Edge> getEdgesAsList() {
        ArrayList<Edge> edgelist = new ArrayList<>();
        for(Entry entry : this.edgeset.entrySet()) {
            edgelist.add(new Edge((TokenPair)entry.getKey(), (double)entry.getValue()));
        }
        return edgelist;
    }

    /**
     * @param edgeset the edgeset to set
     */
    public void setEdgeset(HashMap<TokenPair, Double> edgeset) {
        this.edgeset = edgeset;
    }
    
    //Adds the other network to this network
    public void add(Network other) {      
        for(Edge edge : other.getEdgesAsList()) {
            if(this.edgeset.containsKey(edge.getIncidentTokens())) {
                this.edgeset.put(edge.getIncidentTokens(), edge.getWeight() + this.edgeset.get(edge.getIncidentTokens()));
            } else {
                this.edgeset.put(edge.getIncidentTokens(), edge.getWeight());
            }
        }
    }

    public void putAll(HashSet<Edge> edgeset) {
        for(Edge edge : edgeset) {
            this.edgeset.put(edge.getIncidentTokens(), edge.getWeight());
        }
    }

}
