package Utilities;

import java.util.List;

public class DecouplingClientFromMutatorConstraint {
	private String className;
	private List<String> mutatorNames;
	private String bugMessage;
	private List<String> allowedClasses;
	
	public DecouplingClientFromMutatorConstraint(String className, List<String> mutatorNames, 
			List<String> allowedClasses, String bugMessage){
		this.className = className;
		this.mutatorNames = mutatorNames;
		this.bugMessage = bugMessage;
		this.allowedClasses = allowedClasses;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @param mutatorNames the mutatorNames to set
	 */
	public void setMutatorNames(List<String> mutatorNames) {
		this.mutatorNames = mutatorNames;
	}

	/**
	 * @return the mutatorNames
	 */
	public List<String> getMutatorNames() {
		return mutatorNames;
	}
	
	public List<String> getAllowedClasses() {
		return this.allowedClasses;
	}

	/**
	 * @param bugMessage the bugMessage to set
	 */
	public void setBugMessage(String bugMessage) {
		this.bugMessage = bugMessage;
	}

	/**
	 * @return the bugMessage
	 */
	public String getBugMessage() {
		return bugMessage;
	}
}
