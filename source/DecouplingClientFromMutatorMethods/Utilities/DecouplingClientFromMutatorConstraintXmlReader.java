
package Utilities;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.bcel.generic.ObjectType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import edu.umd.cs.findbugs.ba.ObjectTypeFactory;

public class DecouplingClientFromMutatorConstraintXmlReader {
	
	private final String FILE_NAME = "DecouplingClientFromMutatorConstraints.xml";
	
	/* 
	 * loads Constraints from a xml file 
	 * */
	public List<DecouplingClientFromMutatorConstraint> loadConstraints(){
		List<DecouplingClientFromMutatorConstraint> list = new ArrayList<DecouplingClientFromMutatorConstraint>();
		
		List<String> mutators = new ArrayList<String>();
		List<String> allowedClasses = new ArrayList<String>();
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
			    	String className = fstElmnt.getAttribute("className");
			    	String message = fstElmnt.getAttribute("message");
			    	String mutatorsAttribute = fstElmnt.getAttribute("mutatorsList");
			    	String[] mutatorsList = mutatorsAttribute.split(";");
			    	String allowedClassListAttribute = fstElmnt.getAttribute("allowedClassList");
			    	String[] allowedClassList = allowedClassListAttribute.split(";");
			    	
			    	for(int i = 0; i < mutatorsList.length; i++){
			    		String mutator = mutatorsList[i];
			    		mutators.add(mutator);
			    	}
			    	
			    	for(int i = 0; i < allowedClassList.length; i++){
			    		String allowedClassName = allowedClassList[i];
			    		allowedClasses.add(allowedClassName);
			    	}
			    	DecouplingClientFromMutatorConstraint constraint = new DecouplingClientFromMutatorConstraint(className, mutators, allowedClasses, message);
			    	list.add(constraint);
			    }

			  }
			  } catch (Exception e) {
			    e.printStackTrace();
			  }
		
		return list;
	}
	
	public String getPath(){
		URL jarPath = getClass().getProtectionDomain().getCodeSource().getLocation();
		return jarPath.getPath();
	}
	
/**
 * @param args
 */
public static void main(String[] args) {
	ObjectType fieldObj = ObjectTypeFactory.getInstance("DecouplingFromPackage.data.repository.CustomerRepository");
	String packageName = "";
	String className = fieldObj.getClassName();
	int index = className.lastIndexOf(".");
	if(index > 0)
	{
		packageName = className.substring(0, index);
	}
	
	
	
	DecouplingClientFromMutatorConstraintXmlReader t = new DecouplingClientFromMutatorConstraintXmlReader();
	String path = t.getPath();
	String[] fileParts = path.split("/");
	String pathWithoutFileName = "";
	
	for(int i = 0; i < fileParts.length - 1; i++){
		pathWithoutFileName += fileParts[i] + "/";
	}
	
	
	FileWriter outFile = null;
	try {
		outFile = new FileWriter("c:\\Temp\\textBob.txt");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	PrintWriter out = new PrintWriter(outFile);
	out.println("Path :" + path);
	out.println("Path :" + pathWithoutFileName);
	out.close();
}
}