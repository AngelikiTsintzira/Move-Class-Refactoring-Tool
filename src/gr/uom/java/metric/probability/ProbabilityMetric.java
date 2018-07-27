package gr.uom.java.metric.probability;

import java.io.IOException;
import java.util.Formatter;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import gr.uom.java.metric.probability.xml.*;

public class ProbabilityMetric {
	
	private Hashtable dependenceTable;
	// in this list all the ClassAxisObjects with unsatisfied dependencies are stored
	private List unsatisfiedList;
	private List circleList;
	private List tempList;
	private SystemAxisObject system;
	//TASK 3
	private Hashtable dependencePTable;
	private List unsatisfiedPList;
	private List circlePList;
	private List tempPList;
	
	//private double propagationFactor;
	
//	public ProbabilityMetric(SystemAxisObject system, double propagationFactor) {
	public ProbabilityMetric(SystemAxisObject system) {
		dependenceTable = new Hashtable();
		dependencePTable = new Hashtable();
		unsatisfiedList = new ArrayList();
		unsatisfiedPList = new ArrayList();
		tempList = new ArrayList();
		tempPList = new ArrayList();
		this.system = system;
		circleList = new CircleTree(system).getCircleClassList();
		circlePList = new CircleTree(system).getCirclePackageList();
		//this.propagationFactor = propagationFactor;
		initializeDependenceTable();
		initializePDependenceTable();
	}
	
	private void initializeDependenceTable() {
		ListIterator it = system.getClassListIterator();
		
		while(it.hasNext()) {
			ClassAxisObject c = (ClassAxisObject)it.next();
			dependenceTable.put(c.getName(),new Boolean(false));
			c.printDependancies();
			if(!circleList.contains(c.getName())) {
				unsatisfiedList.add(c);
			}
		}
	}
	
	private void calculateCircleProbabilities() {
		ListIterator circleIt1 = circleList.listIterator();
		Hashtable tempProbTable1 = new Hashtable();
		// 1st iteration
		while(circleIt1.hasNext()) {
			String className = (String)circleIt1.next();
			ClassAxisObject c = system.getClass(className);
			ListIterator axisIt = c.getAxisListIterator();
			double totalProb = 0.0;
			
			while(axisIt.hasNext()) {
				Axis axis = (Axis)axisIt.next();
				if(!circleList.contains(axis.getToClass()) || axis.getToClass().equals(c.getName())) {
					double tempProb;
			
					if(axis.getToClass().equals(c.getName())) {
						tempProb = system.getInternalProbabilityOfClass(c.getName()).doubleValue();
					}
					else {
						tempProb = system.getProbabilityOfClass(axis.getToClass()) * axis.getProbability();
					}
					totalProb = totalProb + tempProb - totalProb*tempProb;
				}
			}
			tempProbTable1.put(className,new Double(totalProb));
		}
		
		ListIterator circleIt2 = circleList.listIterator();
		// 2nd iteration
		while(circleIt2.hasNext()) {
			String className = (String)circleIt2.next();
			ClassAxisObject c = system.getClass(className);
			c.setProbability(((Double)tempProbTable1.get(className)).doubleValue());
		}
		
		ListIterator circleIt3 = circleList.listIterator();
		Hashtable tempProbTable2 = new Hashtable();
		// 3rd iteration
		while(circleIt3.hasNext()) {
			String className = (String)circleIt3.next();
			ClassAxisObject c = system.getClass(className);
			ListIterator axisIt = c.getAxisListIterator();
			double totalProb = c.getProbability();
			
			while(axisIt.hasNext()) {
				Axis axis = (Axis)axisIt.next();
				if(circleList.contains(axis.getToClass()) && !axis.getToClass().equals(c.getName())) {
					double tempProb = system.getProbabilityOfClass(axis.getToClass()) * axis.getProbability();
					
					totalProb = totalProb + tempProb - totalProb*tempProb;
				}
			}
			tempProbTable2.put(className,new Double(totalProb));
		}
		
		ListIterator circleIt4 = circleList.listIterator();
		// 4th iteration
		while(circleIt4.hasNext()) {
			String className = (String)circleIt4.next();
			ClassAxisObject c = system.getClass(className);
			c.setProbability(((Double)tempProbTable2.get(className)).doubleValue());
			dependenceTable.put(c.getName(),new Boolean(true));
		}
		circleList.clear();
	}
	
	
	
