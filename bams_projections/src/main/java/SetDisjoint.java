


public class SetDisjoint {

	public static void main(String[] args) throws Exception {
		
		long start = System.currentTimeMillis();
		//call the class to initialize the data structures.
		ExpandAndWriteIntersection.getInstance().setData(RunQuery.RunBAMSQuery(),RunQuery.RunBAMSProjectionQuery(),
				RunQuery.RunNeurolexQuery());
		
		long end = System.currentTimeMillis();
		long total = end - start;
		System.out.println("time to look for matches " + total + " ms");

	}

}
