package gr.uom.java.metric.probability.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.Hashtable;
import gr.uom.java.metric.probability.xml.XMLParser;
import gr.uom.java.metric.probability.xml.XMLGenerator;
import gr.uom.java.metric.probability.xml.SystemAxisObject;
import gr.uom.java.metric.probability.ProbabilityMetric;
import gr.uom.java.metric.probability.browser.ClassBrowser;
import gr.uom.java.metric.probability.browser.FilteredSystemObject;

public class ProbabilityFrame extends JFrame implements ActionListener, InternalFrameListener {
	private static JDesktopPane desktop;
	private static JFileChooser fc;
	private JMenuItem openProbEvalXML;
	private JMenuItem openDir;
	private JMenuItem saveProbEvalXML;
	private Hashtable table;
	private String activatedInternalFrame;
    private XMLFilter xmlFilter;
    private DirectoryFilter dirFilter;

    public static void main(String[] args) {
		ProbabilityFrame frame = new ProbabilityFrame();
	}
	
	public ProbabilityFrame() {
		super("Probabilistic Evaluation - Package");
		table = new Hashtable();
		
		JFrame.setDefaultLookAndFeelDecorated(true);
		desktop = new JDesktopPane();
		fc = new JFileChooser();
        xmlFilter = new XMLFilter();
        dirFilter = new DirectoryFilter();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        setContentPane(desktop);
        setSize(700,600);
        this.setLocationRelativeTo(null);
        //frame.pack();
        setVisible(true);
        desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu fileMenu;
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		
		openDir = new JMenuItem("Open Directory");
		openDir.addActionListener(this);
        fileMenu.add(openDir);
        
		openProbEvalXML = new JMenuItem("Open prob. evaluation XML");
		openProbEvalXML.addActionListener(this);
        fileMenu.add(openProbEvalXML);
		
		fileMenu.addSeparator();
		
		saveProbEvalXML = new JMenuItem("Save as prob. evaluation XML");
		saveProbEvalXML.addActionListener(this);
		fileMenu.add(saveProbEvalXML);

        return menuBar;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == openProbEvalXML) {
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			fc.setFileFilter(xmlFilter);
			int returnVal = fc.showOpenDialog(desktop);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                XMLParser parser = new XMLParser();
                try {
                	SystemAxisObject sa = parser.parseSystemXML(file);
                	sa.setName(file.toString());
                	ProbabilitySetDialog probabilitySetDialog = new ProbabilitySetDialog(this, sa);
                	//store in Hashtable
                	table.put(sa.getName(),sa);
                	//ProbabilityMetric metric = new ProbabilityMetric(sa, probabilitySetDialog.getPropagationFactor());
                	ProbabilityMetric metric = new ProbabilityMetric(sa);
                	metric.calculateClassProbabilities();
                	ProbabilityInternalFrame internalFrame = new ProbabilityInternalFrame(sa);
                	internalFrame.addInternalFrameListener(this);
                	desktop.add(internalFrame);
                	try {
        				internalFrame.setSelected(true);
    				} catch (java.beans.PropertyVetoException pve) {}
            	}
            	catch(org.jdom.JDOMException jdome) {}
            	catch(java.io.IOException ioe) {} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
		}
		try {
		if(e.getSource() == openDir) {
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setFileFilter(dirFilter);
			int returnVal = fc.showOpenDialog(desktop);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
            	//sa = to arxeio/fakelos pou epilegoume
				File file = fc.getSelectedFile();
				ClassBrowser b = new ClassBrowser(file);
				FilteredSystemObject fso = new FilteredSystemObject(b.getSystemObject());
				SystemAxisObject sa = fso.getSystemAxisObject(b.getSystemObject());
				sa.setName(file.toString());
				
				//Edw anoigei to para9uro me ta apotelesmata
				ProbabilitySetDialog probabilitySetDialog = new ProbabilitySetDialog(this, sa);
				//store in Hashtable
				table.put(sa.getName(),fso);
				/*System.out.println(sa.getName() + " - " + fso.toString() + " !!!");
				System.out.println("\n");*/
				
            	//ProbabilityMetric metric = new ProbabilityMetric(sa, probabilitySetDialog.getPropagationFactor());
            	ProbabilityMetric metric = new ProbabilityMetric(sa);
            	metric.calculateClassProbabilities();
            	metric.calculateMetrics();

            	ProbabilityInternalFrame internalFrame = new ProbabilityInternalFrame(sa);
            	internalFrame.addInternalFrameListener(this);
            	desktop.add(internalFrame);
            	try {
        			internalFrame.setSelected(true);
    			} catch (java.beans.PropertyVetoException pve) {}
			}
		}
		} catch (java.io.IOException ioe) {
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
		
		if(e.getSource() == saveProbEvalXML) {
			if(activatedInternalFrame != null) {
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setFileFilter(xmlFilter);
				int returnVal = fc.showSaveDialog(desktop);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
                	File file = fc.getSelectedFile();
                	Object o = table.get(activatedInternalFrame);
                	SystemAxisObject sa = null;
                	if(o instanceof FilteredSystemObject) {
                		FilteredSystemObject fso = (FilteredSystemObject)o;
						sa = fso.getSystemAxisObject(null);
                	}
                	else if(o instanceof SystemAxisObject) {
                		sa = (SystemAxisObject)o;
                	}
                	if(sa != null) {
                		if((file.toString().toLowerCase()).endsWith(".xml")) {
                			sa.setName(file.getName().substring(0,file.getName().lastIndexOf(".")));
                			XMLGenerator gen = new XMLGenerator();
                			gen.generateSystemXML(sa,file);
                		}
                		else {
                			sa.setName(file.getName());
                			XMLGenerator gen = new XMLGenerator();
                			gen.generateSystemXML(sa,new File(file.toString() + ".xml"));
                		}
                	}
            	}
			}
			else {
				JOptionPane.showMessageDialog(this,
    				"You must select an internal frame in order to save as xml.",
    				"Error",
					JOptionPane.ERROR_MESSAGE);
			}
		}
		} catch (java.io.IOException ioe) {
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void internalFrameActivated(InternalFrameEvent e) {
		activatedInternalFrame = e.getInternalFrame().getTitle();
		//System.out.println(e.getInternalFrame().getTitle() + " activated");
	}

 	public void internalFrameClosed(InternalFrameEvent e) {
 		if(e.getInternalFrame().getTitle().equals(activatedInternalFrame)) {
 			activatedInternalFrame = null;
 			table.remove(e.getInternalFrame().getTitle());
 		}
 		//System.out.println(e.getInternalFrame().getTitle() + " closed");
 	}

 	public void internalFrameClosing(InternalFrameEvent e) {
 		//System.out.println(e.getInternalFrame().getTitle() + " closing");
 	}

 	public void internalFrameDeactivated(InternalFrameEvent e) {
 		//System.out.println(e.getInternalFrame().getTitle() + " deactivated");
 	}

 	public void internalFrameDeiconified(InternalFrameEvent e) {
 		//System.out.println(e.getInternalFrame().getTitle() + " deiconified");
 	}

 	public void internalFrameIconified(InternalFrameEvent e) {
 		//System.out.println(e.getInternalFrame().getTitle() + " iconified");
 	}

 	public void internalFrameOpened(InternalFrameEvent e) {
 		//System.out.println(e.getInternalFrame().getTitle() + " opened");
 	}
}