	private boolean checkDependence(ClassAxisObject c) {
		ListIterator it = c.getAxisListIterator();
		
		while(it.hasNext()) {
			Axis axis = (Axis)it.next();
			if(!axis.getToClass().equals(c.getName())) {
				if( circleList.contains(axis.getToClass()) || 
				tempList.contains(system.getClass(axis.getToClass())) ) {
					unsatisfiedList.remove(c);
					tempList.add(c);
					return false;
				}
				else {
					Boolean bool = (Boolean)dependenceTable.get(axis.getToClass());
					if(bool.booleanValue() == false)
						return false;
				}
			}
		}
		
		return true;
	}
	
	// probabilities are stored in SystemAxisObject
	public void calculateClassProbabilities() {
		
		while(!unsatisfiedList.isEmpty()) {
			ListIterator it = system.getClassListIterator();
			while(it.hasNext()) {
				ClassAxisObject c = (ClassAxisObject)it.next();
				if(unsatisfiedList.contains(c)) {
					if(checkDependence(c)) {
						double temp1 = calculateClassProbability(c);
						//Calculate the using the External Axes
						//TODO calculateClassEProbability ?!?
						//double temp2 = calculateClassEProbability(c);
						c.setProbability(temp1);
						//c.setEProbability(temp2);
						dependenceTable.put(c.getName(),new Boolean(true));
						unsatisfiedList.remove(c);
					}
				}
			}
		}
		calculateCircleProbabilities();
		calculateCircleDependentProbabilities();
	}
	
	private void calculateCircleDependentProbabilities() {
		
		while(!tempList.isEmpty()) {
			ListIterator it = system.getClassListIterator();
			while(it.hasNext()) {
				ClassAxisObject c = (ClassAxisObject)it.next();
				if(tempList.contains(c)) {
					if(checkTempDependence(c)) {
						double temp1 = calculateClassProbability(c);
						double temp2 = calculateClassEProbability(c);
						c.setProbability(temp1);
						c.setEProbability(temp2);
						dependenceTable.put(c.getName(),new Boolean(true));
						tempList.remove(c);
					}
				}
			}
		}
	}
	
	private boolean checkTempDependence(ClassAxisObject c) {
		ListIterator it = c.getAxisListIterator();
		
		while(it.hasNext()) {
			Axis axis = (Axis)it.next();
			if(!axis.getToClass().equals(c.getName())) {
				Boolean bool = (Boolean)dependenceTable.get(axis.getToClass());
				if(bool.booleanValue() == false)
					return false;
			}
		}
		
		return true;
	}
	
	// this method assumes that the probabilities of
	// the superclasses or instances are already calculated
	private double calculateClassProbability(ClassAxisObject c) {
		double totalProb = 0.0;
		
		ListIterator ait = c.getAxisListIterator();
		while(ait.hasNext()) {
			Axis axis = (Axis)ait.next();
			double tempProb;
			
			if(axis.getToClass().equals(c.getName())) {
				tempProb = system.getInternalProbabilityOfClass(c.getName()).doubleValue();
			}
			else {
				tempProb = 
				// assuming that axis.getToClass() probability is already
				// calculated
				system.getProbabilityOfClass(axis.getToClass()) * axis.getProbability();
			}
			totalProb = totalProb + tempProb - totalProb*tempProb;
		}

		return totalProb;
	}
	
	private double calculateClassEProbability(ClassAxisObject c) {
		double totalProb = 0.0;
		
		ListIterator ait = c.getEAxisListIterator();
		while(ait.hasNext()) {
			Axis axis = (Axis)ait.next();
			double tempProb;
			
			if(axis.getToClass().equals(c.getName())) {
				tempProb = system.getInternalProbabilityOfClass(c.getName()).doubleValue();
			}
			else {
				tempProb = 
				// assuming that axis.getToClass() probability is already
				// calculated
				system.getProbabilityOfClass(axis.getToClass()) * axis.getProbability();
			}
			totalProb = totalProb + tempProb - totalProb*tempProb;
		}

		return totalProb;
	}
	
	/*/////////////////////////////////////////////
	 * TASK 3
	 */////////////////////////////////////////////
	
	private void initializePDependenceTable() {
		Iterator pit = system.getPackageSetIterator();
		
		while(pit.hasNext()) {
			PackageAxisObject p = (PackageAxisObject)pit.next();
			dependencePTable.put(p.getName(),new Boolean(false));
			//p.printDependancies();
			if(!circleList.contains(p.getName())) {
				unsatisfiedPList.add(p);
			}
		}
	}
	
