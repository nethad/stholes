import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class RandomDataGenerator {

	private static final String USERNAME = "SA";
	private static final String PASSWORD = "";
	private static final String TABLE_NAME = "test";
	private static final int NUMBER_OF_DATAPOINTS = 1000;
	private Connection connection;

	// private PreparedStatement selectStatement;

	public RandomDataGenerator() {

	}

	public void generateDataset() {
		System.out.println("Start generating dataset...");
		String dbFile = "db/random";
		try {
			// connection = DriverManager.getConnection("jdbc:hsqldb:file:"
			// + dbFile + ";ifexists=true", USERNAME, PASSWORD);
			connection = DriverManager.getConnection("jdbc:hsqldb:file:"
					+ dbFile, USERNAME, PASSWORD);

			System.out.println("Create table...");
			long start = System.currentTimeMillis();
			createTable();
			connection.commit();
			System.out.println("Insert random values...");
			insertRandomValues();
			connection.commit();
			long end = System.currentTimeMillis();
			connection.close();
			System.out.println("Done. (" + (end - start) + "ms)");
		} catch (SQLException e) {
			System.err.println("SQLException: " + e.getMessage());
		}
	}

	private void insertRandomValues() throws SQLException {
		Random randomGenerator = new Random();
		PreparedStatement insertStatement = connection
				.prepareStatement("insert into " + TABLE_NAME
						+ " (x, y) values (?,?)");

		for (int i = 0; i < NUMBER_OF_DATAPOINTS; i++) {
			insertStatement.setDouble(1, randomGenerator.nextDouble());
			insertStatement.setDouble(2, randomGenerator.nextDouble());
			insertStatement.addBatch();
		}
		insertStatement.executeBatch();
	}

	private void createTable() throws SQLException {
		Statement createStatement = connection.createStatement();
		createStatement.execute("create table " + TABLE_NAME
				+ " (id integer identity, x double, y double);");

	}

	public static void main(String[] args) {
		new RandomDataGenerator().generateDataset();
	}

}
