package netgen;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import porter.Stemmer;
import java.lang.String;
import java.util.Arrays;
import java.util.HashMap;

public class Corpus {

    private String rawText;
    private ArrayList<ArrayList<Token>> processedText;
    private String date;
    private String summary;
    private String title;
    private String link;
//    public HashMap<String, String> attributes;
//    HashMap<Token, Integer> frequency = new HashMap<>();

    //CONSTRUCTORS
    public Corpus(String text) {
        rawText = text.trim();
//        attributes = new HashMap<String, String>();
        processedText = new ArrayList<>();
        title = "";
        link = "";
        date = "";
        summary = "";
    }

    //IMPORT METHODS
    //Reads a file & returns each line in the file as a string in an arraylist
    public static ArrayList<String> readFileAsLines(String fileName) {
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

    //Reads the specified file then combines its lines into a single string using combine() method
    public static String readFileAsString(String fileName) {
        return combine(readFileAsLines(fileName));
    }

    //Reads a text file, tokenizes it, and returns a set of those tokens
    public static HashSet<Token> importStopwords(String fileName) {
        HashSet<Token> stopTokens = new HashSet<>();
        ArrayList<String> lines = readFileAsLines(fileName);
        for (String line : lines) {
            stopTokens.addAll(Corpus.getTokenized(line));
        }

        return stopTokens;
    }

    //Combines the lines of the lines of the arraylist into a single String, separated by newline characters
    public static String combine(ArrayList<String> lines) {
        String condensed = "";
        for (String line : lines) {
            condensed += ("\n" + line);
        }
        return condensed;
    }

    //Produces an arraylist of KTUU, ADN or Homer Tribune corpora 
    //from unprocessed lines of text
    public static ArrayList<Corpus> getCorpora(ArrayList<String> lines, String mode) {

        ArrayList<Corpus> corpora = new ArrayList<>();
        ArrayList<String> rows = new ArrayList<>();
        ArrayList<String> fields = new ArrayList<>();

        String combinedLines = combine(lines);

        rows.addAll(Arrays.asList(combinedLines.split("<ROW>")));

        for (String row : rows) {
            fields.addAll(Arrays.asList(row.split("<COL>")));
        }

        //debug
//        System.out.println("Fields: " + fields.size());
//
//        int count = 1;
//        for (String field : fields) {
//            System.out.println("Field " + count + ": " + field);
//            count++;
//        }
        if (mode.equalsIgnoreCase("adn")) {

            //in DB dump: Article, Link, Title, Date
            for (int i = 3; i < fields.size(); i += 4) {
                Corpus corpus = new Corpus(fields.get(i - 3));
                corpus.setLink(fields.get(i - 2));
                corpus.setTitle(fields.get(i - 1));
                corpus.setDate(fields.get(i));
                corpora.add(corpus);
            }

        } else if (mode.equalsIgnoreCase("ktuu")) {

            //in DB dump: Link, Summary, Article, Title, Date
            for (int i = 4; i < fields.size(); i += 5) {
                Corpus corpus = new Corpus(fields.get(i - 2));
                corpus.setDate(fields.get(i));
                corpus.setLink(fields.get(i - 4));
                corpus.setSummary(fields.get(i - 3));
                corpus.setTitle(fields.get(i - 1));
                corpora.add(corpus);
            }

        } else if (mode.equalsIgnoreCase("tribune")) {

            //in DB dump: Link, Article, Title, Date
            for (int i = 3; i < fields.size(); i += 4) {
                Corpus corpus = new Corpus(fields.get(i - 2));
                corpus.setLink(fields.get(i - 3));
                corpus.setTitle(fields.get(i - 1));
                corpus.setDate(fields.get(i));
                corpora.add(corpus);
            }

        } else {
            System.out.println("Unrecognized type of input file");
            System.exit(-1);
        }

        return corpora;
    }

    //CORPUS PARSING AND FILTERING METHODS
    //Splits corpus into strings consisting of complete sentences
    //Spltis on both periods and semicolons
    public static ArrayList<String> getSentences(ArrayList<String> corpus) {
        ArrayList<String> sentences = new ArrayList<>();

        //TODO: ADD PREFILTERING FOR UNWANTED SPECIAL CHARACTERS
        //TODO: FIX POST-COMPLETION?
        //TODO: Probably completely revamp this
        String preCompletion = "( ([A-Za-z0-9,]|\\#|-|'){2,}?)";
        String completion = "(\\.|;|\\?|!)+";
        String postCompletion = "((\\\"|\\\'|[0-9]| |\\z|$|\\n){0,3})";

        for (String line : corpus) {
            Pattern pattern = Pattern.compile(preCompletion + completion + postCompletion);
            Matcher m = pattern.matcher(line);
            int index = 0;
            while (m.find()) {
                String s = line.substring(index, m.end());
                if (!s.matches(".* (Mr|Mrs|Ms|Dr|Rev|Esq|Mass|Conn).( |,)") //Avoid matching on abbreviated titles
                        && !s.matches(".*[0-9]+(\\.|,)[0-9]+ ")) //Avoid matching numerical information
                {
                    if (s.length() > 1) { //avoid adding empty sentences
                        sentences.add(s.trim());
                        index = m.end();
                    }
                }
            }
        }

        return sentences;
    }

    //Modified version which takes a single string
    public static ArrayList<String> getSentences(String corpus) {
        ArrayList<String> list = new ArrayList<String>(1);
        list.add(corpus);
        return Corpus.getSentences(list);
    }

    //Modified version which takes an ArrayList of strings
    public static ArrayList<ArrayList<Token>> getTokenized(ArrayList<String> input) {
        ArrayList<ArrayList<Token>> output = new ArrayList<>();

        for (String line : input) {
            output.add(Corpus.getTokenized(line));
        }
        return output;
    }

    //Takes a filtered sentence and returns its contents as a list of tokens
    //Possible alternative: return a token set rather than a token list?
    public static ArrayList<Token> getTokenized(String input) {
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

    //Eliminates duplicates from a list of tokens
    //TODO: There's probably a faster & less offensive way to do this
    public static void removeDuplicates(ArrayList<Token> tokenlist) {
        HashSet<Token> tokenset = new HashSet<>();
        tokenset.addAll(tokenlist);
        tokenlist = new ArrayList<>();
        tokenlist.addAll(tokenset);
    }

    //Filters out everything but spaces, letters
    //Trims and converts to lower-case
    public static String getFilteredString(String input) {
        return input.replaceAll("[^a-zA-Z ]", " ").toLowerCase().trim();
    }

    //Filters out everything but spaces, letters
    //Trims and converts to lower-case
    public static ArrayList<String> getFilteredStrings(ArrayList<String> input) {
        ArrayList<String> output = new ArrayList<>();
        for (String line : input) {
            output.add(Corpus.getFilteredString(line));
        }
        return output;
    }

    //Removes stopwords from input list of lists of tokens
    public static void removeStopwords(ArrayList<ArrayList<Token>> input, HashSet<Token> stopwords) {
        for (ArrayList<Token> sentence : input) {
            sentence.removeAll(stopwords);
        }
    }

    //Removes stopwords from this processed text
    public void removeStopwords(HashSet<Token> stopwords) {
        removeStopwords(this.getProcessedText(), stopwords);
    }

    //Processes this corpus
    public void process(Stemmer stemmer, HashSet<Token> stopwords) {
        this.setProcessedText(Corpus.getProcessedText(this.getRawText(), stemmer, stopwords));
    }

    public static ArrayList<ArrayList<Token>> getProcessedText(String rawText, Stemmer stemmer, HashSet<Token> stopwords) {
        ArrayList<ArrayList<Token>> processedText = getTokenized(getFilteredStrings(getSentences(rawText)));
        removeStopwords(processedText, stopwords);

        for (ArrayList<Token> line : processedText) {
            for (Token token : line) {
                token.setSignature(stemmer.stem(token.getSignature()));
            }
        }

        return processedText;
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

    private HashMap<Token, Integer> getFrequencyMap() {
        return getFrequencyMap(this.getProcessedText());
    }
    
    //ACCESSORS AND MUTATORS

    public String getDate() {
        return date;
    }

    //Accepts only YYYY-MM-DD format
    //Exits if provided nonmatching string
    public void setDate(String date) {
        date = date.trim();
        if (date.matches("[0-9]{4}(-[0-9]{2}){2}")) {
            this.date = date;
        } else {
            System.out.println("Date? " + date);
            this.date = "UNKNOWN DATE";
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary.trim();
    }

    public String getTitle() {
        return title;
    }

    //Accepts only alphanumeric characters and spaces
    //Filters out all other characters
    public void setTitle(String title) {
        this.title = title.replaceAll("[^A-Za-z0-9 ]", "").trim();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link.trim();
        if (!this.link.matches("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            System.out.println("Link? " + this.link);
        }

    }

    public HashSet<Token> getTokenSet() {
        HashSet<Token> tokens = new HashSet<>();
        for (ArrayList<Token> sentence : this.getProcessedText()) {
            for (Token token : sentence) {
                tokens.add((token));
            }
        }
        return tokens;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public ArrayList<ArrayList<Token>> getProcessedText() {
        return processedText;
    }

    public void setProcessedText(ArrayList<ArrayList<Token>> processedText) {
        this.processedText = processedText;
    }

    //MAIN METHOD
    
    public static void main(String[] args) {

        String name = "adn";
        Stemmer stemmer = new Stemmer();
        HashSet<Token> stopwords = Corpus.importStopwords("stopwords_long.txt");
        ArrayList<String> input = readFileAsLines(name + ".csv");

        System.out.println("Splitting into corpora");
        ArrayList<Corpus> corpora = getCorpora(input, name);
        System.out.println("Split into " + corpora.size() + " corpora");

//        Corpus corpus = corpora.get(115);
        for (Corpus corpus : corpora) {
            corpus.process(stemmer, stopwords);

//        System.out.println("Raw text: ");
//        for(int i = 120; i < corpus.rawText.length(); i+= 120) {
//            System.out.println(corpus.rawText.substring(i-120, i));
//        }
            ArrayList<String> sentences = Corpus.getSentences(corpus.getRawText());
            Corpus.getFilteredStrings(sentences);

//            System.out.println("Sentences: ");
//            for (String sentence : sentences) {
//                System.out.println(sentence);
//            }
            System.out.println("Unique tokens: " + corpus.getTokenSet().size());

            System.out.println("Tokenized Sentences: " + corpus.getProcessedText().size());
//            for (ArrayList<Token> sentence : corpus.processedText) {
//                for (Token token : sentence) {
//                    token.print();
//                }
//                System.out.println();
//            }

            Network network = new Network(corpus.getProcessedText());
            System.out.println("Created network");
            int end = corpus.title.length();
            if (end > 30) {
                end = 30;
            }
            network.writeEdgelist(name + "." + corpus.date + "." + corpus.title.substring(0, end));
        }

    }

}
