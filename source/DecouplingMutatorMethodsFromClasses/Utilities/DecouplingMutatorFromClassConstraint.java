package Utilities;

import java.util.List;

public class DecouplingMutatorFromClassConstraint {
	private String className;
	private List<String> mutatorNames;
	private String bugMessage;
	private List<String> forbiddenClassList;
	
	public DecouplingMutatorFromClassConstraint(String className, List<String> mutatorNames, 
			List<String> forbiddenClassList, String bugMessage){
		this.className = className;
		this.mutatorNames = mutatorNames;
		this.bugMessage = bugMessage;
		this.forbiddenClassList = forbiddenClassList;
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
	
	public List<String> getForbiddenClassList() {
		return forbiddenClassList;
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
