package gr.uom.java.metric.probability.xml;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.Hashtable;
import java.util.Set;
import java.util.Iterator;

public class SystemAxisObject {
	
	private String name;
	private List classList;
	private Hashtable classTable;
	private Hashtable internalProbabilityClassTable;
	//TEST
	private Set<PackageAxisObject> packageSet;
	private Hashtable packageTable;
	private Hashtable internalProbabilityPackageTable;
	
	public SystemAxisObject() {
		classList = new ArrayList();
		classTable = new Hashtable();
		internalProbabilityClassTable = new Hashtable();
		packageSet = new HashSet();
		packageTable = new Hashtable();
		internalProbabilityPackageTable = new Hashtable();
	}
	
	public void setInternalProbabilityOfClass(String className, Double prob) {
		internalProbabilityClassTable.put(className,prob);
	}
	
	public Double getInternalProbabilityOfClass(String className) {
		return (Double)internalProbabilityClassTable.get(className);
	}
	
	public void setInternalProbabilityOfPackage(String packageName, Double prob) {
		internalProbabilityPackageTable.put(packageName,prob);
	}
	
	public Double getInternalProbabilityOfPackage(String packageName) {
		return (Double)internalProbabilityPackageTable.get(packageName);
	}
	
	public void addClass(ClassAxisObject c) throws IOException {
		double prob = 0.2;
		int i = 0;
	/*	
		try (BufferedReader br = new BufferedReader(new FileReader(".//internal_probabilities.csv"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
			       String temp[] = line.split(";");
			       temp[5] = temp[5].replace(',', '.');
			       double changes = Double.parseDouble(temp[5]);
			       prob = prob + changes;
			       i++;
		    }
		}
		
		prob = prob / i;*/
		
		classList.add(c);
		classTable.put(c.getName(),c);
		
//		try (BufferedReader br = new BufferedReader(new FileReader(".//internal_probabilities.csv"))) {
//		    String line;
//		    while ((line = br.readLine()) != null) {
//			       String temp[] = line.split(";");
//			       String cname = temp[0];
//			       temp[5] = temp[5].replace(',', '.');
//			       double changes = Double.parseDouble(temp[5]);
			       
//			       String temp_name = c.getName();
//			       String temp2[] = temp_name.split("\\$");
//			       temp_name = temp2[0]; 
//			       temp_name = temp_name + ".java";
			       
//			       if (cname.replaceAll("/", ".").endsWith(temp_name)) {
//			    	   prob = changes;
//			    	   break;
//			       }
//		    }
//		}
			
		internalProbabilityClassTable.put(c.getName(), prob);
	}
	
	public Set<PackageAxisObject> getPackageSet()
	{
		return packageSet;
	}
	
	public int getPackageNumber() {
		return packageSet.size();
	}

	public void addPackage(PackageAxisObject pao)  throws IOException {

		double prob = 0;
		int i = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(".//internal_probabilities_pack.csv"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
			       String temp[] = line.split(";");
			       temp[5] = temp[5].replace(',', '.');
			       double changes = Double.parseDouble(temp[5]);
			       prob = prob + changes;
			       i++;
		    }
		}
		
		prob = prob / i;
		
		
		if(pao != null)
		{
			pao.organzeAxes();
			packageSet.add(pao);
			packageTable.put(pao.getName(), pao);
			try (BufferedReader br = new BufferedReader(new FileReader(".//internal_probabilities_pack.csv"))) {
			    String line;
			    while ((line = br.readLine()) != null) {
				       String temp[] = line.split(";");
				       String pname = temp[0];
				       temp[5] = temp[5].replace(',', '.');
				       double changes = Double.parseDouble(temp[5]);

				       if (pao.getName().replaceAll("/", ".").endsWith(pname)) {
				    	   prob = changes;
				    	   break;
				       }				       
			    }
			    internalProbabilityPackageTable.put(pao.getName(), prob);
			}
		}
	}
	
	public ListIterator getClassListIterator() {
		return classList.listIterator();
	}
	
	public Iterator<PackageAxisObject> getPackageSetIterator() {
		return packageSet.iterator();
	}
	
	public int getClassNumber() {
		return classList.size();
	}
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public ClassAxisObject getClass(String className) {
		return (ClassAxisObject)classTable.get(className);
	}
	public PackageAxisObject getPackage(String packageName) {
		return (PackageAxisObject)classTable.get(packageName);
	}
	
	public double getProbabilityOfClass(String className) {
		return ((ClassAxisObject)classTable.get(className)).getProbability();
	}
	
	public double getProbabilityOfPackage(String packageName) {
		return ((PackageAxisObject)packageTable.get(packageName)).getProbability();
	}
	
	public void printProbabilities() {
		System.out.println(name);
		ListIterator it = classList.listIterator();
		
		while(it.hasNext()) {
			ClassAxisObject c = (ClassAxisObject)it.next();
			System.out.println("P(" + c.getName() + ") = " + 
			c.getProbability());
		}
	}
	
	/*-----------------------------------------------
	 * ----------------------------------------------
	 * Task1, Task2
	 * ----------------------------------------------
	 * ----------------------------------------------
	 */
	
	/* Filtrarisma onomatos */
	private String filterAxisObjectName(String cname)
	{
		String parts[] = cname.split("\\.");
		String filteredName = "";
		for(int i=0; i<parts.length - 1; i++)
		{
			if(i != parts.length - 2)
				filteredName += parts[i] + ".";
			else
				filteredName += parts[i];
		}
		
		return filteredName;
	}
}