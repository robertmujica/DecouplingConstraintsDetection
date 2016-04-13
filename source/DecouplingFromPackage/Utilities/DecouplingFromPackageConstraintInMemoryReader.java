package Utilities;

import java.util.ArrayList;
import java.util.List;

public class DecouplingFromPackageConstraintInMemoryReader {
	public static List<DecouplingFromPackageConstraint> loadConstraints(){
		List<DecouplingFromPackageConstraint> list = new ArrayList<DecouplingFromPackageConstraint>();
		
		List<String> packageList = new ArrayList<String>();
		packageList.add("DecouplingFromPackage.businessLogic");
		DecouplingFromPackageConstraint constraint1 = new DecouplingFromPackageConstraint("DecouplingFromPackage.data.repository", 
				packageList, "");
		list.add(constraint1);
		
		return list;
	}
}
