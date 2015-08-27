package netgen;

import java.util.Calendar;

/**
 *
 * @author Neal
 */
public interface ChronologicallyComparable extends Comparable {
    
    public abstract int getMonth();

    public abstract int getDayOfMonth();

    public abstract int getYear();

}
