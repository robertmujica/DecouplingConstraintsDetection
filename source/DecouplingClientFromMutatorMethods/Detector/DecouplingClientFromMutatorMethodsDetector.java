package Detector;

import java.util.Iterator;
import java.util.List;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ObjectType;

import Utilities.DecouplingClientFromMutatorConstraint;
import Utilities.DecouplingClientFromMutatorConstraintXmlReader;
import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ObjectTypeFactory;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

/*
 * Class to detect coupling to a Class mutator methods from
 * a list of Classes. (Context 1 documented on Section 7 on Report)
 * */
public class DecouplingClientFromMutatorMethodsDetector extends OpcodeStackDetector {
	/** External Debug flag set? */     
	private static final boolean DEBUG = Boolean.getBoolean("debug.staticcal");      
	
	/** The reporter to report to */
    final private BugReporter reporter;
    final private BugAccumulator bugAccumulator;
    
    /** Class being inspected */  
    private JavaClass currentClass; 
    
    private static final String BUG_TYPE = "DC_DECOUPLING_CLIENT_FROM_MUTATORS";
	
	/* Decoupling Constraint list */
	private List<DecouplingClientFromMutatorConstraint> dcList;
	
	/**
     * Creates a new instance of this Detector.
     * 
     * @param aReporter
     *            {@link BugReporter} instance to report found problems to.
     */
    public DecouplingClientFromMutatorMethodsDetector(BugReporter aReporter) {
            reporter = aReporter;
            bugAccumulator = new BugAccumulator(reporter);
    }
    
    @Override
    public void visit(Code obj) {
            super.visit(obj);
            bugAccumulator.reportAccumulatedBugs();
    }
    
    /**   * Remembers the class name and resets temporary fields.   */  
    @Override  
    public void visit(JavaClass someObj) {   
    	currentClass = someObj;   
    	super.visit(someObj);  
    }
    
    @Override  
    public void visit(Field aField) {   
    	super.visit(aField);
    } 
    
    @Override  
    public void visitMethod(Method obj) {     
    	super.visitMethod(obj);
    }
	
    /* This evaluate each bytecode instruction contained in a method  
     * For this particular Decoupling Constraint,we are only interested in
     * any static and object instances invocations.
     * */
	@Override     
	public void sawOpcode(int seen) {
		// we are only interested in static and virtual method calls
        if (seen != INVOKEVIRTUAL && seen != INVOKESTATIC) {
                return;
        }
        
        String className = getClassConstantOperand();
		if (className.startsWith("[")) {
		        // Ignore array classes
		        return;
		}
		
		DecouplingClientFromMutatorConstraintXmlReader constraintReader = new DecouplingClientFromMutatorConstraintXmlReader();
		dcList = constraintReader.loadConstraints();
		
		for (Iterator<DecouplingClientFromMutatorConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
			DecouplingClientFromMutatorConstraint constraint = iterator.next();
			
			try{
				String tBugType = BUG_TYPE;
				String mutatorsClassName = constraint.getClassName();
				List<String> mutators = constraint.getMutatorNames();
				
				ObjectType targetType = ObjectTypeFactory.getInstance(mutatorsClassName);
				ObjectType tType = ObjectTypeFactory.getInstance(getClassConstantOperand()); 
				String invokedName = getNameConstantOperand();
				
				//Work out the current class 
				if(currentClass.getClassName().equals(mutatorsClassName))
					tBugType = null;
				else if (!tType.subclassOf(targetType)){ //Work out current object's type is of type mutator's class name
					tBugType = null;    
				}else if (mutators.indexOf(invokedName) == -1) {
					tBugType = null;
				}else if((constraint.getAllowedClasses().indexOf(currentClass.getClassName()) != -1))
					tBugType = null;
				
				if(tBugType != null){
					reporter.reportBug(new BugInstance(this,tBugType, HIGH_PRIORITY)
	        		.addClassAndMethod(this)
	        		.addSourceLine(this)
					.addString(constraint.getBugMessage()));
				}
			} catch (ClassNotFoundException e) {    
	        	AnalysisContext.reportMissingClass(e);   
	        } 
		}
	}
}