	private void calculatePCircleProbabilities() {
		ListIterator circleIt1 = circlePList.listIterator();
		Hashtable tempProbTable1 = new Hashtable();
		// 1st iteration
		while(circleIt1.hasNext()) {
			String packageName = (String)circleIt1.next();
			PackageAxisObject p = system.getPackage(packageName);
			Iterator paxisIt = p.getPackageAxisSetIterator();
			double totalProb = 0.0;
			
			while(paxisIt.hasNext()) {
				PackageAxis paxis = (PackageAxis)paxisIt.next();
				if(!circlePList.contains(paxis.getToPackage()) || paxis.getToPackage().equals(p.getName())) {
					double tempProb;
			
					if(paxis.getToPackage().equals(p.getName())) {
						tempProb = system.getInternalProbabilityOfPackage(p.getName()).doubleValue();
					}
					else {
						tempProb = system.getProbabilityOfPackage(paxis.getToPackage()) * paxis.getProbability();
					}
					totalProb = totalProb + tempProb - totalProb*tempProb;
				}
			}
			tempProbTable1.put(packageName,new Double(totalProb));
		}
		
		ListIterator circleIt2 = circlePList.listIterator();
		// 2nd iteration
		while(circleIt2.hasNext()) {
			String packageName = (String)circleIt2.next();
			PackageAxisObject c = system.getPackage(packageName);
			c.setProbability(((Double)tempProbTable1.get(packageName)).doubleValue());
		}
		
		ListIterator circleIt3 = circlePList.listIterator();
		Hashtable tempProbTable2 = new Hashtable();
		// 3rd iteration
		while(circleIt3.hasNext()) {
			String packageName = (String)circleIt3.next();
			PackageAxisObject p = system.getPackage(packageName);
			Iterator paxisIt = p.getPackageAxisSetIterator();
			double totalProb = p.getProbability();
			
			while(paxisIt.hasNext()) {
				PackageAxis paxis = (PackageAxis)paxisIt.next();
				if(circlePList.contains(paxis.getToPackage()) && !paxis.getToPackage().equals(p.getName())) {
					double tempProb = system.getProbabilityOfClass(paxis.getToPackage()) * paxis.getProbability();
					
					totalProb = totalProb + tempProb - totalProb*tempProb;
				}
			}
			tempProbTable2.put(packageName,new Double(totalProb));
		}
		
		ListIterator circleIt4 = circlePList.listIterator();
		// 4th iteration
		while(circleIt4.hasNext()) {
			String packageName = (String)circleIt4.next();
			PackageAxisObject p = system.getPackage(packageName);
			p.setProbability(((Double)tempProbTable2.get(packageName)).doubleValue());
			dependencePTable.put(p.getName(),new Boolean(true));
		}
		circlePList.clear();
	}
	
	private boolean checkPDependence(PackageAxisObject p) {
		Iterator pit = p.getPackageAxisSetIterator();
		
		while(pit.hasNext()) {
			//KA9WS TA PackageAxis EINAI APO9UKEUMENA SE HASHTABLE NA 9UMI9W NA KANW
			//TIS ANTISTOIXES ALLAGES EDW STON METRITI pit
			PackageAxis paxis = (PackageAxis)pit.next();
			if(!paxis.getToPackage().equals(p.getName())) {
				if( circlePList.contains(paxis.getToPackage()) || 
				tempPList.contains(system.getPackage(paxis.getToPackage())) ) {
					unsatisfiedPList.remove(p);
					tempList.add(p);
					return false;
				}
				else {
					Boolean bool = (Boolean)dependencePTable.get(paxis.getToPackage());
					if(bool.booleanValue() == false)
						return false;
				}
			}
		}
		
		return true;
	}
	
	public void calculatePackageProbabilities() {
		
		while(!unsatisfiedPList.isEmpty()) {
			Iterator pit = system.getPackageSetIterator();
			while(pit.hasNext()) {
				PackageAxisObject p = (PackageAxisObject)pit.next();
				if(unsatisfiedPList.contains(p)) {
					if(checkPDependence(p)) {
						double temp = calculatePackageProbability(p);
						p.setProbability(temp);
						dependencePTable.put(p.getName(),new Boolean(true));
						unsatisfiedPList.remove(p);
					}
				}
			}
		}
		calculatePCircleProbabilities();
		calculatePCircleDependentProbabilities();
	}
private void calculatePCircleDependentProbabilities() {
		
		while(!tempList.isEmpty()) {
			ListIterator it = system.getClassListIterator();
			while(it.hasNext()) {
				PackageAxisObject c = (PackageAxisObject)it.next();
				if(tempPList.contains(c)) {
					if(checkTempPDependence(c)) {
						double temp = calculatePackageProbability(c);
						c.setProbability(temp);
						dependencePTable.put(c.getName(),new Boolean(true));
						tempPList.remove(c);
					}
				}
			}
		}
	}
	
