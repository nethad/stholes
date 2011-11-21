package ch.uzh.ifi.dbimpl.stholes;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.dbimpl.stholes.DefaultDatabase;
import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class DefaultDatabaseTest {

	private DefaultDatabase database;

	@Before
	public void setup() {
		database = new DefaultDatabase();
	}

	@Test
	public void queryCount_one() {
		Query query = new Query(0.4, 0.6, 0.4, 0.6);
		assertThat(database.executeCountQuery(query), is(1));
	}

	@Test
	public void queryCount_one_smallSquare() {
		Query query = new Query(0.49, 0.51, 0.49, 0.51);
		assertThat(database.executeCountQuery(query), is(1));
	}

	@Test
	public void queryCount_one_squareIsPoint() {
		Query query = new Query(0.5, 0.5, 0.5, 0.5);
		assertThat(database.executeCountQuery(query), is(1));
	}

	@Test
	public void queryCount_zero() {
		Query query = new Query(0.3, 0.45, 0.3, 0.45);
		assertThat(database.executeCountQuery(query), is(0));
	}

	@Test
	public void queryCount_zero_smallSquare() {
		Query query = new Query(0.49, 0.499, 0.49, 0.499);
		assertThat(database.executeCountQuery(query), is(0));
	}

}
