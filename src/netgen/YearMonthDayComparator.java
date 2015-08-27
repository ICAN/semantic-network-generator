package netgen;

import java.util.Comparator;

//For Corpora. Maybe also for networks.
public class YearMonthDayComparator implements Comparator {

        @Override
        public int compare(Object a, Object b) throws NullPointerException, ClassCastException {
            
            if (a == null || b == null) {
                throw new NullPointerException();
            }
            if (!a.getClass().equals(b.getClass())) {
                throw new ClassCastException();
            }
            
            ChronologicallyComparable dateA = (ChronologicallyComparable)a;
            ChronologicallyComparable dateB = (ChronologicallyComparable)b;
            
            if (dateA.getYear() > dateB.getYear()) {
                return 1;
            } else if (dateA.getYear() < dateB.getYear()) {
                return -1;
            } else if (dateA.getMonth() > dateB.getMonth()) {
                return 1;
            } else if (dateA.getMonth() < dateB.getMonth()) {
                return -1;
            } else if (dateA.getDayOfMonth() > dateB.getDayOfMonth()) {
                return 1;
            } else if (dateA.getDayOfMonth() < dateB.getDayOfMonth()) {
                return -1;
            } else {
                return 0;
            }
        }

    }

