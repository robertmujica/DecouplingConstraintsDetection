package Utilities;

import java.util.ArrayList;
import java.util.List;

public class DecouplingClientFromMutatorConstraintInMemoryReader {
	public static List<DecouplingClientFromMutatorConstraint> loadConstraints(){
		List<DecouplingClientFromMutatorConstraint> list = new ArrayList<DecouplingClientFromMutatorConstraint>();
		
		List<String> mutators = new ArrayList<String>();
		mutators.add("m1");
		List<String> allowedClasses = new ArrayList<String>();
		allowedClasses.add("Test1");
		DecouplingClientFromMutatorConstraint constraint1 = new DecouplingClientFromMutatorConstraint("DecouplingClientFromMutatorSampleCode.EPerson", 
				mutators,allowedClasses, "Access to this mutator is not allowed");
		list.add(constraint1);
		
		List<String> mutators2 = new ArrayList<String>();
		mutators2.add("m2");
		List<String> allowedClasses2 = new ArrayList<String>();
		allowedClasses2.add("Test2");
		DecouplingClientFromMutatorConstraint constraint2 = new DecouplingClientFromMutatorConstraint("DecouplingClientFromMutatorSampleCode.EPerson", 
				mutators2,allowedClasses2,"Access to this mutator is not allowed");
		list.add(constraint2);
		
		return list;
	}
}
