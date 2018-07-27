package gr.uom.java.metric.probability.browser;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

public class SystemObject {
	
	private List<ClassObject> classList;
	private Set<PackageObject> packageSet;
	
	public SystemObject() {
		this.classList = new ArrayList();
		this.packageSet = new HashSet();
	}
	
	public boolean addClass(ClassObject c) {
		return classList.add(c);
	}
	
	public ListIterator getClassListIterator() {
		return classList.listIterator();
	}
	public Iterator getPackageSetIterator() {
		return packageSet.iterator();
	}
	
	public int getClassNumber() {
		return classList.size();
	}
	
	public int getPositionInClassList(String className) {
		ListIterator it = classList.listIterator();
		int counter = 0;
		
		while(it.hasNext()) {
			ClassObject co = (ClassObject)it.next();
			if(co.getName().equals(className))
				return counter;
			counter++;
		}
		return -1;
	}
	
	public HashSet getClassNameSet() {
		HashSet set = new HashSet();
		ListIterator it = classList.listIterator();
		while(it.hasNext()) {
			String co = ((ClassObject)it.next()).getName();
			set.add(co);
		}
		
		return set;
	}
	public HashSet getPackageNameSet() {
		HashSet set = new HashSet();
		Iterator pit = packageSet.iterator();
		while(pit.hasNext()) {
			String po = ((PackageObject)pit.next()).getName();
			set.add(po);
		}
		
		return set;
	}	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		ListIterator it = classList.listIterator();
		while(it.hasNext()) {
			sb.append(((ClassObject)it.next()).toString() + "\n");
		}
		
		return sb.toString();
	}
	
	public int getSize() {
		return classList.size();
	}
	
	public ClassObject getClass(int i) {
		return (ClassObject) classList.get(i);
	}

	//TASK 3
	public void addPackage(PackageObject po) {
		if(po != null)
			packageSet.add(po);		
	}

	public void organizeObjects() {
		Iterator<PackageObject> pit = packageSet.iterator();
		
		while(pit.hasNext())
		{
			Iterator<ClassObject> cit = classList.iterator();
			PackageObject po = pit.next();
			System.out.println("\n" + po.getName() + " POOO\n");
			System.out.println();
			while(cit.hasNext())
			{
				ClassObject co = cit.next();
				String filtered = filterObjectName(co.getName());
				/*System.out.println(filtered + " --- " + po.getName());
				System.out.print("");*/
				if(filtered.endsWith(po.getName()) && !filtered.isEmpty())
				{
					po.addClassObject(co);
					System.out.println(co.getName() + " COOO");
					System.out.print("");
				}
			}
		}
	}
	
	protected String filterObjectName(String cname)
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
		if(filteredName != null)
			return filteredName;
		else
			return "";
	}
}