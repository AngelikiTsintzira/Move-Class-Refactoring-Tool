package gr.uom.java.metric.probability.gui;

import javax.swing.table.AbstractTableModel;

import java.util.Iterator;
import java.util.ListIterator;
import gr.uom.java.metric.probability.xml.SystemAxisObject;
import gr.uom.java.metric.probability.xml.PackageAxisObject;
import gr.uom.java.metric.probability.xml.ClassAxisObject;

public class ProbabilityViewTableModel extends AbstractTableModel {
	
	private String[] columnNames;
	private Object[][] data;
	
	public ProbabilityViewTableModel(SystemAxisObject system) {
		columnNames = new String[2];
		columnNames[0] = "Class";
		columnNames[1] = "Probability";
		
		data = new Object[system.getClassNumber()][2];
		ListIterator it = system.getClassListIterator();
		int counter = 0;
		
		while(it.hasNext()) {
			ClassAxisObject ca = (ClassAxisObject)it.next();
			data[counter][0] = ca.getName();
			data[counter][1] = new Double(ca.getProbability());
			counter ++;
		}
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
}