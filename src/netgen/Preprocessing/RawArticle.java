package netgen.Preprocessing;

//Base class extended by the more commonly used Corpus class 
public class RawArticle
{
	
	// Fields set at point of importing
	private final String rawText;
	private final String source;
	private final String date;
	private final String title;
	private final String summary;
	private final String link;
	
	public RawArticle(String inRaw, String inSource, String inDate, String inTitle, 
			String inSummary, String inLink)
	{
		rawText = inRaw;
		source = inSource;
		date = inDate;
		title = initTitle(inTitle);
		summary = inSummary;
		link = initLink(inLink);
	}
	
		//Accepts only alphanumeric characters and spaces
		//Filters out all other characters
	    private String initTitle(String inTitle) 
	    {
	    	return inTitle.replaceAll("[^A-Za-z0-9 ]", "").trim();
	    }


	    private String initLink(String inLink) 
	    {
	    	 return inLink.trim();
	    	//if (!this.link.matches("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
	    	//	System.out.println("Link? " + this.link);}
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