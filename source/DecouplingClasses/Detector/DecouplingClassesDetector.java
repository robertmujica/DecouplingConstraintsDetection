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

import Utilities.DecouplingClassesConstraint;
import Utilities.DecouplingClassesConstraintXmlReader;
import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance; 
import edu.umd.cs.findbugs.BugReporter; 
import edu.umd.cs.findbugs.ba.AnalysisContext;
import edu.umd.cs.findbugs.ba.ObjectTypeFactory; 
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

/**
 * Class to detect coupling between Classes
 *  (Section 6 on Report)
 * */
public class DecouplingClassesDetector extends OpcodeStackDetector  {
	/** External Debug flag set? */     
	private static final boolean DEBUG = Boolean.getBoolean("debug.staticcal");      
	
	/** The reporter to report to */
    private BugReporter reporter;
    private BugAccumulator bugAccumulator;
    
    /** Class being inspected */  
    private JavaClass currentClass; 
    
    private static final String BUG_TYPE = "DC_DECOUPLING_CLASSES";
    
    private FileWriter outFile;
	
	/**
     * Creates a new instance of this Detector.
     * 
     * @param aReporter
     *            {@link BugReporter} instance to report found problems to.
     */
    public DecouplingClassesDetector(BugReporter aReporter) {
            reporter = aReporter;
            bugAccumulator = new BugAccumulator(reporter);
    }
    
    /** this function is important to Detect multiple DC issues within the same Method*/
    @Override
    public void visit(Code obj) {
            super.visit(obj);
            bugAccumulator.reportAccumulatedBugs();
            this.report();
    }
    
    /** this function looks for Method parameter list violations */
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
		
			DecouplingClassesConstraintXmlReader reader = new DecouplingClassesConstraintXmlReader();
			List<DecouplingClassesConstraint> dcList = reader.loadConstraints();
			
			for (Iterator<DecouplingClassesConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
				DecouplingClassesConstraint constraint = iterator.next();
				
				String tBugType = BUG_TYPE;
								
				ObjectType forbiddenClassType = ObjectTypeFactory.getInstance(constraint.getForbiddenClass());
				
				if(!currentClass.getClassName().equals(constraint.getTargetClassName()))
					tBugType = null;
				
				// if it is not compatible with targetClassType, we are not    
				// interested anymore    
				try {
					if (!tType.getClassName().equals(constraint.getForbiddenClass())
								&& !tType.subclassOf(forbiddenClassType))
							tBugType = null;
				} catch (ClassNotFoundException e) {
					//out.println("ClassNotFoundException :" + e.getMessage());
					e.printStackTrace();
				} 
				
    			if(tBugType != null){
    				bugAccumulator.accumulateBug(new BugInstance(tBugType,HIGH_PRIORITY)
					.addClassAndMethod(this)
	        		.addSourceLine(this)
					.addString(constraint.getBugMessage()), this);
				} 
			}
		
    		}
    		catch(Exception ex)
    		{}
    	}
    	//out.close();
    }
    
    /** Remembers the class name and resets temporary fields.   */  
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
    			
    			/*if (tType.subclassOf(targetType)
    					&& currentClass.getClassName().equals(sourceType.getClassName())) {      
    				tBugType = BUG_TYPE; 
    			}*/
    			
    			DecouplingClassesConstraintXmlReader reader = new DecouplingClassesConstraintXmlReader();
    			List<DecouplingClassesConstraint> dcList = reader.loadConstraints();
    			
    			for (Iterator<DecouplingClassesConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
    				DecouplingClassesConstraint constraint = iterator.next();
    				
    				try{
    				String tBugType = BUG_TYPE;
    				
    				ObjectType forbiddenClassType = ObjectTypeFactory.getInstance(constraint.getForbiddenClass());
    				
    				if(!currentClass.getClassName().equals(constraint.getTargetClassName()))
    					tBugType = null;
    				
    				// if it is not compatible with targetClassType, we are not    
    				// interested anymore    
    				if (!tType.getClassName().equals(constraint.getForbiddenClass())
    						&& !tType.subclassOf(forbiddenClassType))
    					tBugType = null; 
    				
	    			if(tBugType != null){
	    				reporter.reportBug(new BugInstance(this, tBugType, HIGH_PRIORITY)
	    					.addClass(currentClass)
	    					.addField(currentClass.getClassName(), aField.getName(), aField.getSignature(), true)
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
    
    /** This evaluates each bytecode instruction contained in a method.
     * For this particular Decoupling Constraint,we are only interested in
     * get, set operations and also the usage or "new" operation and access 
     * to static properties. 
     *   */
	@Override     
	public void sawOpcode(int seen) {
		// we are only interested in static and virtual method calls
		if (seen != NEW && seen != GETSTATIC && seen != INVOKESTATIC 
				&& seen != INVOKEVIRTUAL && seen != PUTFIELD) {
            return;
		}
			
			//determine type of the object the method is invoked on    
			ObjectType tType = ObjectTypeFactory.getInstance(getClassConstantOperand()); 
			
			DecouplingClassesConstraintXmlReader reader = new DecouplingClassesConstraintXmlReader();
			List<DecouplingClassesConstraint> dcList = reader.loadConstraints();
			
			for (Iterator<DecouplingClassesConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
				DecouplingClassesConstraint constraint = iterator.next();
				
				try{
					String tBugType = BUG_TYPE;
					ObjectType forbiddenClassType = ObjectTypeFactory.getInstance(constraint.getForbiddenClass());
					
					if(!currentClass.getClassName().equals(constraint.getTargetClassName()))
						tBugType = null;
					
					// if it is not compatible with targetClassType, we are not    
					// interested anymore    
					if (!tType.getClassName().equals(constraint.getForbiddenClass())
							&& !tType.subclassOf(forbiddenClassType))
						tBugType = null; 
					
					if(tBugType != null && seen != PUTFIELD){
						reporter.reportBug(new BugInstance(this,tBugType, HIGH_PRIORITY)
			        		.addClassAndMethod(this)
			        		.addSourceLine(this)
							.addString(constraint.getBugMessage()));
					}
					else if(tBugType != null && seen == PUTFIELD){
						bugAccumulator.accumulateBug(new BugInstance(this, tBugType, HIGH_PRIORITY)
						.addClassAndMethod(this),this);
					}
				} catch (ClassNotFoundException e) {    
					AnalysisContext.reportMissingClass(e);   
	        	}
			}
	}
}
