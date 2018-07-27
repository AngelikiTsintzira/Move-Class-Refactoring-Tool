package gr.uom.java.metric.probability.browser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class PackageObject {
	private String name;
	private List<ClassObject> classoList;
	
	PackageObject()
	{
		classoList = new ArrayList<ClassObject>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ListIterator<ClassObject> getClassOListIterator()
	{
		return classoList.listIterator();
	}
	
	public void addClassObject(ClassObject co)
	{
		if(co !=null)
			classoList.add(co);
	}
	
	public String toString(){
		String str;
		str = "Name: " + this.name;
		str += "\nClasses: \n\t";
		ListIterator<ClassObject> cit = this.getClassOListIterator();
		while(cit.hasNext()){
			ClassObject co = cit.next();
			str += co.getName() + "\n\t";
		}
		return str;
	}
}
