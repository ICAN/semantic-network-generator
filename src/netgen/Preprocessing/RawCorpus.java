package netgen.Preprocessing;

//Base class extended by the more commonly used Corpus class 
public class RawCorpus
{
	
	// Fields set at point of importing
	private final String rawText;
	private final String source;
	private final String date;
	private final String title;
	private final String summary;
	private final String link;
	
	public RawCorpus(String inRaw, String inSource, String inDate, String inTitle, 
			String inSummary, String inLink)
	{
		rawText = inRaw;
		source = inSource;
		date = inDate;
		title = inTitle.replaceAll("[^A-Za-z0-9 ]", "").trim();
		summary = inSummary;
		link = inLink.trim();
	}
	

	// There should be no reassigning of class members after construction
	public String getRawText()
	{
		return rawText;
	}
	public String getDate()
	{
		return date;
	}
	public String getSource()
	{
		return source;
	}
	public String getTitle()
	{
		return title;
	}
	public String getSummary()
	{
		return summary;
	}
	public String getLink()
	{
		return link;
	}
	
}