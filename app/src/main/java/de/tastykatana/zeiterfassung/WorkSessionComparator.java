package de.tastykatana.zeiterfassung;

/**
 * Created by matthias on 2/10/17.
 */
public class WorkSessionComparator implements java.util.Comparator<WorkSession> {

    @Override
    public int compare(WorkSession o1, WorkSession o2) {
        return o1.getStart().compareTo(o2.getStart());
    }
}
