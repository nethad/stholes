package ch.uzh.ifi.dbimpl.stholes;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class DefaultDatabase implements Database {

	private Connection connection;
	private PreparedStatement selectStatement;

	private static String USERNAME = "SA";
	private static String PASSWORD = "";

	private String dbFile = "testdb";
	private static String TABLE_NAME = "test";

	public DefaultDatabase() {
		initialize();
	}

	public DefaultDatabase(String dbFile) {
		if (dbFile != null) {
			this.dbFile = dbFile;
		}
		initialize();
	}

	private void initialize() {
		try {
			connection = DriverManager.getConnection("jdbc:hsqldb:file:"
					+ dbFile + ";ifexists=true", USERNAME, PASSWORD);
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

	public List<Point2D.Double> getAllDataPoints() {
		ArrayList<Double> list = new ArrayList<Point2D.Double>();
		try {
			PreparedStatement selectStatement = connection
					.prepareStatement("select x, y from " + TABLE_NAME);
			ResultSet resultSet = selectStatement.executeQuery();
			while (resultSet.next()) {
				list.add(new Point2D.Double(resultSet.getDouble(1), resultSet
						.getDouble(2)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
}
