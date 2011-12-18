package ch.uzh.ifi.dbimpl.stholes;

import ch.uzh.ifi.dbimpl.stholes.data.Query;
import ch.uzh.ifi.dbimpl.stholes.ui.VisualizeSTHoles;

public class Main {

	private static final int VISUALIZATION_INTERVAL = 1;
	private static final int NUMBER_OF_QUERIES = 100;
	private static final int MAX_NUMBER_OF_BUCKETS = 10;
	private static final double QUERY_SELECTIVITY = 0.02; // 0 for random

	public static void main(String[] args) {
		QueryGenerator queryGenerator = getQueryGenerator();

		Database database = new DefaultDatabase("db/import");
		database.setTable("dataset_a"); // or _b, _c

		STHolesAlgorithm stHolesAlgorithm = new STHolesAlgorithm(MAX_NUMBER_OF_BUCKETS, database);

		VisualizeSTHoles visualizeSTHoles = new VisualizeSTHoles();
		visualizeSTHoles.setDataPoints(((DefaultDatabase) database).getAllDataPoints());

		long error = 0;

		// Optional: Run an initial query against the complete range. (Simulate
		// the fact that we already know the complete count)
		Query start = new Query(0.0, 1.0, 0.0, 1.0);
		stHolesAlgorithm.updateHistogram(start, 10000);
		System.out.println("Starting with total estimate = " + stHolesAlgorithm.getRootBucket().getTotalEstimate());

		for (int i = 0; i < NUMBER_OF_QUERIES && queryGenerator.hasNextQuery(); i++) {
			System.out.println("\nNext Query\n");

			visualizeSTHoles.setQueryNumber(i);

			Query query = queryGenerator.nextQuery();
			visualizeSTHoles.setCurrentQuery(query);

			System.out.println("Query selectivity: " + query.getSelectivity());

			int actualResultCount = database.executeCountQuery(query);
			double estimatedCount = stHolesAlgorithm.getEstimateForQuery(query);

			error += Math.pow(Math.round(estimatedCount - actualResultCount), 2);

			System.out.println(query.toString() + "; estimated count: " + estimatedCount + ", actual count: "
					+ actualResultCount);
			stHolesAlgorithm.updateHistogram(query, actualResultCount);

			if (i % VISUALIZATION_INTERVAL == 0) {
				visualizeSTHoles.setRootBucket(stHolesAlgorithm.getRootBucket());
			}

			System.out.print("Square Error = " + (error) / (i + 1));
			System.out.print("\t\t| HistSize = " + stHolesAlgorithm.getRootBucket().getHistogramSize());
			System.out.print("\t\t| total estimate = " + stHolesAlgorithm.getRootBucket().getTotalEstimate() + "\n");

		}
	}

	private static QueryGenerator getQueryGenerator() {
		if (QUERY_SELECTIVITY != 0D) {
			return new SelectivityQueryGenerator(QUERY_SELECTIVITY);
		} else {
			return new RandomQueryGenerator();
		}
	}
}
