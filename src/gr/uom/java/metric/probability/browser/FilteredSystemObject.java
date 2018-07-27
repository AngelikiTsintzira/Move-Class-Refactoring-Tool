package gr.uom.java.metric.probability.browser;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Set;

import gr.uom.java.metric.probability.xml.ClassAxisObject;
import gr.uom.java.metric.probability.xml.PackageAxisObject;
import gr.uom.java.metric.probability.xml.SystemAxisObject;

public class FilteredSystemObject {
	
	private List classList;
	private Set<FilteredPackageObject> packageSet;
	
	public FilteredSystemObject(SystemObject so) {
		filterSystemObject(so);
	}
	
	private void filterSystemObject(SystemObject so) {
		classList = new ArrayList();
		packageSet = new HashSet();
		
		HashSet cset = so.getClassNameSet();
		HashSet pset = so.getPackageNameSet();
		
		ListIterator cit = so.getClassListIterator();
		Iterator pit = so.getPackageSetIterator();
		while(cit.hasNext()) {
			ClassObject co = (ClassObject)cit.next();
			FilteredClassObject fco = new FilteredClassObject();
			
			fco.setName(co.getName());
			
			if(cset.contains(co.getSuperclass())) {
				fco.setSuperclass(co.getSuperclass());
			}
			
			ListIterator iterator = co.getInterfaceIterator();
			while(iterator.hasNext()) {
				String i = (String)iterator.next();
				if(cset.contains(i))
					fco.addInterface(i);
			}
			
			iterator = co.getConstructorIterator();
			while(iterator.hasNext()) {
				ConstructorObject con = (ConstructorObject)iterator.next();
				ListIterator innerIterator = con.getParameterListIterator();
				while(innerIterator.hasNext()) {
					String parameterType = (String)innerIterator.next();
					if(cset.contains(parameterType))
						fco.addReference(parameterType);
				}
			}
			
			iterator = co.getMethodIterator();
			while(iterator.hasNext()) {
				MethodObject method = (MethodObject)iterator.next();
				ListIterator innerIterator = method.getParameterListIterator();
				while(innerIterator.hasNext()) {
					String parameterType = (String)innerIterator.next();
					if(cset.contains(parameterType))
						fco.addReference(parameterType);
				}
			}
			
			iterator = co.getObjectInstantiationIterator();
			while(iterator.hasNext()) {
				String di = (String)iterator.next();
				if(cset.contains(di))
					fco.addObjectInstantiation(di);
			}
			
			iterator = co.getMethodInvocationIterator();
			while(iterator.hasNext()) {
				MethodInvocationObject mco = (MethodInvocationObject)iterator.next();
				if(cset.contains(mco.getOriginClassName()))
					if(!mco.getOriginClassName().equals(co.getName()))
					fco.addMethodInvocation(mco);
			}
			
			classList.add(fco);
		}
		
		while(pit.hasNext())
		{
			PackageObject po = (PackageObject)pit.next();
			FilteredPackageObject fpo = new FilteredPackageObject();
			
			fpo.setName(po.getName());
			cit = classList.listIterator();
			while(cit.hasNext())
			{
				FilteredClassObject fco = (FilteredClassObject)cit.next();
				if(so.filterObjectName(fco.getName()).endsWith(po.getName()))
					fpo.addClass(fco);
			}
			packageSet.add(fpo);
		}
	}
	
	public ListIterator getClassListIterator() {
		return classList.listIterator();
	}
	
	public SystemAxisObject getSystemAxisObject(SystemObject so) throws Exception {
		ListIterator cit = classList.listIterator();
		Iterator<FilteredPackageObject> pit = packageSet.iterator();
		SystemAxisObject sa = new SystemAxisObject();
		List<ClassAxisObject> caolist = new ArrayList<ClassAxisObject>();
		
		while(cit.hasNext()) {
			FilteredClassObject fc = (FilteredClassObject)cit.next();
			ClassAxisObject cao = fc.getClassAxisObject(so);
			
			caolist.add(cao);
			sa.addClass(cao);
		}
		
		cit = classList.listIterator();
		while(pit.hasNext())
		{
			
			FilteredPackageObject fpo = (FilteredPackageObject) pit.next();
			//ListIterator<ClassAxisObject> cait = caolist.listIterator();
			PackageAxisObject pa = fpo.getPackageAxisObject(so, caolist);
			sa.addPackage(pa);
		}
		
		return sa;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		ListIterator it = classList.listIterator();
		while(it.hasNext()) {
			sb.append(((FilteredClassObject)it.next()).toString() + "\n");
		}
		
		return sb.toString();
	}
}