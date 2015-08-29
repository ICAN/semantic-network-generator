package netgen.Preprocessing;

import java.util.HashSet;

public class Token {

    private String signature;
//    private HashSet<Tag> tags;

    private HashSet<Tag> tagset;

    //CONSTRUCTORS
    public Token(String signature) {
        this.setSignature(signature);
        tagset = new HashSet<>();
    }
    
    //ACCESSORS AND MUTATORS
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

    /**
     * @return the tagset
     */
    public HashSet<Tag> getTagset() {
        return (HashSet<Tag>)tagset.clone();
    }

    /**
     * @param tagset the tagset to set
     */
    public void setTagset(HashSet<Tag> tagset) {
        this.tagset = tagset;
    }

    public void tag(Tag tag) {
        tagset.add(tag);
    }
    
    public void removeTag(Tag tag) {
        tagset.remove(tag);
    }
    
    @Override
    //Warning: uses only the signature, not the tagset!
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass()) {
            return false;
        } else if (this.signature.equalsIgnoreCase(((Token) other).getSignature())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    @Override
    //Clones both signature and tagset
    public Token clone() {
        Token token = new Token(this.getSignature());
        token.setTagset(this.getTagset()); //Safe because getTagset() returns cloned hashset
        return token;
    }

    public void print() {
        System.out.print(this.signature + " ");
    }

    public enum Tag {
        NAMED_ENTITY,
        ADJ,
        ADV,
        NOUN,
        VERB,
        STOPWORD,
    }

}
