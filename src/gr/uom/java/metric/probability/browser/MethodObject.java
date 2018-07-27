package gr.uom.java.metric.probability.browser;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class MethodObject {
	
	private String name;
	private String returnType;
	private List parameterList;
	private int access;
	private List<LocalVariableNode> localVariables;
	private List<Attribute> attrs;
	private InsnList instructions;
	private String className;

	public MethodObject(String name) {
		this.name = name;
		this.parameterList = new ArrayList();
	}
	
	public boolean addParameter(String parameterType) {
		return parameterList.add(parameterType);
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	public String getReturnType() {
		return returnType;
	}
	
	public ListIterator getParameterListIterator() {
		return parameterList.listIterator();
	}
	
	public String getName() {
		return name;
	}

	public String getClassName() {
		return className;
	}
	
	public int getAccess() {
		return access;
	}

	public void setAccess(int access_) {
		access = access_;
	}

	public void setLocalVariables(List<LocalVariableNode> localVariables_) {
		localVariables = localVariables_;
		
	}

	public void setAttributes(List<Attribute> attrs_) {
		attrs = attrs_;
		
	}

	public void setInstructions(InsnList instructions_) {
		instructions = instructions_;
		
	}

	public InsnList getInstructions() {
		return instructions;
	}
	
}