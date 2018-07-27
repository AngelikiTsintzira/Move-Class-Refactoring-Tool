package gr.uom.java.metric.probability.xml;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import gr.uom.java.metric.probability.browser.*;

public class ClassAxisObject {
	private String name;
	//Test packageName
	private String packageName;
	private List axisList; //internal
	private double probability;
	private double eprobability;
	private Set<PackageAxis> packageAxisSet;
	private List eaxisList; //external
	
	//TEST
	private Set<String> pAxisSet;
	
	public ClassAxisObject(String name) {
		this.name = name;
		if(filterName(name).isEmpty())
			this.packageName = "default";
		else
			this.packageName = filterName(name);
		
		axisList = new ArrayList();
		packageAxisSet = new HashSet();
		eaxisList = new ArrayList();
		//TEST
		pAxisSet = new HashSet();
	}
	
	
	public List getDependencies() {
		return eaxisList;
	}
	
	public void printDependancies() {
		System.out.println("Class " + name + " is connected to: ");
		for (int i=0;i<axisList.size();i++) {
			Axis temp = (Axis) axisList.get(i);
			System.out.println(temp.getDescription() +" !!!");
		}
	}
	
	public boolean addAxis(Axis axis, SystemObject so) throws IOException, Exception {
		axis = axis.updatePropagation(this.name, so);
		return axisList.add(axis);
	}
	//TASK 3 - External Axis
	public boolean addEAxis(Axis axis, SystemObject so)
	{
		//axis = axis.updatePropagation(this.name, so);
		pAxisSet.add(filterName(axis.getToClass()));
		return eaxisList.add(axis);
	}
	
	//TASK 2,3
	public boolean addPackageAxis(PackageAxis paxis, SystemObject so)
	{
		//paxis = paxis.updatePropagation(this.name, so);
		return packageAxisSet.add(paxis);
	}
	
	public void addPackageAxis(String paxis)
	{
		pAxisSet.add(paxis);
	}
	
	public ListIterator getAxisListIterator() {
		return axisList.listIterator();
	}
	
	public int getAxisNumber() {
		return axisList.size();
	}
	
	public String getName() {
		return name;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public void setProbability(double prob) {
		probability = prob;
	}
	
	public double getEProbability() {
		return eprobability;
	}

	public void setEProbability(double eprobability) {
		this.eprobability = eprobability;
	}
	
	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}
	
	public String getPackageName()
	{
		return packageName;
	}
	
	public Iterator<PackageAxis> getPackageAxisSetIterator()
	{
		return packageAxisSet.iterator();
	}
	
	private String filterName(String cname)
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


	public ListIterator<Axis> getEAxisListIterator() {
		return this.eaxisList.listIterator();
	}
}