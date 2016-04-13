package Detector;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

import Utilities.DecouplingFromPackageConstraint;
import Utilities.DecouplingFromPackageXmlReader;
import edu.umd.cs.findbugs.BugAccumulator;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;
import edu.umd.cs.findbugs.ba.ObjectTypeFactory;

/**
 * Class to detect coupling between Packages
 *  (Section 5 on Report)
 * */
public class DecouplingFromPackageDetector extends BytecodeScanningDetector {
	/** External Debug flag set? */     
	private static final boolean DEBUG = Boolean.getBoolean("debug.staticcal");      
	
	/** The reporter to report to */
    private BugReporter reporter;
    private BugAccumulator bugAccumulator;
    
    /** Class being inspected */  
    private JavaClass currentClass; 
    private String currentClassPackageName;
    
    private FileWriter outFile;
    
    private static final String BUG_TYPE = "DC_DECOUPLING_PACKAGES";
	
	/**
     * Creates a new instance of this Detector.
     * 
     * @param aReporter
     *            {@link BugReporter} instance to report found problems to.
     */
    public DecouplingFromPackageDetector(BugReporter aReporter) {
            reporter = aReporter;
            outFile = null;
            bugAccumulator = new BugAccumulator(reporter);
    }
    
    /** this function is important to Detect multiple DC issues within the same Method */
    @Override
    public void visit(Code obj) {
            super.visit(obj);
            bugAccumulator.reportAccumulatedBugs();
            this.report();
    }
    
    /** Remembers the class name and resets temporary fields.   */  
    @Override  
    public void visit(JavaClass someObj) {   
    	currentClass = someObj;
    	/* --------------------------------------------------------------------------------
    	 * Possible Routine to implement "Import" statements check.  
    	 * String fileName = someObj.getSourceFileName(); //this.getSourceFile();
    	reporter.reportBug(new BugInstance(this, BUG_TYPE, HIGH_PRIORITY)
		.addClass(currentClass)
		.addString("line 2")
		.addString(fileName + " - " + someObj.getFileName())
		.addString("second line"));
		-----------------------------------------------------------------------------------
		*/
    	currentClassPackageName = GetPackageName(currentClass.getClassName());
    	super.visit(someObj);  
    }
    
    /**   This function looks for methods parameter list violations */  
    @Override
    public void visitMethod(Method obj) {     
    	super.visitMethod(obj);
    	Type[] args = obj.getArgumentTypes();
    	
    	try {
			outFile = new FileWriter("c:\\Temp\\textBob.txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PrintWriter out = new PrintWriter(outFile);
		out.println("Method :" + obj.getName());
   
    	for(int iType = 0; iType < args.length; iType++)
    	{
    		try{
	    		ObjectType tType = (ObjectType)args[iType];
	    		ObjectType fieldObj = ObjectTypeFactory.getInstance(tType.getClassName());
	    		String fieldPackageName = GetPackageName(fieldObj.getClassName());
				
	    		out.println("Package Detector - Arg Type :" + tType.getClassName());
			
	    		DecouplingFromPackageXmlReader reader = new DecouplingFromPackageXmlReader();
	    		List<DecouplingFromPackageConstraint> dcList = reader.loadConstraints();
	    		
	    		for (Iterator<DecouplingFromPackageConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
	    			DecouplingFromPackageConstraint constraint = iterator.next();
	    			
	    			String tBugType = null;  
	    			
					if (fieldPackageName.equals(constraint.getTargetPackage()) 
							&& (constraint.getAllowedPackage().indexOf(currentClassPackageName) == -1 
								&& !currentClassPackageName.equals(constraint.getTargetPackage()))) {      
						tBugType = BUG_TYPE; 
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
    	out.close();
    }  
    
    /** this method looks for violations at class Fields declaration level */
    @Override  
    public void visit(Field aField) {   
    	super.visit(aField);   
    	if (aField.getType() instanceof ObjectType) {
    		  
    		ObjectType tType = (ObjectType) aField.getType();   
    		/*try {
        		outFile = new FileWriter("c:\\Temp\\textBob.txt", true);
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
    		PrintWriter out = new PrintWriter(outFile);*/
    		ObjectType fieldObj = ObjectTypeFactory.getInstance(tType.getClassName());
    		String fieldPackageName = GetPackageName(fieldObj.getClassName());
    		//out.println("Field Package :" + fieldPackageName);
    		//out.println("Current Class :" + currentClassPackageName);
    		
    		DecouplingFromPackageXmlReader reader = new DecouplingFromPackageXmlReader();
    		List<DecouplingFromPackageConstraint> dcList = reader.loadConstraints();
    		
    		for (Iterator<DecouplingFromPackageConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
    			DecouplingFromPackageConstraint constraint = iterator.next();
    			
    			String tBugType = null;  
    			
				if (fieldPackageName.equals(constraint.getTargetPackage()) 
						&& (constraint.getAllowedPackage().indexOf(currentClassPackageName) == -1 
							&& !currentClassPackageName.equals(constraint.getTargetPackage()))){
									tBugType = BUG_TYPE; 
				}
				
				if(tBugType != null){
					reporter.reportBug(new BugInstance(this, tBugType, HIGH_PRIORITY)
						.addClass(currentClass)
						.addField(currentClass.getClassName(), aField.getName(), aField.getSignature(), true)
						.addString(constraint.getBugMessage()));
				}   
    		}
			//out.close();
    	}  
    }

	private String GetPackageName(String fieldClassName) {
		String packageName = "";
		int index = fieldClassName.lastIndexOf(".");
		if(index > 0)
		{
			packageName = fieldClassName.substring(0, index);
		}
		
		return packageName;
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
				&& seen != INVOKEVIRTUAL && seen != PUTFIELD && seen != GETFIELD) {
            return;
		}
        
        //determine type of the object the method is invoked on    
		ObjectType tType = ObjectTypeFactory.getInstance(getClassConstantOperand());
		String tTypePackageName = GetPackageName(tType.getClassName());
        
		DecouplingFromPackageXmlReader reader = new DecouplingFromPackageXmlReader();
		List<DecouplingFromPackageConstraint> dcList = reader.loadConstraints();
		
		for (Iterator<DecouplingFromPackageConstraint> iterator = dcList.iterator(); iterator.hasNext();) {
			DecouplingFromPackageConstraint constraint = iterator.next();
			
			String tBugType = null;
			
			if (tTypePackageName.equals(constraint.getTargetPackage())
					&& (constraint.getAllowedPackage().indexOf(currentClassPackageName) == -1
						&& !currentClassPackageName.equals(constraint.getTargetPackage()))) {      
						tBugType = BUG_TYPE;
			}
				
			if(tBugType != null && seen != PUTFIELD){
				reporter.reportBug(new BugInstance(tBugType, HIGH_PRIORITY)
		    		.addClassAndMethod(this)
		    		.addSourceLine(this)
		    		.addString(constraint.getBugMessage()));
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
		}
	}
}
