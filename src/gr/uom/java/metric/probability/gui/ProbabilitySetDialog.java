package gr.uom.java.metric.probability.gui;

import java.io.*;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import gr.uom.java.metric.probability.xml.ClassAxisObject;
import gr.uom.java.metric.probability.xml.SystemAxisObject;

public class ProbabilitySetDialog extends JDialog implements ActionListener, TableModelListener {
	
	private static JFileChooser fc;
	private JTable table;
	private JButton selectAll;
	private JButton selectNone;
	private JButton setValue;
	private JTextField probField;
	private JButton processButton;
	private JMenuItem loadFile;
	private JMenuItem setPropagationFactor;
	private double propagationFactor = 0.3;
	
	private SystemAxisObject system;
	
	public ProbabilitySetDialog(JFrame owner, SystemAxisObject system) {
		
		super(owner, "Set internal probabilities", true);
		//TEST!
		this.system = system;
		
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setFileFilter(new TxtFilter());

        JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		loadFile = new JMenuItem("Load probs from txt");
		loadFile.addActionListener(this);
        fileMenu.add(loadFile);
        setPropagationFactor = new JMenuItem("Set propagation factor");
		setPropagationFactor.addActionListener(this);
        fileMenu.add(setPropagationFactor);
        
        this.setJMenuBar(menuBar);
		
		table = new JTable(new ProbabilitySetTableModel(system));
		table.getModel().addTableModelListener(this);
		table.getColumnModel().getColumn(0).setMaxWidth(16);
		table.getColumnModel().getColumn(2).setMaxWidth(100);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Value must be between 0.0 and 1.0");
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		
		JScrollPane scrollPane = new JScrollPane(table);
		//table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		
		selectAll = new JButton("Select All");
		selectNone = new JButton("Select None");
		probField = new JTextField();
		probField.setMaximumSize(new Dimension(80,25));
		probField.setMinimumSize(new Dimension(80,25));
		probField.setToolTipText("Value must be between 0.0 and 1.0");
		setValue = new JButton("Set Value");
		
		selectAll.addActionListener(this);
		selectNone.addActionListener(this);
		setValue.addActionListener(this);
		
		buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		buttonPane.add(selectAll);
		buttonPane.add(Box.createHorizontalStrut(10));
		buttonPane.add(selectNone);
		//buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(Box.createHorizontalStrut(50));
		buttonPane.add(probField);
		buttonPane.add(Box.createHorizontalStrut(10));
		buttonPane.add(setValue);
		
		JPanel processPane = new JPanel();
		processPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		processButton = new JButton("Process Data");
		processButton.addActionListener(this);
		processPane.add(processButton);
		
		panel.add(scrollPane);
		panel.add(buttonPane);
		panel.add(processPane);
		
		this.setContentPane(panel);
		this.setSize(500,525);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        Object data = model.getValueAt(row, column);
        // Do something with the data...
    }
    
    private void setCheckboxes(boolean value) {
    		for(int i=0; i<table.getRowCount(); i++) {
    			table.setValueAt(new Boolean(value),i,0);
    		}
    }
    
    private void setInternalProbs(File file) {
    	try {
	    	FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String inputLine;
			StringTokenizer st;
			int counter = 0;
			String className = null;
			String internalProb = null;
			
			while ((inputLine = bufferedReader.readLine()) != null) {
				st = new StringTokenizer(inputLine);
				while (st.hasMoreTokens()) {
					if(counter == 0)
			   			className = st.nextToken();
			   		else if(counter == 1)
			   			internalProb = st.nextToken();
			   		
			   		counter++;
				}
				counter = 0;
				for(int i=0; i<table.getRowCount(); i++) {
					if( className.equals((String)table.getValueAt(i,1)) ) {
						table.setValueAt(Double.valueOf(internalProb),i,2);
						break;
					}
				}
			}
		}
		catch(FileNotFoundException fnfe) {}
		catch(IOException ioe) {}
    }
    
    public void setPropagationFactor(double propagationFactor) {
    	this.propagationFactor = propagationFactor;
    }
    
    public double getPropagationFactor() {
    	return this.propagationFactor;
    }
    
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource() == loadFile) {
			int returnVal = fc.showOpenDialog(this.getContentPane());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				setInternalProbs(fc.getSelectedFile());
			}
    	}
    	if(e.getSource() == setPropagationFactor) {
    		PropagationFactorDialog propagationFactorDialog = new PropagationFactorDialog(this, propagationFactor);
    	}
    	if(e.getSource() == selectAll) {
    		setCheckboxes(true);
    	}
    	if(e.getSource() == selectNone) {
    		setCheckboxes(false);
    	}
    	if(e.getSource() == setValue) {
    		try {
    			Double prob = Double.valueOf(probField.getText());
    			if(prob.doubleValue() < 0.0 || prob.doubleValue() > 1.0)
    				throw new NumberFormatException();
    			
    			for(int i=0; i<table.getRowCount(); i++) {
    				if( ((Boolean)table.getValueAt(i,0)).booleanValue() )
    					table.setValueAt(prob,i,2);
    			}
    		}
    		catch(NumberFormatException nfe) {}
    	}
    	if(e.getSource() == processButton) {
    		this.dispose();
    		//system.printClassesAndPackets();
    		//system.printReferences();
    	}
    }
}