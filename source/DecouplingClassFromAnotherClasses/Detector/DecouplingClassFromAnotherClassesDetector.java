package Detector;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Field; 
import org.apache.bcel.classfile.JavaClass; 
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ObjectType;  
import org.apache.bcel.generic.Type;

import Utilities.DecouplingClassFromAnotherClassesConstraint;
import Utilities.DecouplingClassFromAnotherConstraintXmlReader;
import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance; 
import edu.umd.cs.findbugs.BugReporter; 
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ObjectTypeFactory; 
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

/**
 * Class to detect coupling between Classes
 *  (Section 6 on Report)
 *  This Detector will allow access to a subset of Classes
 * */
public class DecouplingClassFromAnotherClassesDetector extends OpcodeStackDetector { // BytecodeScanningDetector  {
	/** External Debug flag set? */     
	private static final boolean DEBUG = Boolean.getBoolean("debug.staticcal");      
	
	/** The reporter to report to */
    private BugReporter reporter;
    private BugAccumulator bugAccumulator;
    
    /** Class being inspected */  
    private JavaClass currentClass; 
    
    private static final String BUG_TYPE = "DC_DECOUPLING_CLASS_FROM_ANOTHER";
    
    private FileWriter outFile;
		
	/**
     * Creates a new instance of this Detector. 
     * @param aReporter
     *            {@link BugReporter} instance to report found problems to.
     */
    public DecouplingClassFromAnotherClassesDetector(BugReporter aReporter) {
            reporter = aReporter;
            bugAccumulator = new BugAccumulator(reporter);
    }
    
    /** this function is important to Detect multiple DC issues within the same Method */
    @Override
    public void visit(Code obj) {
            super.visit(obj);
            bugAccumulator.reportAccumulatedBugs();
            this.report();
    }
    
    /**   Remembers the class name and resets temporary fields.   */  
    @Override  
    public void visit(JavaClass someObj) {   
    	currentClass = someObj;
    	super.visit(someObj);  
    }
    
    /** this method looks for violations at class Fields declaration level */
    @Override  
    public void visit(Field aField) {   
    	super.visit(aField);   
    	if (aField.getType() instanceof ObjectType) {       
    		ObjectType tType = (ObjectType) aField.getType();    
    		
    		/*
    		try {
				outFile = new FileWriter("c:\\Temp\\textBob.txt", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			PrintWriter out = new PrintWriter(outFile);*/
    			
    			DecouplingClassFromAnotherConstraintXmlReader reader = new DecouplingClassFromAnotherConstraintXmlReader();
    			List<DecouplingClassFromAnotherClassesConstraint> dcList = reader.loadConstraints();
    			
    			for (Iterator<DecouplingClassFromAnotherClassesConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
    				DecouplingClassFromAnotherClassesConstraint constraint = iterator.next();
    				
    				try{
    				String tBugType = BUG_TYPE;
    				
    				ObjectType targetClassType = ObjectTypeFactory.getInstance(constraint.getTargetClassName());
    				
    				// if it is not compatible with targetClassType, we are not    
    				// interested anymore    
    				if (!tType.getClassName().equals(constraint.getTargetClassName())
    						&& !tType.subclassOf(targetClassType))
    					tBugType = null;
    				
    				if((constraint.getAllowedClasses().indexOf(currentClass.getClassName()) != -1))
    					tBugType = null;
    				
    				if(currentClass.getClassName().equals(constraint.getTargetClassName()))
    					tBugType = null;
    				
	    			if(tBugType != null){
	    				reporter.reportBug(new BugInstance(this, tBugType, HIGH_PRIORITY)
	    					.addClass(currentClass)
	    					.addField(currentClass.getClassName(), aField.getName(), aField.getSignature(), true)
	    					.addClass(currentClass)
	    					.addString(constraint.getBugMessage()));
					}
    				} catch (ClassNotFoundException e) {     
    					//out.println("Field Package :" + e.getMessage());
        				AnalysisContext.reportMissingClass(e);    
        			} 
    			}
    		
    		//out.close();
    	}  
    }
    
