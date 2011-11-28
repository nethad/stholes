package ch.uzh.ifi.dbimpl.stholes.data;

public class ParentMerge extends Merge {
	Bucket bc;
	Bucket bp;

	public ParentMerge(Bucket bp, Bucket bc, double penalty) {
		super(penalty);
		this.bc = bc;
		this.bp = bp;
	}

	@Override
	public void executeMerge() {
		// Remove the child bc and add all children of bc to bp;
		bp.removeChildBucket(bc);
		for (Bucket child : bc.getChildren()) {
			bp.addChildBucket(child);
		}

		bp.setFrequency(bp.getFrequency() + bc.getFrequency());
	}
}
