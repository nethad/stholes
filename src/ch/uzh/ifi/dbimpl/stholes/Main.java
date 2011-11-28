package ch.uzh.ifi.dbimpl.stholes;

import ch.uzh.ifi.dbimpl.stholes.data.Query;
import ch.uzh.ifi.dbimpl.stholes.ui.VisualizeSTHoles;

public class Main {

	private static final int VISUALIZATION_INTERVAL = 10;
	private static final int NUMBER_OF_QUERIES = 100;

	public static void main(String[] args) {
		QueryGenerator queryGenerator = new RandomQueryGenerator();

		// ((RandomQueryGenerator) queryGenerator).setSelectivity(0.25);

		Database database = new DefaultDatabase("db/random");
		STHolesAlgorithm stHolesAlgorithm = new STHolesAlgorithm(100, database);

		VisualizeSTHoles visualizeSTHoles = new VisualizeSTHoles();
		visualizeSTHoles.setDataPoints(((DefaultDatabase) database).getAllDataPoints());

		long error = 0;

		// Optional: Run an initial query against the complete range. (Simulate
		// the fact that we already know the complete count)
		Query start = new Query(0.0, 1.0, 0.0, 1.0);
		stHolesAlgorithm.updateHistogram(start, 9872);
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
}
