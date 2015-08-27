
package netgen;

//Composed of two tokens; used to form edges in a semantic network
public class TokenPair {
    private Token a;
    private Token b;

    /**
     * @return the a
     */
    public Token getA() {
        return a;
    }

    /**
     * @param a the a to set
     */
    public void setA(Token a) {
        this.a = a;
    }

    /**
     * @return the b
     */
    public Token getB() {
        return b;
    }

    /**
     * @param b the b to set
     */
    public void setB(Token b) {
        this.b = b;
    }
    
    public TokenPair(Token a, Token b) {
        if(a.getSignature().compareTo(b.getSignature()) > 0) {
            this.a = new Token(a.getSignature());
            this.b = new Token(b.getSignature());
        } else {
            this.a = new Token(b.getSignature());
            this.b = new Token(a.getSignature());
        }
    }
    
    public TokenPair(String a, String b) {
        if(a.compareTo(b) > 0) {
            this.a = new Token(a);
            this.b = new Token(b);
        } else {
            this.a = new Token(b);
            this.b = new Token(a);
        }    
    }
    
    

    
}