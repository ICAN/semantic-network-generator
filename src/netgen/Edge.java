package netgen;

public class Edge implements Comparable {
    
    private TokenPair pair;
    private double weight;
    
    
    public Edge(TokenPair pair, double weight) {
        this.pair = pair;
        this.weight = weight;
    }
    
    public Edge(String a, String b) {
        pair = new TokenPair(a, b);
        weight = 0;
    }
    
    //Compares by edge weight
    @Override
    public int compareTo(Object other) throws NullPointerException, ClassCastException {
        if (other == null) {
            throw new NullPointerException();
        }
        if (!other.getClass().equals(this.getClass())) {
            throw new ClassCastException();
        }
        Edge otherEdge = (Edge) other;
        
        if (this.getWeight() > otherEdge.getWeight()) {
            return 1;
        } else if (this.getWeight() < otherEdge.getWeight()) {
            return -1;
        } else {
            return 0;
        }

    }
    
    //Warning: Edges are checked for equality and hashed by their incident vertices
    @Override
    public boolean equals(Object other) {
        if(other.getClass() != this.getClass()) {
            return false;
        } else if (pair.getA().getSignature().equalsIgnoreCase(((Edge)other).getIncidentTokens().getA().getSignature())
                && pair.getB().getSignature().equalsIgnoreCase(((Edge)other).getIncidentTokens().getB().getSignature())) {
            return true;
        } else {
            return false;
        }
    }
    
    //Warning: Edges are checked for equality and hashed by their incident vertices
    @Override
    public int hashCode() {
        return (pair.getA().hashCode()/2 + pair.getA().hashCode()%2 - pair.getB().hashCode()%2 + pair.getB().hashCode()/2);
    }

    /**
     * @return the pair
     */
    public TokenPair getIncidentTokens() {
        return pair;
    }

    /**
     * @param pair the pair to set
     */
    public void setPair(TokenPair pair) {
        this.pair = pair;
    }

    /**
     * @return the weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight the weight to set
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    
    
}
