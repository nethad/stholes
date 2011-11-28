package ch.uzh.ifi.dbimpl.stholes.data;

import java.util.LinkedList;
import java.util.List;

public class SiblingMerge extends Merge {
	Bucket bn;
	Bucket bp;
	Bucket b1;
	Bucket b2;

	int newParentFreqeuncy;

	public SiblingMerge(Bucket bp, Bucket bn, Bucket b1, Bucket b2, double penalty, int newParentFreqeuncy) {
		super(penalty);
		this.bn = bn;
		this.bp = bp;
		this.b1 = b1;
		this.b2 = b2;
		this.newParentFreqeuncy = newParentFreqeuncy;
	}

	@Override
	public void executeMerge() {
		List<Bucket> toMove = new LinkedList<Bucket>();

		bp.removeChildBucket(b1);
		bp.removeChildBucket(b2);

		// Move all children of b1 and b2 to bn
		for (Bucket child : b1.getChildren()) {
			toMove.add(child);
		}
		for (Bucket m : toMove) {
			b1.removeChildBucket(m);
			bn.addChildBucket(m);
		}
		toMove.clear();

		for (Bucket child : b2.getChildren()) {
			toMove.add(child);
		}
		for (Bucket m : toMove) {
			b2.removeChildBucket(m);
			bn.addChildBucket(m);
		}
		toMove.clear();

		// Move all contained children to the new bucket
		for (Bucket child : bp.getChildren()) {
			if (bn.getRectangle().contains(child.getRectangle())) {
				toMove.add(child);
			}
		}
		for (Bucket m : toMove) {
			bp.removeChildBucket(m);
			bn.addChildBucket(m);
		}
		toMove.clear();

		bp.addChildBucket(bn);
		// Update the parent frequency
		bp.setFrequency(newParentFreqeuncy);
	}
}