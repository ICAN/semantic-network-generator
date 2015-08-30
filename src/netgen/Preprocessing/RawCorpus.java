package netgen.Preprocessing;

// This class needs to provide the raw article text to a preprocessing 
// component that requests it
public class RawCorpus
{
	
	private String rawText;
	private String source;
	private String date;
	
	
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