    /**   This function looks for methods parameter list violations */  
    @Override  
    public void visitMethod(Method obj) {     
    	super.visitMethod(obj);
    	Type[] args = obj.getArgumentTypes();
    	
    	/*try {
			outFile = new FileWriter("c:\\Temp\\textBob.txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter out = new PrintWriter(outFile);*/
   
    	for(int i = 0; i < args.length; i++)
    	{
    		try{
    		ObjectType tType = (ObjectType)args[i];
			
    		//out.println("Arg Type :" + tType.getClassName());
		
			DecouplingClassFromAnotherConstraintXmlReader reader = new DecouplingClassFromAnotherConstraintXmlReader();
			List<DecouplingClassFromAnotherClassesConstraint> dcList = reader.loadConstraints();
			
			for (Iterator<DecouplingClassFromAnotherClassesConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
				DecouplingClassFromAnotherClassesConstraint constraint = iterator.next();
				
				try{
					String tBugType = BUG_TYPE;
					ObjectType targetClassType = ObjectTypeFactory.getInstance(constraint.getTargetClassName());
					
					// if it is not compatible with targetClassType, we are not    
					// interested anymore    
					if (!tType.getClassName().equals(constraint.getTargetClassName())
							&& !tType.subclassOf(targetClassType))
						tBugType = null;
					
					if((constraint.getAllowedClasses().indexOf(currentClass.getClassName()) != -1))
						tBugType = null;
					
					if(currentClass.getClassName().equals(constraint.getTargetClassName()))
						tBugType = null;
					
					if(tBugType != null){
						reporter.reportBug(new BugInstance(this, tBugType, HIGH_PRIORITY)
			        		.addClassAndMethod(this)
			        		.addSourceLine(this)
			        		.addString(constraint.getBugMessage()));
					} 
				} catch (ClassNotFoundException e) {    
					AnalysisContext.reportMissingClass(e);   
	        	}
			}
    	}
    	catch (Exception e) {     
			//out.println("Field Package :" + e.getMessage());    
		} 
    	}
    	//out.close();
    }
    
    /** This evaluates each bytecode instruction contained in a method.
     * For this particular Decoupling Constraint,we are only interested in
     * get, set operations and also the usage or "new" operation and access 
     * to static properties. 
     *   */
	@Override     
	public void sawOpcode(int seen) {
		// we are only interested in static and virtual method calls
		if (seen != NEW && seen != GETSTATIC && seen != INVOKESTATIC && seen != INVOKESPECIAL
				&& seen != INVOKEVIRTUAL && seen != PUTFIELD && seen != GETFIELD) {
            return;
		}
			
			//determine type of the object the method is invoked on    
			ObjectType tType = ObjectTypeFactory.getInstance(getClassConstantOperand()); 
			
			DecouplingClassFromAnotherConstraintXmlReader reader = new DecouplingClassFromAnotherConstraintXmlReader();
			List<DecouplingClassFromAnotherClassesConstraint> dcList = reader.loadConstraints();
			
			for (Iterator<DecouplingClassFromAnotherClassesConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
				DecouplingClassFromAnotherClassesConstraint constraint = iterator.next();
				
				try{
					String tBugType = BUG_TYPE;
					ObjectType targetClassType = ObjectTypeFactory.getInstance(constraint.getTargetClassName());
    				
    				// if it is not compatible with targetClassType, we are not    
    				// interested anymore    
    				if (!tType.getClassName().equals(constraint.getTargetClassName())
    						&& !tType.subclassOf(targetClassType))
    					tBugType = null;
    				
    				if((constraint.getAllowedClasses().indexOf(currentClass.getClassName()) != -1))
    					tBugType = null;
    				
    				if(currentClass.getClassName().equals(constraint.getTargetClassName()))
    					tBugType = null;
					
					if(tBugType != null && seen != PUTFIELD && seen != INVOKESPECIAL){
						reporter.reportBug(new BugInstance(this, tBugType, HIGH_PRIORITY)
			        		.addClassAndMethod(this)
			        		.addSourceLine(this)
			        		.addString(tType.getClassName() + " - " + constraint.getBugMessage()));
					}
					else if(tBugType != null && seen == PUTFIELD){
						bugAccumulator.accumulateBug(new BugInstance(this, tBugType, HIGH_PRIORITY)
						.addClassAndMethod(this),this);
					}
					else if(tBugType != null && seen == INVOKESPECIAL){
						reporter.reportBug(new BugInstance(this, tBugType, HIGH_PRIORITY)
						.addClass(this)
						.addSourceLine(this)
		        		.addString(constraint.getBugMessage()));
					}
				} catch (ClassNotFoundException e) {    
					AnalysisContext.reportMissingClass(e);   
	        	}
			}
	}
}
