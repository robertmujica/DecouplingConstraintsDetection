package Utilities;

import java.util.ArrayList;
import java.util.List;

public class DecouplingClassFromAnotherClassesConstraintInMemoryReader {
	public static List<DecouplingClassFromAnotherClassesConstraint> loadConstraints(){
		List<DecouplingClassFromAnotherClassesConstraint> list = new ArrayList<DecouplingClassFromAnotherClassesConstraint>();
		
		List<String> allowedClassList = new ArrayList<String>();
		allowedClassList.add("DecouplingFromPackage.businessLogic");
		DecouplingClassFromAnotherClassesConstraint constraint1 = new DecouplingClassFromAnotherClassesConstraint("DecouplingClassesSampleCode.BitstreamStorageManager", 
				allowedClassList, "test message");
		list.add(constraint1);
		
		return list;
	}
}
