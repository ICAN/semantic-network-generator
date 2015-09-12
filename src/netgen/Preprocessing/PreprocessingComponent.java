package netgen.Preprocessing;

public interface PreprocessingComponent <CorpusType>
{
	public CorpusType 	getProcessedCorpus();
	public String 		showRequiredTypes(); 
	
	
}