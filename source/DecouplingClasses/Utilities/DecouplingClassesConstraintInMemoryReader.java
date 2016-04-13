package Utilities;

import java.util.ArrayList;
import java.util.List;

public class DecouplingClassesConstraintInMemoryReader {
	public static List<DecouplingClassesConstraint> loadConstraints(){
		List<DecouplingClassesConstraint> list = new ArrayList<DecouplingClassesConstraint>();
		
		DecouplingClassesConstraint constraint1 = new DecouplingClassesConstraint("DecouplingClassesSampleCode.BitstreamStorageManager", 
				"DecouplingClassesSampleCode.BitstreamInfoDAO", "");
		list.add(constraint1);
		
		return list;
	}
}
