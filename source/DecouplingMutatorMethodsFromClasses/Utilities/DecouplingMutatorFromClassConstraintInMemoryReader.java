package Utilities;

import java.util.ArrayList;
import java.util.List;

public class DecouplingMutatorFromClassConstraintInMemoryReader {
	public static List<DecouplingMutatorFromClassConstraint> loadConstraints(){
		List<DecouplingMutatorFromClassConstraint> list = new ArrayList<DecouplingMutatorFromClassConstraint>();
		
		List<String> mutators = new ArrayList<String>();
		mutators.add("m1");
		List<String> forbiddenClasses = new ArrayList<String>();
		forbiddenClasses.add("Test1");
		DecouplingMutatorFromClassConstraint constraint1 = new DecouplingMutatorFromClassConstraint("DecouplingClientFromMutatorSampleCode.EPerson", 
				mutators,forbiddenClasses, "This Class is not allowed to access this Mutator");
		list.add(constraint1);
		
		List<String> mutators2 = new ArrayList<String>();
		mutators2.add("m2");
		List<String> forbiddenClasses2 = new ArrayList<String>();
		forbiddenClasses2.add("Test2");
		DecouplingMutatorFromClassConstraint constraint2 = new DecouplingMutatorFromClassConstraint("DecouplingClientFromMutatorSampleCode.EPerson", 
				mutators2,forbiddenClasses2,"This Class is not allowed to access this Mutator");
		list.add(constraint2);
		
		return list;
	}
}
