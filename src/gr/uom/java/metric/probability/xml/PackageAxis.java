package gr.uom.java.metric.probability.xml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import gr.uom.java.metric.probability.browser.ClassObject;
import gr.uom.java.metric.probability.browser.FieldObject;
import gr.uom.java.metric.probability.browser.MethodInvocationObject;
import gr.uom.java.metric.probability.browser.MethodObject;
import gr.uom.java.metric.probability.browser.SystemObject;

public class PackageAxis implements Cloneable
{
	//private String description;
	private String from;
	private String to;
	private Double probability;
	private List<Axis> axisList;
	private Set<String> methods;
	private int flag;
	
	
	public PackageAxis clone() {
        try {
            return (PackageAxis) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
	//TODO Na pros9esw tin from Package.
	public PackageAxis(String from, String to)
	{
		if(from.isEmpty())
			this.from = "default";
		else
			this.from = from;
		this.from = from;
		this.to = to;
		axisList = new ArrayList<Axis>();
		methods = new HashSet<String>();
	}
	
	/*public String getDescription() {
		return description;
	}*/

	public ListIterator getAxisListIterator()
	{
		return this.axisList.listIterator();
	}
	
	public List getAxisList()
	{
		return this.axisList;
	}
	
	public void addAxis(Axis axis)
	{
		axisList.add(axis);
	}
	
	public void DeleteAxis(ArrayList<Axis> axis)
	{
		this.axisList.removeAll(axis);
	}
	
	public String getToPackage() {
		return to;
	}
	
	public void setToPackage(String to)
	{
		this.to = to;
	}

	public String getFromPackage() {
		return this.from;
	}
	
	public Double getProbability() {
		return probability;
	}
	
	//TASK 3
	/*public void updatePropagation()
	{
		ListIterator ait = axisList.listIterator();
		Set<String> methods = new HashSet<String>();
		double num = 0.0;
		double denum = 0.0;
		
		//Vazw tis me9odous pou kalountai se mia lista
		while(ait.hasNext())
		{
			Axis a = (Axis)ait.next();
			ListIterator<String> mci = a.getMethodsCalledListIterator();
			while(mci.hasNext())
				methods.add(mci.next());
		}
		
		ait = axisList.listIterator();
		//TODO - Last Bug
		while(ait.hasNext())
		{
			Axis a = (Axis)ait.next();
			num += a.getRFC() + a.getNOP() + a.getNPRF_USED();
			denum += a.getNPM()+ a.getNPRF();
		}
		if(denum == 0)
			probability = 0.0;
		else
			probability = num/denum;
		System.out.print("\nTo: " + to + " --- " + probability);
		System.out.print("");
	}*/
	
	public double updatePropagation()
	{
		ListIterator ait = axisList.listIterator();
		double num = 0.0;
		double denum = 0.0;
		double tempNum = 0.0;
		
//		if (from.contains("controller") && to.contains("preferences")) {
//			System.out.println("FROM: " + from + "- TO: " + to);
//		}
		
		Hashtable<String, Double> sourceClasses = new Hashtable<String, Double>();
		while(ait.hasNext()){
			Axis a = (Axis)ait.next();
			ListIterator mit = a.getMethodsCalledListIterator();
			while(mit.hasNext())
				methods.add((String)mit.next());

			if (sourceClasses.containsKey(a.getToClass()) == false) {
				sourceClasses.put(a.getToClass(), a.getNPM() + a.getAttributes());
				num += a.getNOP() + a.getNPRF_USED();
			}
			
			System.out.println("\t\t" + a.getFromClass() + " --> " + a.getToClass() + " " + a.getNPM() + " " + a.getNOP() + " " + a.getNPRF_USED());
		}

		Set<String> scSet = sourceClasses.keySet();
		Iterator scit = scSet.iterator();

		num = num + methods.size();
		
		while(scit.hasNext()) {
			Double temp = sourceClasses.get(scit.next()); 
			denum += temp;
			System.out.println("denum: " + temp);
		}
		
		Iterator<String> s = methods.iterator();
		while(s.hasNext())
			System.out.println("\t\t" + s.next());
		System.out.println("num:" + num);
		System.out.println("");
		
//		if (num == 4 && denum == 1) {
//			System.out.println("FROM: " + from + "- TO: " + to);
//		}
		
		
		if(denum == 0)
			probability = 0.0;
		else
			probability = num/denum;
		
		if (this.probability < 0.001) this.probability = 0.001;
		if (this.probability > 0.999) this.probability = 1.0;
		
		return probability;
	}
	
	// flag = 0 --> Package A 
	// flag = 1 --> Package B
	public void setFlag (int x)
	{
		this.flag = x;
	}
	
	public int getFlag()
	{
		return this.flag;
	}
}
