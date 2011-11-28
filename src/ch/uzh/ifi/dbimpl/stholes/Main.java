package ch.uzh.ifi.dbimpl.stholes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class Main {

	public static void main(String[] args) {
		QueryGenerator queryGenerator = new RandomQueryGenerator();
		Database database = new DefaultDatabase("db/random");
		STHolesAlgorithm stHolesAlgorithm = new STHolesAlgorithm(1000, database);
		Query start = new Query(0.0, 1.0, 0.0, 1.0);
		stHolesAlgorithm.updateHistogram(start, 0);
		System.out.println("total est = "
				+ stHolesAlgorithm.getRootBucket().getTotalEstimate());
		long error = 0;

		for (int i = 0; i < 1000 && queryGenerator.hasNextQuery(); i++) {
			Query query = queryGenerator.nextQuery();
			int actualResultCount = database.executeCountQuery(query);
			double estimatedCount = stHolesAlgorithm.getEstimateForQuery(query);

			error += Math.abs(estimatedCount - actualResultCount);

			System.out.println(query.toString() + "; estimated count: "
					+ estimatedCount + ", actual count: " + actualResultCount);
			stHolesAlgorithm.updateHistogram(query, actualResultCount);
			System.out.println("HistSize = "
					+ stHolesAlgorithm.getRootBucket().getHistogramSize()
					+ " | Error = " + (error) / (i + 1));
			System.out.println("total estimate = "
					+ stHolesAlgorithm.getRootBucket().getTotalEstimate());
		}
	}

	private static void doDBStuff() {
		try {
			Connection connection = DriverManager.getConnection(
					"jdbc:hsqldb:file:testdb;ifexists=true", "SA", "");
			Statement statement = connection.createStatement();
			statement
					.execute("create table test (id integer primary key, x double, y double);");
			statement
					.executeUpdate("insert into test (id, x, y) values (1, 0.5, 0.5);");
			ResultSet result = statement
					.executeQuery("select count(*) from test;");
			if (result.next()) {
				System.out.println("count = " + result.getInt(1));
			} else {
				System.err.println("No result.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
