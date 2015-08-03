
package netgen;


public class Tag {
    
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
    
    
    public Tag (String signature) {
        this.signature = signature;
    }
    
}
