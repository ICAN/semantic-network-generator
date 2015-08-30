package netgen.Preprocessing;

//Base class extended by the more commonly used Corpus class 
public class RawCorpus
{
	
	private final String rawText;
	private final String source;
	private final String date;
	
	
	public RawCorpus(String inRaw, String inSource, String inDate)
	{
		rawText = inRaw;
		source = inSource;
		date = inDate;
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
	
	
	
	
	
	
}