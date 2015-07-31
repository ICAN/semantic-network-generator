package netgen;

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
        this.signature = signature.trim().toLowerCase();
    }
    
    public Token(String signature) {
        this.signature = signature.trim().toLowerCase();
    }

    @Override
    public boolean equals(Object other) {
        if(other.getClass() != this.getClass()) {
            return false;
        } else if (this.signature.equalsIgnoreCase(((Token)other).getSignature())) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return signature.hashCode();
    }
    
    
    public void print() {
        System.out.print(this.signature + " ");
    }
    
}
