package Utilities;

import java.util.List;

public class DecouplingClassFromAnotherClassesConstraint {
	private String targetClassName;
	private List<String> allowedClasses;
	private String bugMessage;
	
	public DecouplingClassFromAnotherClassesConstraint(String targetClassName, List<String> allowedClasses, String bugMessage){
		setTargetClassName(targetClassName);
		setAllowedClasses(allowedClasses);
		setBugMessage(bugMessage);
	}

	/**
	 * @param targetClassName the targetClassName to set
	 */
	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	/**
	 * @return the targetClassName
	 */
	public String getTargetClassName() {
		return targetClassName;
	}

	/**
	 * @param forbiddenClass the forbiddenClass to set
	 */
	public void setAllowedClasses(List<String> allowedClasses) {
		this.allowedClasses = allowedClasses;
	}

	/**
	 * @return the forbiddenClass
	 */
	public List<String> getAllowedClasses() {
		return allowedClasses;
	}

	/**
	 * @param bugMessage the bugMessage to set
	 */
	private void setBugMessage(String bugMessage) {
		this.bugMessage = bugMessage;
	}

	/**
	 * @return the bugMessage
	 */
	public String getBugMessage() {
		return bugMessage;
	}

	
}
