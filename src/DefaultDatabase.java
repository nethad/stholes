import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultDatabase implements Database {

	private Connection connection;
	private PreparedStatement selectStatement;

	private static String USERNAME = "SA";
	private static String PASSWORD = "";

	private static String DB_FILE = "testdb";
	private static String TABLE_NAME = "test";

	public DefaultDatabase() {
		initialize();
	}

	private void initialize() {
		try {
			connection = DriverManager.getConnection("jdbc:hsqldb:file:"
					+ DB_FILE + ";ifexists=true", USERNAME, PASSWORD);
			selectStatement = connection
					.prepareStatement("select count(*) from " + TABLE_NAME
							+ " where x between ? and ? and y between ? and ?");
		} catch (SQLException e) {
			System.err.println("SQLException: " + e.getMessage());
		}
	}

	@Override
	public int executeCountQuery(Query query) {
		try {
			selectStatement.setDouble(1, query.getXMin());
			selectStatement.setDouble(2, query.getXMax());
			selectStatement.setDouble(3, query.getYMin());
			selectStatement.setDouble(4, query.getYMax());
			ResultSet resultSet = selectStatement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("SQLException: " + e.getMessage());
		}
		return 0;
	}
}
