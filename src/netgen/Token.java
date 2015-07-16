
package netgen;

/**
 *
 * @author Neal
 */
public class Token {
    
    private String signature;

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public Token(String signature) {
        this.signature = signature;
    }
    
}
