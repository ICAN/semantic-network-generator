
package netgen.Preprocessing;

/**
 *
 * @author Neal
 */
public interface Stemmer {
    
    //Stemmers gotta stem(word)
    public abstract String stem(String word);
    
}
