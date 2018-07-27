package gr.uom.java.metric.probability.browser;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

public class ClassObject {
	
	private String name;
	//private String pack;
	private List constructorList;
	private List methodList;
	private List fieldList;
	private String superclass;
	private List interfaceList;
	private List objectInstantiationList;
	private List methodInvocationList;
	//private boolean isInterface;
	//private boolean isAbstract;
	
	public ClassObject() {
		this.constructorList = new ArrayList();
		this.methodList = new ArrayList();
		this.interfaceList = new ArrayList();
		this.fieldList = new ArrayList();
		this.objectInstantiationList = new ArrayList();
		this.methodInvocationList = new ArrayList();
	}
	
	public void setSuperclass(String superclass) {
		this.superclass = superclass;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean addMethod(MethodObject method) {
		return methodList.add(method);
	}
	
	public boolean addInterface(String i) {
		return interfaceList.add(i);
	}
	
	public boolean addConstructor(ConstructorObject c) {
		return constructorList.add(c);
	}
	
	public boolean addField(FieldObject f) {
		return fieldList.add(f);
	}
	
	public boolean addObjectInstantiation(String di) {
		return objectInstantiationList.add(di);
	}
	
	public boolean addMethodInvocation(MethodInvocationObject mco) {
		return methodInvocationList.add(mco);
	}
	
	public ListIterator getConstructorIterator() {
		return constructorList.listIterator();
	}
	
	public ListIterator getMethodIterator() {
		return methodList.listIterator();
	}
	
	public ListIterator getInterfaceIterator() {
		return interfaceList.listIterator();
	}
	
	public ListIterator getFieldIterator() {
		return fieldList.listIterator();
	}
	
	public ListIterator getObjectInstantiationIterator() {
		return objectInstantiationList.listIterator();
	}
	
	public ListIterator getMethodInvocationIterator() {
		return methodInvocationList.listIterator();
	}
	
	public String getName() {
		return name;
	}
	
	public String getSuperclass() {
		return superclass;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		ListIterator it;
		
		sb.append("Class name : " + name + "\n");
		sb.append("Extended superclass : " + superclass + "\n");
		sb.append("Implemented interfaces :" + "\n");
		it = interfaceList.listIterator();
		while(it.hasNext()) {
			sb.append((String)it.next() + "\n");
		}
		it = constructorList.listIterator();
		while(it.hasNext()) {
			ListIterator iterator = ((ConstructorObject)it.next()).getParameterListIterator();
			sb.append("Constructor parameter types : ");
			while(iterator.hasNext()) {
				sb.append((String)iterator.next() + " ");
			}
			sb.append("\n");
		}
		
		it = methodList.listIterator();
		while(it.hasNext()) {
			MethodObject method = (MethodObject)it.next();
			sb.append("Method name : " + method.getName() + "\n");
			sb.append("Method return type : " + method.getReturnType() + "\n");
			ListIterator iterator = method.getParameterListIterator();
			sb.append("Method parameter types : ");
			while(iterator.hasNext()) {
				sb.append((String)iterator.next() + " ");
			}
			sb.append("\n");
		}
		
		sb.append("Fields :" + "\n");
		it = fieldList.listIterator();
		while(it.hasNext()) {
			FieldObject field = (FieldObject)it.next();
			sb.append(field.getType() + " " + field.getName() + "\n");
		}
		
		sb.append("Direct instances :" + "\n");
		it = objectInstantiationList.listIterator();
		while(it.hasNext()) {
			sb.append((String)it.next() + "\n");
		}
		
		sb.append("Method calls :" + "\n");
		it = methodInvocationList.listIterator();
		while(it.hasNext()) {
			MethodInvocationObject mco = (MethodInvocationObject)it.next();
			sb.append(mco.getOriginClassName() + " " + mco.getMethodName() + "\n");
		}
		
		return sb.toString();
	}
	
	public int getSizeofMethodInvocationList() {
		return methodInvocationList.size();
	}
	
	public MethodInvocationObject getMethodInvocationList(int i) {
		return (MethodInvocationObject) methodInvocationList.get(i);
	}

	public int getSizeofMethodList() {
		return methodList.size();
	}
	
	public MethodObject getMethodList(int i) {
		return (MethodObject) methodList.get(i);
	}

	public int getSizeofFieldsList() {
		return fieldList.size();
	}
	
	public FieldObject getFieldsList(int i) {
		return (FieldObject) fieldList.get(i);
	}


	public int getSizeofConstructorList() {
		return constructorList.size();
	}
	
	public ConstructorObject getConstructorList(int i) {
		return (ConstructorObject) constructorList.get(i);
	}
		
}