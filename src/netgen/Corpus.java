package netgen;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lancaster.Stemmer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.lang.String;


public class Corpus {
    
    
    
    private static ArrayList<String> read(String fileName) {
        ArrayList<String> lines = new ArrayList<>();
        Scanner inFile = null;
  
        try {
            inFile = new Scanner(new FileReader(fileName));
        } catch (Exception e) {
            System.out.println("Failed to open input file. Exiting.");
            System.exit(-1);
        }

        while (inFile.hasNextLine()) {
            lines.add(inFile.nextLine());
        }
        return lines;
    }

//    HashMap<Token, ArrayList<Tag>> corpus;
    //Splits corpus into strings consisting of individual sentences
    public static ArrayList<String> splitIntoSentences(ArrayList<String> corpus) {
        ArrayList<String> sentences = new ArrayList<>();

        //TODO: ADD PREFILTERING FOR UNWANTED SPECIAL CHARACTERS
        
        String preCompletion = "( ([A-Za-z]|\\#|-|'){2,}?)";
        String completion = "(\\.|;|\\?|!)+";
        String postCompletion = "(\\\"|\\\'|[0-9]| |\\z|$|\\n){1,2}";

        for (String line : corpus) {
            Pattern pattern = Pattern.compile(preCompletion + completion + postCompletion);
            Matcher m = pattern.matcher(line);
            int index = 0;
            while (m.find()) {
                String s = line.substring(index, m.end());
                if (!s.matches(".* (Mr|Mrs|Ms|Dr|Rev|Esq|Mass|Conn). ") //Avoid matching on abbreviated titles
                        && !s.matches(".*[0-9]+(.|,)[0-9]+.*")) { //Avoid matching numerical information
                    if (s.length() > 1) {
                        sentences.add(s);
                        index = m.end(); //avoid adding empty sentences
                    }
                }
            }
        }

        return sentences;
    }

    //Modified version which takes a single string
    public static ArrayList<String> splitIntoSentences(String corpus) {
        ArrayList<String> list = new ArrayList<String>(1);
        list.add(corpus);
        return Corpus.splitIntoSentences(list);
    }

    
    //
    public static ArrayList<ArrayList<Token>> tokenize(ArrayList<String> input) {
        ArrayList<ArrayList<Token>> output = new ArrayList<>();

        for (String line : input) {
            output.add(Corpus.tokenize(line));
        }
        return output;
    }

    public static ArrayList<Token> tokenize(String input) {
        ArrayList<Token> sentence = new ArrayList<>();
        String[] split = input.split("\\s+");
        for (String word : split) {
            word = word.trim();
            if (word.length() > 0) {
                sentence.add(new Token(word));
            }
        }
        return sentence;
    }

    public static ArrayList<String> filterCharacters(ArrayList<String> input) {
        ArrayList<String> output = new ArrayList<>();
        for (String line : input) {
            output.add(Corpus.filterCharacters(line));
        }
        return output;
    }

    //Filters out everything but spaces, letters
    //Trims and converts to lower-case
    public static String filterCharacters(String input) {
        return input.replaceAll("[^a-zA-Z ]", "").toLowerCase().trim();
    }

    public static void filterStopwords(ArrayList<ArrayList<Token>> input, HashSet<Token> stopWords) {
        for (ArrayList<Token> sentence : input) {
            sentence.removeAll(stopWords);
        }
    }

//    private static ArrayList<String> lemmatize(ArrayList<String> input) {
//        
//    }
    
//    public static ArrayList<Token> snowballStem(ArrayList<Token> corpus) {
//        
//        
//    }
    
    
    
    
    public static HashSet<Token> getStopwords() {
        HashSet<Token> stopTokens = new HashSet<>();        
//        String stopwords = "the over i see but if th st few my show shows into which called first hundreds was said another from again one two three four five six seven eight nine ten thousands who were being been some let allow by with through almost completely years year while all he him not no those many are there here her she us his hers they theirs them in an of it and to is be at they take also put or a has their its as our on for have had out that would will we have";
        ArrayList<String> lines = read("stopwords_long.txt");
        for(String line : lines) {
            stopTokens.addAll(tokenize(line));
        }

        return stopTokens;
    }

    //Test
    public static void main(String[] args) {

        Stemmer stemmer = new Stemmer();
        
        ArrayList<String> input = read("newsarticle4.txt");
        
//        ArrayList<String> strings = filterCharacters(splitIntoSentences(input));
        ArrayList<String> strings = filterCharacters(splitIntoSentences(input));
        
        ArrayList<ArrayList<Token>> tokenizedCorpus = tokenize(strings);

        filterStopwords(tokenizedCorpus, getStopwords());

        for (ArrayList<Token> line : tokenizedCorpus) {
            for (Token token : line) {
                token.print();
                token.setSignature(stemmer.stem(token.getSignature()));
                token.print();
            }
            System.out.print("\n");
        }

        Network network = new Network(tokenizedCorpus);

        System.out.println("Edges: " + network.edges.size());
        
        network.writeEdgelist("output.dl");
    }

}
