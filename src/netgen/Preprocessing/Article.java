package netgen.Preprocessing;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import netgen.ChronologicallyComparable;
import netgen.YearMonthDayComparator;
import java.util.HashMap;

/* This class should make available text for processing.
 * A distinction should be made between raw text and text in the middle of 
 *		a processing pipeline.
 * The class extends RawArticle, it should always remember where it started
 * 		through the rawText field inherited by RawArticle superclass.
 */

public class Article extends RawArticle implements ChronologicallyComparable 
{
	private ArrayList<ArrayList<Token>> processedText;
	private HashMap<Token, Integer> tokenFrequency;
	private Calendar calendar;

	
	public Article(RawArticle inCorpus)
	{
		super(inCorpus.getRawText(), inCorpus.getSource(), inCorpus.getDate(), 
				inCorpus.getTitle(), inCorpus.getSummary(), inCorpus.getLink());
	}


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

	@Override
	public int compareTo(Object other) {
		YearMonthDayComparator comparator = new YearMonthDayComparator();
		return comparator.compare(this, other);
	}

	//ACCESSORS AND MUTATORS
	
	//Accepts only YYYY-MM-DD format
	//Warns & sets to "UNKOWN DATE" if provided non-matching string
	public void setDate(String date)
	{
		if (date.trim().matches("[0-9]{4}(-[0-9]{2}){2}")) {
			this.calendar.set(Integer.parseInt(date.substring(0, 3)),
					Integer.parseInt(date.substring(5, 6)),
					Integer.parseInt(date.substring(8, 9)));
		} else {
			this.calendar.set(0, 0, 0);
			System.out.println("Date? " + date);
		}
	}
	
	//Returns a set of all unique tokens in the corpus
	public HashSet<Token> getTokenSet() 
	{
		HashSet<Token> tokenSet = new HashSet<>();
		tokenSet.addAll(tokenFrequency.keySet());
		return tokenSet;
	}

	//Returns the total number of tokens in the processed text
	public int getTokenizedSize() 
	{
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



	public ArrayList<ArrayList<Token>> getProcessedText() {
		return processedText;
	}

	public void setProcessedText(ArrayList<ArrayList<Token>> inText)
	{
		processedText = inText;
	}

}
