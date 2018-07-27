package gr.uom.java.metric.probability.xml;
import java.util.*;

import gr.uom.java.metric.probability.browser.SystemObject;
public class PackageAxisObject {
	private String name;
	private Set<ClassAxisObject> classset;
	private double probability;
	private Hashtable packageAxisSet;
	
	public PackageAxisObject(String name)
	{
		this.setName(name);
		classset = new HashSet<ClassAxisObject>();
		packageAxisSet = new Hashtable<String, PackageAxis>();
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	public Iterator getPackageAxisSetIterator() {
		return packageAxisSet.entrySet().iterator();
	}
	
	public Hashtable getPackageAxisSet() {
		return packageAxisSet;
	}
	
	public void addClassToPackage(ClassAxisObject ca) {
		classset.add(ca);
	}
	
	public Iterator<ClassAxisObject> getClassSetIterator() {
		return classset.iterator();
	}
	
	public boolean addPackageAxis(PackageAxis paxis, SystemObject so)
	{
		//TASK 3
		/*Iterator<ClassAxisObject> cit = classset.iterator();
		paxis = paxis.updatePropagation(this.name, so);*/
		
		//TODO - Mpainoun PackageAxis me from = ""!
		if(!aExists(paxis.getToPackage()))
			return packageAxisSet.put(paxis.getToPackage(), paxis) != null;
		else
			return false;
	}
	
	public int getNumberOfReferences()
	{
		return packageAxisSet.size();
	}
	
	public List getReferences()
	{
		ArrayList<PackageAxis> paxes = new ArrayList(packageAxisSet.values());
		return paxes;
	}

	private boolean aExists(String toPackage, ArrayList<PackageAxis> paxes) {
		Iterator it = paxes.iterator();
		while(it.hasNext())
		{
			PackageAxis pa = (PackageAxis)it.next();
			if(pa.getToPackage().equals(toPackage))
				return true;
		}
		return false;
	}
	
	private boolean aExists(String toPackage) {
		Set<PackageAxis> pSet = packageAxisSet.keySet();
		Iterator pit = pSet.iterator();
		while(pit.hasNext())
		{
			PackageAxis pa = (PackageAxis)packageAxisSet.get(pit.next());
			if(pa.getToPackage().equals(toPackage))
				return true;
		}
		return false;
	}
		
	public void updateProbability() {
		
		double totalProb = 0.0;
		double tempProb = 0.0;
		Set<PackageAxis> pSet = packageAxisSet.keySet();
		Iterator pit = pSet.iterator();
		while(pit.hasNext())
		{
			PackageAxis pas = (PackageAxis)packageAxisSet.get(pit.next());
			pas.updatePropagation();
			tempProb = pas.getProbability();
			totalProb = totalProb + tempProb - totalProb*tempProb;
		}
		
		System.out.println("\n" + this.name + " - " + totalProb);
		probability = totalProb;
	}

	public void organzeAxes() {
		Iterator cit = classset.iterator();
		System.out.println("---" + name + "\n");
		System.out.println();
		//Ta ClassAxisObject
		while(cit.hasNext())
		{
			ClassAxisObject c = (ClassAxisObject) cit.next();
			ListIterator al = c.getEAxisListIterator();
			while(al.hasNext())
			{
				Axis a = (Axis)al.next();
				PackageAxis pa = (PackageAxis) packageAxisSet.get(getClassPacketName(a.getToClass()));
				if(pa != null)
					pa.addAxis(a);
			}
		}
		Set<PackageAxis> pSet = packageAxisSet.keySet();
		Iterator pit = pSet.iterator();
		while(pit.hasNext())
		{
			PackageAxis pa = (PackageAxis)packageAxisSet.get(pit.next());
			//Gia na luso to provlima me to keno onoma tou PackageAxis
			if(this.getName() == "default" && pa.getToPackage() == "")
			{
				packageAxisSet.remove(pa);
				pa.setToPackage("default");
				continue;
			}
			pa.updatePropagation();
		}
		updateProbability();
	}
	
	private String getClassPacketName(String cname) {
		String parts[] = cname.split("\\.");
		String filteredName = "";
		for(int i=0; i<parts.length - 1; i++)
		{
			if(i != parts.length - 2)
				filteredName += parts[i] + ".";
			else
				filteredName += parts[i];
		}
		
		//System.out.println("--------------------> " + filteredName);
		return filteredName;
	}
}