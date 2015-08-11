package netgen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import porter.Stemmer;
import java.util.HashMap;

public class Corpus {

    private String rawText;
    private ArrayList<ArrayList<Token>> processedText;
    private String date;
    private String summary;
    private String title;
    private String link;
//    public HashMap<String, String> attributes;
    private HashMap<Token, Integer> tokenFrequency;
    private HashSet<Token> stopwords;
    private Stemmer stemmer;

    //CONSTRUCTORS & ASSOCIATED METHODS
    public Corpus(String text) {
        rawText = text.trim();
//        attributes = new HashMap<String, String>();
        title = "";
        link = "";
        date = "";
        summary = "";
    }

    //CORPUS PROCESSING METHODS
    //Splits corpus into strings consisting of complete sentences
    //Spltis on both periods and semicolons
    private static ArrayList<String> splitSentences(ArrayList<String> corpus) {
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
    private static ArrayList<String> splitSentences(String corpus) {
        ArrayList<String> list = new ArrayList<String>(1);
        list.add(corpus);
        return Corpus.splitSentences(list);
    }

    //Modified version which takes an ArrayList of strings
    private static ArrayList<ArrayList<Token>> tokenize(ArrayList<String> input) {
        ArrayList<ArrayList<Token>> output = new ArrayList<>();

        for (String line : input) {
            output.add(Corpus.tokenize(line));
        }
        return output;
    }

    //Takes a filtered sentence and returns its contents as a list of tokens
    //Possible alternative: return a token set rather than a token list?
    private static ArrayList<Token> tokenize(String input) {
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
    //NOTE: The list may no longer be in the same order
    //TODO: There's probably a faster & less offensive way to do this
    private static void removeDuplicates(ArrayList<Token> tokenlist) {
        HashSet<Token> tokenset = new HashSet<>();
        tokenset.addAll(tokenlist);
        tokenlist = new ArrayList<>();
        tokenlist.addAll(tokenset);
    }

    //Filters out everything but spaces, letters
    //Trims and converts letters to lower-case
    private static String makeFilteredString(String input) {
        return input.replaceAll("[^a-zA-Z ]", " ").toLowerCase().trim();
    }

    //Filters out everything but spaces, letters
    //Trims and converts to lower-case
    private static ArrayList<String> makeFilteredStrings(ArrayList<String> input) {
        ArrayList<String> output = new ArrayList<>();
        for (String line : input) {
            output.add(Corpus.makeFilteredString(line));
        }
        return output;
    }

    //Removes stopwords from this processed text
    private void removeStopwords() {
        for (ArrayList<Token> sentence : this.processedText) {
            sentence.removeAll(this.stopwords);
        }
    }

    private void generateFrequencyMap() {
        HashMap<Token, Integer> map = new HashMap<>();

        for (ArrayList<Token> tokens : this.processedText) {
            for (int i = 0; i < tokens.size(); i++) {
                if (map.containsKey(tokens.get(i))) {
                    int value = map.get(tokens.get(i)) + 1;
                    map.put(tokens.get(i), value);
                } else {
                    map.put(tokens.get(i), 1);
                }
            }
        }
        this.tokenFrequency = map;
    }

    //Processes the rawText into the filtered, split, tokenized processedText
    private void processText() {
        ArrayList<ArrayList<Token>> processed = Corpus.tokenize(makeFilteredStrings(Corpus.splitSentences(rawText)));
        this.stopwords = stopwords;
        this.removeStopwords();

        for (ArrayList<Token> line : processed) {
            for (Token token : line) {
                token.setSignature(stemmer.stem(token.getSignature()));
            }
        }
        this.processedText = processed;
    }

    //Conducts all processing activities on the corpus
    public void process(Stemmer stemmer, HashSet<Token> stopwords) {
        this.stemmer = stemmer;
        this.stopwords = stopwords;
        this.processText();
        this.generateFrequencyMap();

    }

    //ACCESSORS AND MUTATORS
    public String getDate() {
        return date;
    }

    public HashSet<Token> getTokenSet() {
        return (HashSet<Token>) this.tokenFrequency.keySet();
    }

    //Returns the number of tokens in the processed text
    public int getTokenizedSize() {
        int count = 0;
        for (ArrayList<Token> sentence : this.processedText) {
            count += sentence.size();
        }
        return count;
    }

    //Accepts only YYYY-MM-DD format
    //Warns & sets to "UNKOWN DATE" if provided nonmatching string
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

    public void setStopwords(HashSet<Token> stopwords) {
        this.stopwords = stopwords;
    }

    //MAIN METHOD
    public static void main(String[] args) {

        String name = "adn";
        Stemmer stemmer = new Stemmer();
        HashSet<Token> stopwords = new HashSet<>();
        stopwords.addAll(Corpus.tokenize(IO.readFileAsString("stopwords_long.txt")));

        System.out.println("Importing and splitting into corpora");
        ArrayList<Corpus> corpora = IO.importCorpora(IO.readFileAsLines(name + ".csv"), name);
        System.out.println("Split into " + corpora.size() + " corpora");

//        Corpus corpus = corpora.get(115);
        for (Corpus corpus : corpora) {
            corpus.process(stemmer, stopwords);

//        System.out.println("Raw text: ");
//        for(int i = 120; i < corpus.rawText.length(); i+= 120) {
//            System.out.println(corpus.rawText.substring(i-120, i));
//        }
            ArrayList<String> sentences = Corpus.splitSentences(corpus.getRawText());
            Corpus.makeFilteredStrings(sentences);

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
