package Utilities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DecouplingFromPackageXmlReader {
	
	private final String FILE_NAME = "DecouplingFromPackageConstraints.xml";
	
	/* 
	 * loads Constraints from a xml file 
	 * */
	public List<DecouplingFromPackageConstraint> loadConstraints(){
		List<DecouplingFromPackageConstraint> list = new ArrayList<DecouplingFromPackageConstraint>();
		
		URL constraintsFile = getClass().getProtectionDomain().getCodeSource().getLocation();
		String[] fileParts = constraintsFile.getPath().split("/");
		String pathWithoutFileName = "";
		
		for(int i = 0; i < fileParts.length - 1; i++){
			pathWithoutFileName += fileParts[i] + "/";
		}
		
		try {
			
			  File file = new File(pathWithoutFileName + FILE_NAME);
			  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			  DocumentBuilder db = dbf.newDocumentBuilder();
			  Document doc = db.parse(file);
			  doc.getDocumentElement().normalize();
			  
			  NodeList nodeLst = doc.getElementsByTagName("constraint");
			  
			  for (int s = 0; s < nodeLst.getLength(); s++) {

			    Node fstNode = nodeLst.item(s);
			    
			    if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
			    	
			    	Element fstElmnt = (Element) fstNode;
			    	String targetPackage = fstElmnt.getAttribute("targetPackage");
			    	String allowedPackageAttribute = fstElmnt.getAttribute("allowedPackageList");
			    	String[] allowedPackages = allowedPackageAttribute.split(";");
			    	List<String> allowedPackageList = new ArrayList<String>();
			    	String message = fstElmnt.getAttribute("message");
			    	
			    	for(int i = 0; i < allowedPackages.length; i++){
			    		String packageName = allowedPackages[i];
			    		allowedPackageList.add(packageName);
			    	}
			    	
			    	DecouplingFromPackageConstraint constraint = new DecouplingFromPackageConstraint(
			    			targetPackage, allowedPackageList, message);
			    	list.add(constraint);
			    }
			  }
			  } catch (Exception e) {
			    e.printStackTrace();
			  }
		
		return list;
	}
}
