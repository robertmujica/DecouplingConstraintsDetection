package Detector;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ObjectType;

import Utilities.DecouplingMutatorFromClassConstraint;
import Utilities.DecouplingMutatorFromClassConstraintXmlReader;
import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ObjectTypeFactory;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

/**
 * Class to detect coupling to a Class mutator methods from
 * a list of Classes. (Context 2 documented on Section 7 on Report)
 * */
public class DecouplingMutatorMethodsFromClassesDetector extends OpcodeStackDetector {
	/** External Debug flag set? */     
	private static final boolean DEBUG = Boolean.getBoolean("debug.staticcal");      
	
	/** The reporter to report to */
    final private BugReporter reporter;
    final private BugAccumulator bugAccumulator;
    
    /** Class being inspected */  
    private JavaClass currentClass; 
    
    private FileWriter outFile;
    
    private static final String BUG_TYPE = "DC_DECOUPLING_MUTATORS_FROM_CLASSES";
	
	/* Decoupling Constraint list */
	private List<DecouplingMutatorFromClassConstraint> dcList;
	
	/**
     * Creates a new instance of this Detector.
     * 
     * @param aReporter
     *            {@link BugReporter} instance to report found problems to.
     */
    public DecouplingMutatorMethodsFromClassesDetector(BugReporter aReporter) {
            reporter = aReporter;
            bugAccumulator = new BugAccumulator(reporter);
            outFile = null;
    }
    
    @Override
    public void visit(Code obj) {
            super.visit(obj);
            bugAccumulator.reportAccumulatedBugs();
    }
    
    /** Remembers the class name and resets temporary fields.   */  
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
	
    /** This evaluate each bytecode instruction contained in a method  */
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
		
		/*try {
		outFile = new FileWriter("c:\\Temp\\textBob.txt", true);
	} catch (IOException e) {
		e.printStackTrace();
	}
	PrintWriter out = new PrintWriter(outFile);
	out.println("currentClass.getClassName() :" + currentClass.getClassName());*/
		
		DecouplingMutatorFromClassConstraintXmlReader constraintReader = new DecouplingMutatorFromClassConstraintXmlReader();
		dcList = constraintReader.loadConstraints();
		
		for (Iterator<DecouplingMutatorFromClassConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
			DecouplingMutatorFromClassConstraint constraint = iterator.next();
			
			try{
				String tBugType = BUG_TYPE;
				String mutatorsClassName = constraint.getClassName();
				List<String> mutators = constraint.getMutatorNames();
				
				ObjectType targetType = ObjectTypeFactory.getInstance(mutatorsClassName);
				ObjectType tType = ObjectTypeFactory.getInstance(getClassConstantOperand()); 
				String invokedName = getNameConstantOperand();
				//out.println("mutatorsClassName :" + mutatorsClassName);
				//out.println("invokedName :" + invokedName);
				
				//Work out the current class 
				if(currentClass.getClassName().equals(mutatorsClassName))
					tBugType = null;
				else if (!tType.subclassOf(targetType)){ //Work out current object's type is of type mutator's class name
					tBugType = null;    
				}else if (mutators.indexOf(invokedName) == -1) {
					tBugType = null;
				}else if((constraint.getForbiddenClassList().indexOf(currentClass.getClassName()) == -1))
					tBugType = null;
				
				if(tBugType != null){
					bugAccumulator.accumulateBug(new BugInstance(tBugType,
						HIGH_PRIORITY)
						.addClassAndMethod(this)
						.addCalledMethod(this)
						.addString(constraint.getBugMessage()), this);
				}
			} catch (ClassNotFoundException e) {    
	        	AnalysisContext.reportMissingClass(e);   
	        } 
		}
		//out.close();
	}
}
