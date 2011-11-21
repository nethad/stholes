package ch.uzh.ifi.dbimpl.stholes;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

public interface QueryGenerator {

    public Query nextQuery();

    public boolean hasNextQuery();

}
