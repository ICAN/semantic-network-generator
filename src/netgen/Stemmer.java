
package netgen;

/**
 *
 * @author Neal
 */
public interface Stemmer {
    
    //Stemmers gotta stem()
    public abstract String stem(String word);
    
}
