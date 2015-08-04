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

    public String rawText;
    public ArrayList<ArrayList<Token>> processedText;
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
        title = "";
        link = "";
        date = "";
        summary = "";
    }

    //INPUT
    //Reads a file & returns each line in the file as a string in an arraylist
    public static ArrayList<String> read(String fileName) {
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

    //Produces an arraylist of corpora from 
    public static ArrayList<Corpus> splitIntoCorpora(ArrayList<String> lines, String mode) {

        ArrayList<Corpus> corpora = new ArrayList<>();
        ArrayList<String> rows = new ArrayList<>();
        ArrayList<String> fields = new ArrayList<>();

        String condensed = "";
        for (String line : lines) {
            condensed += ("\n" + line);
        }

        rows.addAll(Arrays.asList(condensed.split("<ROW>")));

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

    //Splits corpus into strings consisting of complete sentences
    //Spltis on both periods and semicolons
    public static ArrayList<String> splitIntoSentences(ArrayList<String> corpus) {
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
    public static ArrayList<String> splitIntoSentences(String corpus) {
        ArrayList<String> list = new ArrayList<String>(1);
        list.add(corpus);
        return Corpus.splitIntoSentences(list);
    }

    //Modified version which takes an ArrayList of strings
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
        return input.replaceAll("[^a-zA-Z ]", " ").toLowerCase().trim();
    }

    public static void filterStopwords(ArrayList<ArrayList<Token>> input, HashSet<Token> stopWords) {
        for (ArrayList<Token> sentence : input) {
            sentence.removeAll(stopWords);
        }
    }

    public static HashSet<Token> importStopwordSet(String fileName) {
        HashSet<Token> stopTokens = new HashSet<>();
//        String stopwords = "the over i see but if th st few my show shows into which called first hundreds was said another from again one two three four five six seven eight nine ten thousands who were being been some let allow by with through almost completely years year while all he him not no those many are there here her she us his hers they theirs them in an of it and to is be at they take also put or a has their its as our on for have had out that would will we have";
        ArrayList<String> lines = read(fileName);
        for (String line : lines) {
            stopTokens.addAll(tokenize(line));
        }

        return stopTokens;
    }

    //Mainly for testing
    public static void processSingleText(String fileName) {

        Stemmer stemmer = new Stemmer();

        ArrayList<String> input = read(fileName);
        ArrayList<String> strings = filterCharacters(splitIntoSentences(input));
        ArrayList<ArrayList<Token>> tokenizedCorpus = tokenize(strings);
        filterStopwords(tokenizedCorpus, importStopwordSet("stopwords_long.txt"));

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

    //Processes this corpus
    public void processText(Stemmer stemmer, HashSet<Token> stopwords) {

        processedText = tokenize(filterCharacters(splitIntoSentences(rawText)));
        filterStopwords(processedText, stopwords);

        for (ArrayList<Token> line : processedText) {
            for (Token token : line) {
                token.setSignature(stemmer.stem(token.getSignature()));
            }
        }
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date.trim();
        if (!this.date.matches("[0-9]{4}(-[0-9]{2}){2}")) {
            System.out.println("Date? " + date);
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

    public void setTitle(String title) {
        this.title = title.replaceAll("[^A-Za-z ]", "").trim();
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
        for (ArrayList<Token> sentence : this.processedText) {
            for (Token token : sentence) {
                tokens.add((token));
            }
        }
        return tokens;
    }

    
    
    public static void main(String[] args) {

        String name = "adn";
        Stemmer stemmer = new Stemmer();
        HashSet<Token> stopwords = Corpus.importStopwordSet("stopwords_long.txt");
        ArrayList<String> input = read(name + ".csv");

        System.out.println("Splitting into corpora");
        ArrayList<Corpus> corpora = splitIntoCorpora(input, name);
        System.out.println("Split into " + corpora.size() + " corpora");

//        Corpus corpus = corpora.get(115);
        for (Corpus corpus : corpora) {
            corpus.processText(stemmer, stopwords);

//        System.out.println("Raw text: ");
//        for(int i = 120; i < corpus.rawText.length(); i+= 120) {
//            System.out.println(corpus.rawText.substring(i-120, i));
//        }
            ArrayList<String> sentences = Corpus.splitIntoSentences(corpus.rawText);
            Corpus.filterCharacters(sentences);

//            System.out.println("Sentences: ");
//            for (String sentence : sentences) {
//                System.out.println(sentence);
//            }

            System.out.println("Unique tokens: " + corpus.getTokenSet().size());

            System.out.println("Tokenized Sentences: " + corpus.processedText.size());
//            for (ArrayList<Token> sentence : corpus.processedText) {
//                for (Token token : sentence) {
//                    token.print();
//                }
//                System.out.println();
//            }

            Network network = new Network(corpus.processedText);
            System.out.println("Created network");
            int end = corpus.title.length();
            if(end > 30) {
                end = 30;
            }
            network.writeEdgelist(name + "." + corpus.date + "." + corpus.title.substring(0, end));
        }

    }

}
