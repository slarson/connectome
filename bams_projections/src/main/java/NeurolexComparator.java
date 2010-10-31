
import java.util.Comparator;


public class NeurolexComparator implements Comparator{

	public int compare(Object o1, Object o2) {
		
		NeurolexPageId component1 = (NeurolexPageId)o1;
		NeurolexPageId component2 = (NeurolexPageId)o2;
		
		return component1.getName().compareTo(component2.getName());
		
	}

}