	private boolean checkTempPDependence(PackageAxisObject p) {
		Iterator pit = p.getPackageAxisSetIterator();
		
		while(pit.hasNext()) {
			PackageAxis paxis = (PackageAxis)pit.next();
			if(!paxis.getToPackage().equals(p.getName())) {
				Boolean bool = (Boolean)dependencePTable.get(paxis.getToPackage());
				if(bool.booleanValue() == false)
					return false;
			}
		}
		
		return true;
	}
	
	// this method assumes that the probabilities of
	// the superclasses or instances are already calculated
	private double calculatePackageProbability(PackageAxisObject p) {
		double totalProb = 0.0;
		
		Iterator pit = p.getPackageAxisSetIterator();
		while(pit.hasNext()) {
			PackageAxis paxis = (PackageAxis)pit.next();
			
			double tempProb;
			//get the internal
			if(paxis.getToPackage().equals(p.getName())) {
				tempProb = system.getInternalProbabilityOfPackage(p.getName()).doubleValue();
			}
			else {
				tempProb = 
				// assuming that axis.getToClass() probability is already
				// calculated
				system.getProbabilityOfClass(paxis.getToPackage()) * paxis.getProbability();
			}
			totalProb = totalProb + tempProb - totalProb*tempProb;
		}

		return totalProb;
	}
	
	//TODO - Check INCOMING/OUTGOING
	public void calculateMetrics() {
		List<PackageAxis> paxisList;
		Formatter formatter;
		Set<PackageAxisObject> pSet = system.getPackageSet();
		Iterator<PackageAxisObject> pit = pSet.iterator();
		int ca_metric[] = new int[pSet.size()];
		int ce_metric[] = new int[pSet.size()];
		int i = 0;
		int ref = 0;
		float ins = 0;
		
		try {
			formatter = new Formatter("metrics.txt");
			formatter.format("====================== %s ======================%n", "Afferent Couplings");
			
			i = 0;
			while(pit.hasNext())
			{
				PackageAxisObject pao = (PackageAxisObject)pit.next();
				paxisList = getPackageAxisToPackage(pao);
				ref = paxisList.size();
				ca_metric[i] = ref;
				formatter.format("%s - %d%n", pao.getName(), ref);
				i++;
			}
			
			formatter.format("====================== %s ======================%n", "Efferent Couplings");
			pit = system.getPackageSetIterator();
			
			i = 0;
			while(pit.hasNext())
			{
				PackageAxisObject pao = (PackageAxisObject)pit.next();
				ref = pao.getNumberOfReferences();
				ce_metric[i] = ref;
				formatter.format("%s - %d%n", pao.getName(), ref);
				i++;
			}
			
			formatter.format("====================== %s ======================%n", "Instability");
			pit = system.getPackageSetIterator();
			
			i = 0;
			while(pit.hasNext())
			{
				PackageAxisObject pao = (PackageAxisObject)pit.next();
				float ce = ce_metric[i];
				float ca = ca_metric[i];
				ins = ce / (ca + ce);
				formatter.format("%s - %f%n", pao.getName(), ins);
				i++;
			}
			formatter.close();
		} catch (IOException exc) {
			System.out.println(exc.getMessage());
		}
	}
	
	private List<PackageAxis> getPackageAxisToPackage(PackageAxisObject packageAxisObject)
	{
		Iterator classIt = system.getPackageSetIterator();
		List list = new ArrayList();
		
		while(classIt.hasNext()) {
			PackageAxisObject pao = (PackageAxisObject)classIt.next();
			Hashtable paxis = pao.getPackageAxisSet();
			
			Set<PackageAxis> pSet = paxis.keySet();
			Iterator pit = pSet.iterator();
			
			while(pit.hasNext())
			{
				PackageAxis axis = (PackageAxis)paxis.get(pit.next());
				if( axis.getToPackage().equals(packageAxisObject.getName()) && 
					!axis.getToPackage().equals(pao.getName()) ) {
					list.add(axis);
				}
			}
		}
		return list;
	}
}