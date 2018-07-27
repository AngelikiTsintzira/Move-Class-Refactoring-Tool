package gr.uom.java.metric.probability.browser;

public class MethodInvocationObject implements Comparable {
	private volatile int hashCode = 0;
	
	private String originClassName;
	private String methodName;
	private int code;
	
	public MethodInvocationObject(String originClassName, String methodName, int code_) {
		this.originClassName = originClassName;
		this.methodName = methodName;
		this.code = code_;
	}
	
	public String getOriginClassName() {
		return this.originClassName;
	}
	
	public String getMethodName() {
		return this.methodName;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public int compareTo(Object o) {
        MethodInvocationObject methodInvocationObject = (MethodInvocationObject)o;
        
        if(!(originClassName.equals(methodInvocationObject.getOriginClassName()))) {
        	if(originClassName.hashCode() < methodInvocationObject.getOriginClassName().hashCode()) {
        		return -1;
        	}
        	else {
        		return 1;
        	}
        }

        if(!(methodName.equals(methodInvocationObject.getMethodName()))) {
        	if(methodName.hashCode() < methodInvocationObject.getMethodName().hashCode()) {
        		return -1;
        	}
        	else {
        		return 1;
        	}
        }
        
        //equal 
        return 0;       
	}

	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		
		if (o instanceof MethodInvocationObject) {
			MethodInvocationObject methodInvocationObject = (MethodInvocationObject)o;
			
			return originClassName.equals(methodInvocationObject.getOriginClassName()) &&
			       methodName.equals(methodInvocationObject.getMethodName());
		}
		return false;
	}
	
	public int hashCode() {
        if (hashCode == 0) {
            int result = 17;
            result = 37*result + originClassName.hashCode();
            result = 37*result + methodName.hashCode();
            hashCode = result;
        }
        return hashCode;
    }
}