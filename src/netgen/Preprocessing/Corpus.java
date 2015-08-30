package netgen.Preprocessing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import netgen.ChronologicallyComparable;
import netgen.YearMonthDayComparator;
import java.util.HashMap;

/* This class should make available text for processing.
 *A distinction should be made between raw text and text in the middle of 
 *		a processing pipeline.
 * The class extends RawCorpus, it should always remember where it started
 * 		through the rawText field inherited by RawCorpus superclass.
 * Honestly, this class could be renamed to Article since it assumes every 
 * 		incoming instance has a summary, title, link etc.
 */

public class Corpus extends RawCorpus implements ChronologicallyComparable {

    //CLASS MEMBERS
	private ArrayList<ArrayList<Token>> processedText;
    private HashMap<Token, Integer> tokenFrequency;
    private Calendar calendar;


    //CONSTRUCTORS & ASSOCIATED METHODS
    public Corpus(String inRaw, String inSource, String inDate, String inTitle, 
    		String inSummary, String inLink) 
    {
    	super(inRaw,inSource,inDate, inTitle, inSummary.trim(), inLink);       
    }
    
    public Corpus(RawCorpus inCorpus)
    {
    	super(inCorpus.getRawText(), inCorpus.getSource(), inCorpus.getDate(), 
    			inCorpus.getTitle(), inCorpus.getSummary(), inCorpus.getLink());
    }

//Accepts only YYYY-MM-DD format
//Warns & sets to "UNKOWN DATE" if provided nonmatching string
    public void setDate(String date) {
    	if (date.trim().matches("[0-9]{4}(-[0-9]{2}){2}")) {
    		this.calendar.set(Integer.parseInt(date.substring(0, 3)),
    				Integer.parseInt(date.substring(5, 6)),
    				Integer.parseInt(date.substring(8, 9)));
    	} else {
    		this.calendar.set(0, 0, 0);
    		System.out.println("Date? " + date);
    	}
    }
    
    /*
//Accepts only alphanumeric characters and spaces
//Filters out all other characters
    public void setTitle(String title) {
    	this.title = title.replaceAll("[^A-Za-z0-9 ]", "").trim();
    }
    
    
    public void setLink(String link) {
    	this.link = link.trim();
    	if (!this.link.matches("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
    		System.out.println("Link? " + this.link);
    	}
    }
    */
    
    
    //////////TEXT PROCESSING METHODS///////////
    public void generateFrequencyMap() {
        HashMap<Token, Integer> map = new HashMap<>();

        for (ArrayList<Token> tokens : processedText) {
            for (int i = 0; i < tokens.size(); i++) {
                if (map.containsKey(tokens.get(i))) {
                    int value = map.get(tokens.get(i)) + 1;
                    map.put(tokens.get(i), value);
                } else {
                    map.put(tokens.get(i), 1);
                }
            }
        }
        tokenFrequency = map;
    }

    


    //ACCESSORS AND MUTATORS
    //Returns a set of all unique tokens in the corpus
    public HashSet<Token> getTokenSet() {
        HashSet<Token> tokenSet = new HashSet<>();
        tokenSet.addAll(tokenFrequency.keySet());
        return tokenSet;
    }

    //Returns the total number of tokens in the processed text
    public int getTokenizedSize() {
        int count = 0;
        for (ArrayList<Token> sentence : this.processedText) {
            count += sentence.size();
        }
        return count;
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH);
    }
    
    public int getDayOfMonth() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    
    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }
    

    @Override
    public int compareTo(Object other) {
        YearMonthDayComparator comparator = new YearMonthDayComparator();
        return comparator.compare(this, other);
    }
    
    public ArrayList<ArrayList<Token>> getProcessedText() {
        return processedText;
    }

    public void setProcessedText(ArrayList<ArrayList<Token>> inText)
    {
    	processedText = inText;
    }

}
