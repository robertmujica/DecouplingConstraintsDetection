package Utilities;

public class DecouplingClassesConstraint {
	private String targetClassName;
	private String forbiddenClass;
	private String bugMessage;
	
	public DecouplingClassesConstraint(String targetClassName, String forbiddenClass, String bugMessage){
		setTargetClassName(targetClassName);
		setForbiddenClass(forbiddenClass);
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
	public void setForbiddenClass(String forbiddenClass) {
		this.forbiddenClass = forbiddenClass;
	}

	/**
	 * @return the forbiddenClass
	 */
	public String getForbiddenClass() {
		return forbiddenClass;
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
