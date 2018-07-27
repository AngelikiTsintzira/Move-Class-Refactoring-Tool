package gr.uom.java.metric.probability.browser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import gr.uom.java.metric.probability.xml.Axis;
import gr.uom.java.metric.probability.xml.ClassAxisObject;
import gr.uom.java.metric.probability.xml.PackageAxis;
import gr.uom.java.metric.probability.xml.PackageAxisObject;

public class FilteredPackageObject
{
	private String name;
	private List<FilteredClassObject> classoSet;

	public FilteredPackageObject()
	{
		classoSet = new ArrayList<FilteredClassObject>();
	}
	
	public PackageAxisObject getPackageAxisObject(SystemObject so, List<ClassAxisObject> caolist)
	{
		boolean packageIsEmpty = true;
		PackageAxisObject pao = new PackageAxisObject(name);
		ListIterator<ClassAxisObject> caoit = caolist.listIterator();
		//Aplws setarw swsta to onoma tou PackageAxisObject
		setPackageAxisName(pao, caoit);
		//int i=0;
		
		caoit = caolist.listIterator();
		
		while(caoit.hasNext())
		{
			ClassAxisObject cao = caoit.next();
			if(cao.getPackageName().equals("default") && cao.getPackageName().equals(pao.getName()))
			{
				packageIsEmpty = false;
				ListIterator<Axis> ait = cao.getAxisListIterator();
				while(ait.hasNext())
				{
					Axis a = ait.next();
					//Mega la9os pou vazw pantou tis klaseis tou default paketou
					pao.addClassToPackage(cao);
					if(!getClassPacketName(a.getToClass()).equals(cao.getPackageName()) && !getClassPacketName(a.getToClass()).equals("default"))
					{
						PackageAxis pa;
						if(getClassPacketName(a.getToClass()) == "")
							continue;
						else
						{
							pa = new PackageAxis(getClassPacketName(a.getFromClass()), getClassPacketName(a.getToClass()));
							System.out.print("");
						
							cao.addEAxis(a, so);
							pao.addPackageAxis(pa, so);
							System.out.println(a.getToClass() + " PAO Method!");
						}
					}
				}
			}
			else if(cao.getPackageName().equals(pao.getName()))
			{
				packageIsEmpty = false;
				pao.addClassToPackage(cao);
				System.out.println("\n---------------- " + cao.getName() + " ----------------");
				ListIterator<Axis> ait = cao.getAxisListIterator();
				while(ait.hasNext())
				{
					Axis a = ait.next();
					//pao.addClassToPackage(cao);
					if(!getClassPacketName(a.getToClass()).equals(cao.getPackageName()))
					{
						PackageAxis pa = new PackageAxis(getClassPacketName(a.getFromClass()), getClassPacketName(a.getToClass()));
						cao.addEAxis(a, so);
						pao.addPackageAxis(pa, so);
					}
				}
			}
		}
		if(packageIsEmpty)
			return null;
		else
			return pao;
	}
	
	private List getCAOofThisPackage(List<ClassAxisObject> caolist) {
		List cao = new ArrayList<ClassAxisObject>();
		ListIterator cit = caolist.listIterator();
		while(cit.hasNext())
		{
			ClassAxisObject c = (ClassAxisObject)cit.next();
			if(c.getPackageName().endsWith(name))
				cao.add(c);
		}
		return cao;
	}
	
	private PackageAxisObject setPackageAxisName(PackageAxisObject pao, ListIterator<ClassAxisObject> caoit) {
		String fcaoName = "";
		//Tsekarw an auto to FilteredPackageObject einai to default paketo
		if(name == "default")
		{
			pao.setName("default");
			return pao;
		}
		else
			while(caoit.hasNext())
			{
				fcaoName = getClassPacketName(caoit.next().getName());
				if(fcaoName.endsWith(name))
				{
					pao.setName(fcaoName);
					return pao;
				}
			}
		return pao;
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
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public ListIterator getClassOSetIterator()
	{
		return classoSet.listIterator();
	}
	public void addClass(FilteredClassObject fco)
	{
		classoSet.add(fco);
	}
}
