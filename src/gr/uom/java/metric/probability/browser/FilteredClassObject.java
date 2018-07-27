package gr.uom.java.metric.probability.browser;

import java.io.IOException;
import java.util.*;
import gr.uom.java.metric.probability.xml.Axis;
import gr.uom.java.metric.probability.xml.ClassAxisObject;
import gr.uom.java.metric.probability.xml.PackageAxis;
import gr.uom.java.metric.probability.xml.PackageAxisObject;

public class FilteredClassObject {
	
	private Set objectInstantiationSet;
	private Set referenceSet;
	private String superclass;
	private Set interfaceSet;
	private Set methodInvocationSet;
	private String name;
	
	public FilteredClassObject() {
		this.referenceSet = new HashSet();
		this.objectInstantiationSet = new HashSet();
		this.interfaceSet = new HashSet();
		this.methodInvocationSet = new HashSet();
	}
	
	public void setSuperclass(String superclass) {
		this.superclass = superclass;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean addInterface(String i) {
		return interfaceSet.add(i);
	}
	
	public boolean addReference(String r) {
		return referenceSet.add(r);
	}
	
	public boolean addObjectInstantiation(String di) {
		return objectInstantiationSet.add(di);
	}
	
	public boolean addMethodInvocation(MethodInvocationObject mco) {
		return methodInvocationSet.add(mco);
	}
	
	public Iterator getInterfaceIterator() {
		return interfaceSet.iterator();
	}
	
	public Iterator getReferenceIterator() {
		return referenceSet.iterator();
	}
	
	public Iterator getObjectInstantiationIterator() {
		return objectInstantiationSet.iterator();
	}
	
	public Iterator getMethodInvocationIterator() {
		return methodInvocationSet.iterator();
	}
	
	// returns the class names that method calls originate from
	// without duplicate values
	public Set getOriginClassNameSet() {
		Set set = new HashSet();
		
		Iterator it = methodInvocationSet.iterator();
		while(it.hasNext()) {
			MethodInvocationObject mco = (MethodInvocationObject)it.next();
			set.add(mco.getOriginClassName());
		}
		
		return set;
	}
	
	public List getMethodsOriginatingFromClass(String originClassName) {
		List list = new ArrayList();
		
		Iterator it = methodInvocationSet.iterator();
		while(it.hasNext()) {
			MethodInvocationObject mco = (MethodInvocationObject)it.next();
			if(mco.getOriginClassName().equals(originClassName))
				list.add(mco.getMethodName());
		}
		
		return list;
	}
	
	public ClassAxisObject getClassAxisObject(SystemObject so) throws IOException, Exception {
		ClassAxisObject ca = new ClassAxisObject(name);
		Set refSet = new HashSet(objectInstantiationSet);
		refSet.addAll(referenceSet);
		//remove reference to class' self
		refSet.remove(name);
		
		if(superclass != null) {
			if(refSet.contains(superclass)) {
				Axis axis = new Axis("extend axis + reference axis",superclass, ca.getName());
				ca.addAxis(axis, so);
				refSet.remove(superclass);
			}
			else {
				Axis axis = new Axis("extend axis",superclass, ca.getName());
				ca.addAxis(axis, so);
			}
		}
		
		Iterator it = interfaceSet.iterator();
		while(it.hasNext()) {
			String i = (String)it.next();
			if(refSet.contains(i)) {
				//TEST2
				Axis axis = new Axis("implement axis + reference axis",i, ca.getName());
				//PackageAxis paxis = new PackageAxis(getClassPacketName(i));
				ca.addAxis(axis, so);
				//ca.addPackageAxis(paxis, so);
				refSet.remove(i);
			}
			else {
				//TEST2
				Axis axis = new Axis("implement axis",i, ca.getName());
				//PackageAxis paxis = new PackageAxis(getClassPacketName(i));
				//ca.addPackageAxis(paxis, so);
				ca.addAxis(axis, so);
			}
		}

		Iterator iterator = refSet.iterator();
		while(iterator.hasNext()) {
			//TEST2
			String r = (String)iterator.next();
			Axis axis = new Axis("reference axis",r, ca.getName());
			//PackageAxis paxis = new PackageAxis(getClassPacketName(r));
			ca.addAxis(axis, so);
			//ca.addPackageAxis(paxis, so);
		}
		
		Axis axis = new Axis("internal axis",name, ca.getName());
		//PackageAxis paxis = new PackageAxis(getClassPacketName(name));
		ca.addAxis(axis, so);
		//ca.addPackageAxis(paxis, so);
		
		ListIterator ax = ca.getAxisListIterator();
		while(ax.hasNext())
		{
			Axis a = (Axis)ax.next();
			System.out.println(a.getToClass() + " !");
		}
		
		return ca;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator it;
		
		sb.append("Class name : " + name + "\n");
		sb.append("Extended superclass : " + superclass + "\n");
		sb.append("Implemented interfaces :" + "\n");
		it = interfaceSet.iterator();
		while(it.hasNext()) {
			sb.append((String)it.next() + "\n");
		}
		
		sb.append("References :" + "\n");
		it = referenceSet.iterator();
		while(it.hasNext()) {
			sb.append((String)it.next() + "\n");
		}
		
		sb.append("Direct instances :" + "\n");
		it = objectInstantiationSet.iterator();
		while(it.hasNext()) {
			sb.append((String)it.next() + "\n");
		}
		
		sb.append("Method calls :" + "\n");
		it = methodInvocationSet.iterator();
		while(it.hasNext()) {
			MethodInvocationObject mco = (MethodInvocationObject)it.next();
			sb.append(mco.getOriginClassName() + " " + mco.getMethodName() + "\n");
		}
		
		return sb.toString();
	}
}