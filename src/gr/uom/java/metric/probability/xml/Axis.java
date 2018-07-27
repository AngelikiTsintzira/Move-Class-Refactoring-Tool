package gr.uom.java.metric.probability.xml;

import gr.uom.java.metric.probability.browser.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Axis {

	private String description;
	private String from;
	private String to;
	private Double probability;
	private Double rfc = 0.0; // Distinct methods called from A to B (number)
	private Double nop = 0.0; // Number of polymorphic methods in superclass
	private Double nprf_used = 0.0; // Number of protected fields of the superclass used in A
	private Double npm = 0.0; // Number of total methods in B
	private Double na = 0.0; // Number of attributes
	private List<String> methodsCalled;

	public Axis(String desc, String toClass, String classFrom) {
		this.description = desc;
		this.to = toClass;
		this.from = classFrom;
		methodsCalled = new ArrayList<String>();
	}

	public void calculateMetrics (String classFrom, SystemObject so) {
		//Double rfc = 0.0;
		//List<String> methodsCalled = new ArrayList<String>(); // Distinct methods called from A to B (names) 
		List<String> methodsOwned = new ArrayList<String>(); // Distinct methods owned by B (names) 
		List<String> methodsNotFoundInTo = new ArrayList<String>(); // Methods called from A that are not owned by B (names)
		List<String> polymorphicMethodsOwned = new ArrayList<String>(); // Distinct methods owned by B (names) 
		//Double npm = 0.0;
		//Double nop = 0.0; 
		//Double nprf_used = 0.0; 
		//Double na = 0.0;
		String superclass = "";
		this.from = classFrom;

		// Search all classes
		for (int i = 0; i < so.getSize(); i++) {
			ClassObject c = so.getClass(i);				
			// Find class A
			if (c.getName().equals(classFrom)) {
				//System.out.println("Class " + classFrom + " calls: ");
				// Get all methods that class A calls 
				for (int j = 0; j < c.getSizeofMethodInvocationList(); j++) {
					MethodInvocationObject mio = c.getMethodInvocationList(j);
					// Check if these methods are part of B
					//if (classFrom.equals("org.jdom.Text") || classFrom.equals("org.jdom.adapters.AbstractDOMAdapter") || classFrom.equals("org.jdom.adapters.CrimsonDOMAdapter") ||classFrom.equals("org.jdom.Comment") || classFrom.equals("org.jdom.filter.OrFilter")  || classFrom.equals("org.jdom.input.JDOMParseException")) 
					//	System.out.println(mio.getOriginClassName() + "." + mio.getMethodName() + " code: " + mio.getCode());						
					if (mio.getOriginClassName().equals(this.to)) {
						// Check if the methods are not already declared in methodsCalled
						if (!methodsCalled.contains(mio.getOriginClassName() + "." + mio.getMethodName())) {								
							//if (classFrom.equals("org.jdom.Document") || classFrom.equals("org.jdom.Text") || classFrom.equals("org.jdom.adapters.AbstractDOMAdapter") || classFrom.equals("org.jdom.adapters.CrimsonDOMAdapter") ||classFrom.equals("org.jdom.Comment") || classFrom.equals("org.jdom.filter.OrFilter")  || classFrom.equals("org.jdom.input.JDOMParseException")) 
							//	System.out.println(mio.getOriginClassName() + "." + mio.getMethodName() + " code: " + mio.getCode());
							if (!mio.getMethodName().contains("<init>") && !mio.getMethodName().contains("<clinit>")) {
								rfc++;
								methodsCalled.add(mio.getOriginClassName() + "." + mio.getMethodName());
							}

						}							
						// Check if the method called is implemented in the superclass
					} else if (mio.getOriginClassName().equals(this.to) && mio.getCode() == 182 && (description.contains("extend") || description.contains("implement"))) {
						if (!methodsCalled.contains(mio.getOriginClassName() + "." + mio.getMethodName())) {
							//if (classFrom.equals("org.jdom.Document") || classFrom.equals("org.jdom.Text") || classFrom.equals("org.jdom.adapters.AbstractDOMAdapter") || classFrom.equals("org.jdom.adapters.CrimsonDOMAdapter") ||classFrom.equals("org.jdom.Comment") || classFrom.equals("org.jdom.filter.OrFilter")  || classFrom.equals("org.jdom.input.JDOMParseException")) 
							//	System.out.println(mio.getOriginClassName() + "." + mio.getMethodName() + " code: " + mio.getCode());
							if (!mio.getMethodName().contains("<init>") && !mio.getMethodName().contains("<clinit>")) {
								rfc++;
								methodsCalled.add(mio.getOriginClassName() + "." + mio.getMethodName());
							}
						}
					} else {
						methodsNotFoundInTo.add(mio.getOriginClassName() + "." + mio.getMethodName());
					}
				}

				// This for should calculate the protected fields that the method uses 
				//for (int j = 0; j < c.getSizeofMethodList(); j++) {
				//	//MethodObject mo = c.getMethodList(j);
				//	System.out.println("Method: " + mo.getName() + " uses: ");
				//	nprf_used = nprf;						
				//}
			}

			// Find class B
			if (c.getName().equals(this.to)) {
				//System.out.println("Class " + this.to + " owns: ");
				superclass = c.getSuperclass();
				// Get all methods declared on B
				for (int j = 0; j < c.getSizeofMethodList(); j++) {
					MethodObject mo = c.getMethodList(j);
					String mName =  mo.getClass().getName() + "." + mo.getName() + "(";							
					ListIterator<?> pi = mo.getParameterListIterator();							
					while(pi.hasNext()){
						mName = mName + pi.next() + ",";
					}
					mName = mName + ")";
					mName = mName.replace(",)", ")");

					//if (classFrom.equals("org.jdom.Text") || classFrom.equals("org.jdom.adapters.AbstractDOMAdapter") || classFrom.equals("org.jdom.adapters.CrimsonDOMAdapter") ||classFrom.equals("org.jdom.Comment")  ||classFrom.equals("org.jdom.filter.OrFilter"))
					//	System.out.println(mName);
					// If method is abstract
					if (mo.getAccess() == 1025 || mo.getAccess() == 1028) {
						//if the axis of change investigated in inheritance
						if (description.contains("extend") || description.contains("implement")) {
							if (!polymorphicMethodsOwned.contains(mo.getClass().getName() + "." + mo.getName())) {								
								nop++;
								polymorphicMethodsOwned.add(mo.getClass().getName() + "." + mo.getName());
							}
						}
					}

					if (!methodsOwned.contains(mName)) {								
						if (!mo.getName().contains("class$")) npm++;
						if (!mo.getName().contains("class$")) methodsOwned.add(mName);
					}
				}

				// Get all fields of B
				for (int j = 0; j < c.getSizeofFieldsList(); j++) {
					FieldObject fo = c.getFieldsList(j);
					//System.out.print(mo.getName());
					// If field is protected
					if (fo.getAccess() == 4) {
						// if the axis of change investigated in inheritance
						if (description.contains("extend") || description.contains("implement")) {
							nprf_used++;
						}
					}
				}

				na = (double) c.getSizeofFieldsList();

				// add constructors in the number of B methods
				//npm = npm + c.getSizeofConstructorList();
			}				
		}

		// Search all classes again to find superclass of B			
		for (int i = 0; i < so.getSize(); i++) {
			ClassObject c = so.getClass(i);				

			// Find superclass of B
			if (c.getName().equals(superclass)) {
				//System.out.println("Class " + this.to + " inherits: ");
				for (int j = 0; j < c.getSizeofMethodList(); j++) {
					MethodObject mo = c.getMethodList(j);
					//System.out.println(mo.getName() + " with code: " + mo.getAccess());
					// if method is abstract

					String mName =  mo.getClassName() + "." + mo.getName() + "(";							
					ListIterator<?> pi = mo.getParameterListIterator();							
					while(pi.hasNext()){
						mName = mName + pi.next() + ",";
					}
					mName = mName + ")";
					mName = mName.replace(",)", ")");

					//if (classFrom.equals("org.jdom.Text") || classFrom.equals("org.jdom.adapters.AbstractDOMAdapter") || classFrom.equals("org.jdom.adapters.CrimsonDOMAdapter") ||classFrom.equals("org.jdom.Comment") || classFrom.equals("org.jdom.filter.OrFilter")  || classFrom.equals("org.jdom.input.JDOMParseException")) { 
					//	System.out.println("Cheking if method:" + mo.getClassName() + "." + mo.getName() + " exists in:");
					//	for (int pos=0; pos<methodsNotFoundInTo.size(); pos++) {
					//		System.out.println("\t\t" + methodsNotFoundInTo.get(pos));
					//	}
					//}
					if (methodsNotFoundInTo.contains(mo.getClassName() + "." + mo.getName())) {
						if (!mo.getName().contains("<init>") && !mo.getName().contains("<clinit>")) {
							rfc++;
							methodsCalled.add(mo.getClassName() + "." + mo.getName());
							//System.out.println("\t\t\t\t\t Adding Method: " + mo.getClassName() + "." + mo.getName());
						}							
					}

					if (mo.getAccess() != 1025 && mo.getAccess() != 1028) {
						//if (classFrom.equals("org.jdom.Text") || classFrom.equals("org.jdom.adapters.AbstractDOMAdapter") || classFrom.equals("org.jdom.adapters.CrimsonDOMAdapter") ||classFrom.equals("org.jdom.Comment")  ||classFrom.equals("org.jdom.filter.OrFilter"))
						//	System.out.println(mName + " with code: " + mo.getAccess() + " is inherited!");
						if (!methodsOwned.contains(mName)) {								
							npm++;
							methodsOwned.add(mName);
						}
					}
				}	
			}
		}


		//if (classFrom.equals("org.jdom.Document") || classFrom.equals("org.jdom.Text") || classFrom.equals("org.jdom.adapters.AbstractDOMAdapter") || classFrom.equals("org.jdom.adapters.CrimsonDOMAdapter") ||classFrom.equals("org.jdom.Comment") || classFrom.equals("org.jdom.filter.OrFilter")  || classFrom.equals("org.jdom.input.JDOMParseException")) {
		//	System.out.println("RFC (" + classFrom + "->" + this.to + ")=" + rfc);
		//	System.out.println("NOP (" + this.to + ")=" + nop);
		//	System.out.println("NPM (" + this.to + ")=" + npm );
		//	//System.out.println("NPRF (" + this.to + ")=" + nprf);
		//	System.out.println("NPRF_USED (" + this.to + ")=" + nprf_used);
		//	System.out.println("NA (" + this.to + ")=" + na);	
		//}
	}


	public Axis updatePropagation(String classFrom, SystemObject so) throws Exception, IOException {

		PrintWriter writer = new PrintWriter(new FileOutputStream(new File("output.csv"), true));

		// Calculates possible propagation of change from class B to class A	
		System.out.println("Handling association from: " + classFrom + " to " + this.to + " type: " + description);
		if (classFrom.equals(this.to)) {
			this.probability = 0.0;
		} else {
			calculateMetrics(classFrom, so);
			// propagation_factor(A->B) = (RFC(A->B) + NOP(B) + NPRF_USED(B)) / (NOM(B) +NPRF(B))  
			if ( (npm + na) > 0) 
				this.probability = (rfc + nop + nprf_used) / (npm + na);
			else
				this.probability = 0.0;

			if (this.probability < 0.001) 
				this.probability = 0.001;

		}
		System.out.println("propagation_factor (" + classFrom + "->" + this.to+ ")=" + this.probability);		
		System.out.println("RFC: " + rfc + "\nNOP: " + nop + "\nNPRF_USED: " + nprf_used +
				"\nNPM: " + npm + "\nNA: " + na);

		if (this.probability > 0) {
			writer.println(classFrom + ";" + this.to+ ";" + this.probability);
			writer.close();
		}

		return this;
	}

	public String getDescription() {
		return description;
	}

	public String getFromClass() {
		return this.from;
	}

	public String getToClass() {
		return to;
	}

	public Double getProbability() {
		return probability;
	}

	public Double getRFC() {
		return rfc;
	}

	public Double getNOP() {
		return nop;
	}

	public Double getNPRF_USED() {
		return nprf_used;
	}

	/*public Double getNPRF() {
		return nprf;
	}*/

	public Double getNPM() {
		return npm;
	}

	public ListIterator<String> getMethodsCalledListIterator()
	{
		return methodsCalled.listIterator();
	}

	public Double getAttributes() {
		return na;
	}

	// Angeliki's Code

	public List<String> getMethodsCalled()
	{
		return this.methodsCalled;
	}

	public void setProbability(double prob)
	{
		this.probability = prob;
	}

	public void setRfc (double rfc)
	{
		this.rfc = rfc;
	}

	public void setNop (double nop)
	{
		this.nop = nop;
	}

	public void setNprfUsed (double nprf)
	{
		this.nprf_used = nprf;
	}

	public void setNpm (double npm)
	{
		this.npm = npm;
	}

	public void setNa (double na)
	{
		this.na = na;
	}

	public void setMethodsCalled(List<String> list)
	{
		this.methodsCalled.addAll(list);
	}
}