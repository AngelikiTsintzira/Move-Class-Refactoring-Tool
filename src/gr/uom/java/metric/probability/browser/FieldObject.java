package gr.uom.java.metric.probability.browser;

public class FieldObject {
	
	private String name;
	private String type;
	private int access;
	
	public FieldObject(String type, String name, int acc) {
		this.type = type;
		this.name = name;
		this.access = acc;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access_) {
		access = access_;
	}
	
}