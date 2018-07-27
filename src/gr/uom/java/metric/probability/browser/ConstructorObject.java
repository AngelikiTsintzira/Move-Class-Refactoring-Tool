package gr.uom.java.metric.probability.browser;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

public class ConstructorObject {
	
	private List parameterList;
	
	public ConstructorObject() {
		this.parameterList = new ArrayList();
	}
	
	public boolean addParameter(String parameterType) {
		return parameterList.add(parameterType);
	}
	
	public ListIterator getParameterListIterator() {
		return parameterList.listIterator();
	}
}