package ch.uzh.ifi.dbimpl.stholes.data;

public abstract class Merge {

	private double penalty;

	public Merge(double penalty) {
		this.penalty = penalty;
	}

	public double getPenalty() {
		return penalty;
	}

	public abstract void executeMerge();
}
