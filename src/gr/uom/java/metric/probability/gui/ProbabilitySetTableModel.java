package gr.uom.java.metric.probability.gui;

import java.util.*;
import java.util.ListIterator;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import gr.uom.java.metric.probability.xml.ClassAxisObject;
import gr.uom.java.metric.probability.xml.SystemAxisObject;
import gr.uom.java.metric.probability.xml.PackageAxisObject;

public class ProbabilitySetTableModel extends AbstractTableModel {
	
	private String[] columnNames;
	private Object[][] data;
	SystemAxisObject system;
	
	public ProbabilitySetTableModel(SystemAxisObject system) {
		this.system = system;
		
		columnNames = new String[3];
		columnNames[0] = "";
		columnNames[1] = "Class";
		columnNames[2] = "Internal";

		Iterator pit = system.getPackageSetIterator();
		int pNum = system.getPackageNumber();
		int cNum = system.getClassNumber();
		data = new Object[cNum][3];
		ListIterator it = system.getClassListIterator();
		
		int counter = 0;
		while(it.hasNext()) {
			ClassAxisObject ca = (ClassAxisObject)it.next();
			data[counter][0] = new Boolean(false);
			data[counter][1] = ca.getName();
			data[counter][2] = system.getInternalProbabilityOfClass(ca.getName());
			counter++;
		}
		
		//TEST!
		/*int counter = 0;
		
		while(pit.hasNext())
		{
			PackageAxisObject pack = (PackageAxisObject) pit.next();
			if(!pack.getName().isEmpty())
			{
				data[counter][0] = new Boolean(false);
				data[counter][1] = pack.getName();
				data[counter][2] = Double.parseDouble("0.2");
				counter++;
			}
		}*/
	}
	
	public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }
    
	public Object getValueAt(int row, int col) {
        return data[row][col];
    }
    
    public Class getColumnClass(int c) {
    	return getValueAt(0, c).getClass();
	}
	
	public boolean isCellEditable(int row, int col) {
        if (col== 0 || col == 2) {
            return true;
        } else {
            return false;
        }
    }
    
    public void setValueAt(Object value, int row, int col) {
        if(col==2) {
        	try {
    			Double prob = (Double)value;
    			if(prob.doubleValue() < 0.0 || prob.doubleValue() > 1.0)
    				throw new NumberFormatException();
        		
        		data[row][col] = value;
        		fireTableCellUpdated(row, col);
        		//system.setInternalProbabilityOfClass((String)data[row][1],(Double)data[row][2]);
        		system.setInternalProbabilityOfPackage((String)data[row][1],(Double)data[row][2]);
        	}
        	catch(NumberFormatException nfe) {}
        }
        else {
        	data[row][col] = value;
        	fireTableCellUpdated(row, col);
    	}
        
    }
	
}