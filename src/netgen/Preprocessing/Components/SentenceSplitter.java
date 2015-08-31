package netgen.Preprocessing.Components;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netgen.Preprocessing.PreprocessingComponent;

public class SentenceSplitter implements PreprocessingComponent<ArrayList<String>>
{
	
	private final ArrayList<String> splitCorpus;
	
	public SentenceSplitter(ArrayList<String> inCorpus)
	{
		splitCorpus = splitSentences(inCorpus);
	}
	public SentenceSplitter(String inCorpus)
	{
		splitCorpus = splitSentences(inCorpus);
	}
	
	
	//Splits corpus into strings consisting of complete sentences
    //Splits on both periods and semicolons
    public ArrayList<String> splitSentences(ArrayList<String> corpus) {
        ArrayList<String> sentences = new ArrayList<>();

        String preCompletion = "( ([A-Za-z0-9,]|\\#|-|'){2,}?)";
        String completion = "(\\.|;|\\?|!)+"; //Valid completion characters for a sentence; 1 or more required, any composition permitted
        String postCompletion = "((\\\"|\\\'|[0-9]| |\\z|$|\\n){0,3})"; //Post-sentence characters permitted; extremely tolerant

        for (String line : corpus) {
            Pattern pattern = Pattern.compile(preCompletion + completion + postCompletion);
            Matcher m = pattern.matcher(line);
            int index = 0;
            while (m.find()) {
                String s = line.substring(index, m.end());
                if (!s.matches(".* (Mr|Mrs|Ms|Dr|Rev|Esq|Mass|Conn).( |,)") //Avoid matching on abbreviated titles 
                        //TODO: Make this list of titles more comprehensive
                        //TODO: 
                        && !s.matches(".*[0-9]+(\\.|,)[0-9]+")) //Avoid matching decimal numbers
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
	
    //Single string version of sentence splitter
    public ArrayList<String> splitSentences(String line) {
    	
        ArrayList<String> list = new ArrayList<String>();
        list.add(line);
        list = splitSentences(list);
        return list;
    }

	
	public String showRequiredTypes()
	{
		return "ArrayList<String>, String";
	}
	
	public ArrayList<String> getProcessedCorpus()
	{
		return splitCorpus;
	}
	
}