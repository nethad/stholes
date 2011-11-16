
public class HistorgramFactory {

	public static Query CreateTestQuery()
	{
		Query q = new Query(0.2, 0.8, 0.0, 1.0);
		return q;
	}
	
	public static Bucket CreateHistogram1() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		return root;
	}
	
	public static Bucket CreateHistogram2() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		Bucket b1 = new Bucket(0.1, 0.9, 0.3, 0.7);
		b1.setFrequency(100);
		root.addChildBucket(b1);		
		return root;
	}
	
	public static Bucket CreateHistogram3() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		Bucket b11 = new Bucket(0.1, 0.6, 0.3, 0.7);
		b11.setFrequency(100);
		root.addChildBucket(b11);
		Bucket b12 = new Bucket(0.6, 0.9, 0.3, 0.7);
		b12.setFrequency(500);
		root.addChildBucket(b12);
		return root;
	}
	
	public static Bucket CreateHistogram4() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		Bucket child = new Bucket(0.1, 0.9, 0.3, 0.7);
		child.setFrequency(10000);
		root.addChildBucket(child);
		Bucket b21 = new Bucket(0.2, 0.5, 0.4, 0.6);
		b21.setFrequency(600);
		child.addChildBucket(b21);		
		Bucket b22 = new Bucket(0.5, 0.8, 0.4, 0.6);
		b22.setFrequency(900);
		child.addChildBucket(b22);
		return root;
	}
	
	public static Bucket CreateHistogram5() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		Bucket b1 = new Bucket(0.5, 0.9, 0.4, 0.6);
		b1.setFrequency(400);
		root.addChildBucket(b1);
		return root;
	}
	
	public static Bucket CreateHistogram6() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		Bucket b11 = new Bucket(0.0, 0.1, 0.4, 0.6);
		b11.setFrequency(900);
		root.addChildBucket(b11);
		Bucket b12 = new Bucket(0.5, 0.9, 0.4, 0.6);
		b12.setFrequency(400);
		root.addChildBucket(b12);
		return root;
	}	
	
	public static Bucket CreateHistogram7() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		Bucket child = new Bucket(0.1, 0.9, 0.3, 0.7);
		child.setFrequency(100);
		root.addChildBucket(child);
		Bucket b21 = new Bucket(0.3, 0.5, 0.4, 0.6);
		b21.setFrequency(400);
		child.addChildBucket(b21);		
		Bucket b22 = new Bucket(0.8, 0.9, 0.4, 0.6);
		b22.setFrequency(5000);
		child.addChildBucket(b22);
		return root;
	}	
	
	public static Bucket CreateHistogram8() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		Bucket b1 = new Bucket(0.3, 0.7, 0.3, 0.7);
		b1.setFrequency(100);
		root.addChildBucket(b1);		
		return root;
	}
	
	public static Bucket CreateHistogram9() {
		Bucket root = new Bucket(0.0, 1.0, 0.0, 1.0);
		root.setFrequency(1000);
		Bucket b11 = new Bucket(0.0, 0.2, 0.3, 0.7);
		b11.setFrequency(100);
		root.addChildBucket(b11);		
		Bucket b12 = new Bucket(0.8, 1.0, 0.3, 0.7);
		b12.setFrequency(4000);
		root.addChildBucket(b12);	
		return root;
	}
}
