package Utilities;

import java.util.List;

public class DecouplingFromPackageConstraint {
	private String targetPackage;
	private List<String> allowedPackages;
	private String bugMessage;
	
	public DecouplingFromPackageConstraint(String targetPackage, List<String> allowedPackages, String bugMessage){
		setTargetPackage(targetPackage);
		setAllowedPackage(allowedPackages);
		setBugMessage(bugMessage);
	}

	/**
	 * @param targetPackage the targetPackage to set
	 */
	public void setTargetPackage(String targetPackage) {
		this.targetPackage = targetPackage;
	}

	/**
	 * @return the targetPackage
	 */
	public String getTargetPackage() {
		return targetPackage;
	}

	/**
	 * @param allowedPackage the allowedPackage to set
	 */
	public void setAllowedPackage(List<String> allowedPackages) {
		this.allowedPackages = allowedPackages;
	}

	/**
	 * @return the allowedPackage
	 */
	public List<String> getAllowedPackage() {
		return allowedPackages;
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
		return this.bugMessage;
	}
}
