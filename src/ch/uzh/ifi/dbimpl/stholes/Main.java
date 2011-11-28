package ch.uzh.ifi.dbimpl.stholes;

import ch.uzh.ifi.dbimpl.stholes.data.Query;

public class Main {

	public static void main(String[] args) {
		QueryGenerator queryGenerator = new RandomQueryGenerator();
		Database database = new DefaultDatabase("db/random");
		STHolesAlgorithm stHolesAlgorithm = new STHolesAlgorithm(100, database);
		long error = 0;

		// Optional: Run an initial query against the complete range. (Simulate the fact that we already know the complete count)
		Query start = new Query(0.0, 1.0, 0.0, 1.0);
		stHolesAlgorithm.updateHistogram(start, 9872);
		System.out.println("Starting with total estimate = " + stHolesAlgorithm.getRootBucket().getTotalEstimate());

		for (int i = 0; i < 100 && queryGenerator.hasNextQuery(); i++) {
			System.out.println("\nNext Query\n");
			Query query = queryGenerator.nextQuery();
			int actualResultCount = database.executeCountQuery(query);
			double estimatedCount = stHolesAlgorithm.getEstimateForQuery(query);

			error += Math.pow(Math.round(estimatedCount - actualResultCount), 2);

			System.out.println(query.toString() + "; estimated count: " + estimatedCount + ", actual count: " + actualResultCount);
			stHolesAlgorithm.updateHistogram(query, actualResultCount);
			System.out.print("Square Error = " + (error) / (i + 1));
			System.out.print("\t\t| HistSize = " + stHolesAlgorithm.getRootBucket().getHistogramSize());
			System.out.print("\t\t| total estimate = " + stHolesAlgorithm.getRootBucket().getTotalEstimate() + "\n");
		}
	}
}
