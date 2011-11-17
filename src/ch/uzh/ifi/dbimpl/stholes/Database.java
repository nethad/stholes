package ch.uzh.ifi.dbimpl.stholes;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

public interface Database {

    public int executeCountQuery(Query query);

}
