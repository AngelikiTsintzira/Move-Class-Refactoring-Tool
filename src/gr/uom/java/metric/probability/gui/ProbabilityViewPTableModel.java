package gr.uom.java.metric.probability.gui;

import javax.swing.table.AbstractTableModel;

import java.util.Iterator;
import java.util.ListIterator;
import gr.uom.java.metric.probability.xml.SystemAxisObject;
import gr.uom.java.metric.probability.xml.PackageAxisObject;
import gr.uom.java.metric.probability.xml.ClassAxisObject;

public class ProbabilityViewPTableModel extends AbstractTableModel {
	
	private String[] columnNames;
	private Object[][] data;
	
	public ProbabilityViewPTableModel(SystemAxisObject system) {
		columnNames = new String[2];
		columnNames[0] = "Package";
		columnNames[1] = "Probability";
		
		data = new Object[system.getPackageNumber()][2];		
		Iterator<PackageAxisObject> pit = system.getPackageSetIterator();
		int counter = 0;
		
		while(pit.hasNext()) {
			PackageAxisObject sp = pit.next();
			data[counter][0] = sp.getName();
			data[counter][1] = new Double(sp.getProbability());
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