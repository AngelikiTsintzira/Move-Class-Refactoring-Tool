package gr.uom.java.metric.probability.gui;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.swing.*;
import javax.swing.table.*;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import gr.uom.java.metric.probability.browser.SystemObject;
import gr.uom.java.metric.probability.xml.*;

public class ProbabilityInternalFrame extends JInternalFrame {

	static int openFrameCount = 0;
	static final int xOffset = 30, yOffset = 30;
	private SystemAxisObject system;
	private JTabbedPane tabbedPane;
	static int f= 1;

	public ProbabilityInternalFrame(SystemAxisObject system) {
		super(system.getName(),true,true,true,true);
		this.system = system;
		tabbedPane = new JTabbedPane();

		JTable classProbTable = new JTable(new ProbabilityViewTableModel(system));
		classProbTable.getColumnModel().getColumn(0).setMinWidth(350);
		classProbTable.getColumnModel().getColumn(1).setMinWidth(100);
		classProbTable.setColumnSelectionAllowed(true);
		JScrollPane classProbScrollPane = new JScrollPane(classProbTable);

		//Antistoixo gia paketa
		JTable packageProbTable = new JTable(new ProbabilityViewPTableModel(system));
		packageProbTable.getColumnModel().getColumn(0).setMinWidth(350);
		packageProbTable.getColumnModel().getColumn(1).setMinWidth(100);
		packageProbTable.setColumnSelectionAllowed(true);
		JScrollPane packageProbScrollPane = new JScrollPane(packageProbTable);

		tabbedPane.addTab("Class probabilities", classProbScrollPane);
		tabbedPane.addTab("Package probabilities", packageProbScrollPane);

		String[] classColumnNames = new String[3];
		classColumnNames[0] = "Class";
		classColumnNames[1] = "Axis counter";
		classColumnNames[2] = "Receives ripple effect from";

		String[] packageColumnNames = new String[3];
		packageColumnNames[0] = "Package";
		packageColumnNames[1] = "Axis counter";
		packageColumnNames[2] = "Receives ripple effect from";

		//system.getClassNumber --- system.getPackageNumber
		DefaultTableModel defaultClassTableModel = new DefaultTableModel(classColumnNames, system.getClassNumber()) {
			public boolean isCellEditable(int row, int col) {
				if(col == 2)
					return true;
				else
					return false;
			}
		};

		DefaultTableModel defaultPackageTableModel = new DefaultTableModel(packageColumnNames, system.getPackageNumber()) {
			public boolean isCellEditable(int row, int col) {
				if(col == 2)
					return true;
				else
					return false;
			}
		};
		TableSorter classSorter = new TableSorter(defaultClassTableModel);

		JTable classAxisTable = new JTable(classSorter) {
			public TableCellRenderer getCellRenderer(int row, int column) {
				TableColumn tableColumn = getColumnModel().getColumn(column);
				TableCellRenderer renderer = tableColumn.getCellRenderer();
				if (renderer == null) {
					Class c = getColumnClass(column);
					if( c.equals(Object.class) ) {
						Object o = getValueAt(row,column);
						if( o != null )
							c = getValueAt(row,column).getClass();
					}
					renderer = getDefaultRenderer(c);
				}
				return renderer;
			}

			public TableCellEditor getCellEditor(int row, int column) {
				TableColumn tableColumn = getColumnModel().getColumn(column);
				TableCellEditor editor = tableColumn.getCellEditor();
				if (editor == null) {
					Class c = getColumnClass(column);
					if( c.equals(Object.class) ) {
						Object o = getValueAt(row,column);
						if( o != null )
							c = getValueAt(row,column).getClass();
					}
					editor = getDefaultEditor(c);
				}
				return editor;
			}
		};

		//Antistoixo Table gia ta paketa

		TableSorter packageSorter = new TableSorter(defaultPackageTableModel);

		JTable packageAxisTable = new JTable(packageSorter) {
			public TableCellRenderer getCellRenderer(int row, int column) {
				TableColumn tableColumn = getColumnModel().getColumn(column);
				TableCellRenderer renderer = tableColumn.getCellRenderer();
				if (renderer == null) {
					Class c = getColumnClass(column);
					if( c.equals(Object.class) ) {
						Object o = getValueAt(row,column);
						if( o != null )
							c = getValueAt(row,column).getClass();
					}
					renderer = getDefaultRenderer(c);
				}
				return renderer;
			}

			public TableCellEditor getCellEditor(int row, int column) {
				TableColumn tableColumn = getColumnModel().getColumn(column);
				TableCellEditor editor = tableColumn.getCellEditor();
				if (editor == null) {
					Class c = getColumnClass(column);
					if( c.equals(Object.class) ) {
						Object o = getValueAt(row,column);
						if( o != null )
							c = getValueAt(row,column).getClass();
					}
					editor = getDefaultEditor(c);
				}
				return editor;
			}
		};

		classSorter.setTableHeader(classAxisTable.getTableHeader());
		classAxisTable.setColumnSelectionAllowed(true);
		classAxisTable.getColumnModel().getColumn(0).setMinWidth(200);
		classAxisTable.getColumnModel().getColumn(1).setMinWidth(70);
		classAxisTable.getColumnModel().getColumn(1).setMaxWidth(80);
		classAxisTable.getColumnModel().getColumn(2).setMinWidth(200);
		classAxisTable.setDefaultRenderer(JComponent.class, new JComponentCellRenderer());
		classAxisTable.setDefaultEditor(JComponent.class, new JComponentCellEditor());

		packageSorter.setTableHeader(packageAxisTable.getTableHeader());
		packageAxisTable.setColumnSelectionAllowed(true);
		packageAxisTable.getColumnModel().getColumn(0).setMinWidth(200);
		packageAxisTable.getColumnModel().getColumn(1).setMinWidth(70);
		packageAxisTable.getColumnModel().getColumn(1).setMaxWidth(80);
		packageAxisTable.getColumnModel().getColumn(2).setMinWidth(200);
		packageAxisTable.setDefaultRenderer(JComponent.class, new JComponentCellRenderer());
		packageAxisTable.setDefaultEditor(JComponent.class, new JComponentCellEditor());

		ListIterator it = system.getClassListIterator();
		int counter = 0;

		while(it.hasNext()) {
			ClassAxisObject ca = (ClassAxisObject)it.next();
			classAxisTable.setValueAt(ca.getName(), counter, 0);
			List axisList = getAxisToClass(ca);
			classAxisTable.setValueAt(new Integer(axisList.size()), counter, 1);

			ListIterator axisListIterator = axisList.listIterator();
			JComboBox comboBox = new JComboBox();
			while(axisListIterator.hasNext()) {
				String axis = (String)axisListIterator.next();
				comboBox.addItem(axis);
			}
			classAxisTable.setValueAt(comboBox, counter, 2);
			counter ++;
		}

		Iterator<PackageAxisObject> pit = system.getPackageSetIterator();
		counter = 0;

		while(pit.hasNext())
		{
			PackageAxisObject pao = (PackageAxisObject)pit.next();
			packageAxisTable.setValueAt(pao.getName(), counter, 0);
			packageAxisTable.setValueAt(new Integer(pao.getNumberOfReferences()), counter, 1);

			List<PackageAxis> axisList = pao.getReferences();
			Iterator axisListIterator = axisList.iterator();
			JComboBox comboBox = new JComboBox();

			while(axisListIterator.hasNext())
			{
				PackageAxis axis = (PackageAxis)axisListIterator.next();
				comboBox.addItem(axis.getToPackage() + " - " + axis.getProbability());
			}
			packageAxisTable.setValueAt(comboBox, counter, 2);
			counter++;
		}


		JScrollPane classAxisScrollPane = new JScrollPane(classAxisTable);
		JScrollPane packageAxisScrollPane = new JScrollPane(packageAxisTable);
		tabbedPane.addTab("Class Axis browser", classAxisScrollPane);
		tabbedPane.addTab("Package Axis browser", packageAxisScrollPane);
		//table.setPreferredScrollableViewportSize(new Dimension(300, 100));
		this.setContentPane(tabbedPane);
		this.setVisible(true);
		this.setSize(600,500);
		this.setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
		openFrameCount++;
		//this.pack();
		moveClassRefactoring(system);
	}

	private List getAxisToClass(ClassAxisObject classAxisObject) {
		ListIterator classIt = system.getClassListIterator();
		List list = new ArrayList();

		while(classIt.hasNext()) {
			ClassAxisObject ca = (ClassAxisObject)classIt.next();
			ListIterator axisIt = ca.getAxisListIterator();

			while(axisIt.hasNext()) {
				Axis axis = (Axis)axisIt.next();
				if(axis.getFromClass().equals(classAxisObject.getName()) && !axis.getToClass().equals(axis.getFromClass()) ) {
					list.add(axis.getToClass() + " (propagation factor: " + axis.getProbability() + ")");
				} 
			}
		}

		return list;
	}

	class JComponentCellRenderer implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			return (JComponent)value;
		}
	}

	/**
	 * @param system
	 */
	public void moveClassRefactoring(SystemAxisObject system)
	{	
		System.out.println("------ Move Class Refactoring ------");
		System.out.println();

		ArrayList<Refactoring> refactoringObjects = new ArrayList<Refactoring>();
		ArrayList<Refactoring> refactoringObjectsReverse = new ArrayList<Refactoring>();

		// Iterate System Axis Object
		Set<PackageAxisObject> packageAxisObjectSet = system.getPackageSet();
		Iterator<PackageAxisObject> packageAxisIterator = packageAxisObjectSet.iterator();

		ArrayList <PackageAxis> packageAxisObjects =  new ArrayList <PackageAxis>();
		while(packageAxisIterator.hasNext()) 
		{

			PackageAxisObject packageAxisObject = packageAxisIterator.next(); 
			Hashtable<String, PackageAxis> classAxisObjectSet = packageAxisObject.getPackageAxisSet();

			for(String key : classAxisObjectSet.keySet())
			{
				PackageAxis packageAxis = classAxisObjectSet.get(key);
				packageAxisObjects.add(packageAxis);
			}
		}

		//%%%%%%%%%%%%%%%%%%%%%%%% CALCULATE METRICS %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		Map<String, List<Refactoring>> map = new HashMap<String, List<Refactoring>>();
		map = calculateMetrics(packageAxisObjects, system);

		refactoringObjects.addAll((List<Refactoring>) map.get("mainList"));
		refactoringObjectsReverse.addAll((List<Refactoring>) map.get("reverseList"));

		//%%%%%%%%%%%%%%%%%%%%%%%% WRITE IN FILE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		writeFile("refactoring_level1.csv",refactoringObjects);
		writeFile("reverseRefactoringLevel1.csv",refactoringObjectsReverse);

		//%%%%%%%%%%%%%%%%%%%%%%%% MOVE CLASSES SETS %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
		moveClassSets(5, refactoringObjects, packageAxisObjects, system, refactoringObjects);

	}

	public void moveClassSets(int numOfSets, ArrayList<Refactoring> refObject, ArrayList<PackageAxis> packAxisObject, SystemAxisObject system, ArrayList<Refactoring> initiallyRefObject)
	{	
		ArrayList<Axis> axisToBeAdded = new ArrayList<Axis>();
		ArrayList <Refactoring> newRefactoring = new ArrayList<Refactoring>();
		ArrayList<Refactoring> refactoringObjectsReverse = new ArrayList<Refactoring>();
		ArrayList<String> classesToBeMoved =  new ArrayList<String>();

		for (int i=0; i<refObject.size(); i++)
		{
			int j = i ;
			String packageFrom = refObject.get(j).getPackageNameFrom();
			String packageTo = refObject.get(j).getPackageNameTo();
			String fromClass = refObject.get(j).getFromClass();
			int resolvedCounter = 0;
			System.out.println("<---> From Package: " + packageFrom + "  <----> To Package: " + packageTo);

			// Case when the set include more than two classes together
			if (fromClass.contains("#"))
			{	
				classesToBeMoved.clear();
				String array[] = fromClass.split("#");
				for (String x : array)
				{
					System.out.print(x);
					classesToBeMoved.add(x);
				}
			}

			while (refObject.get(j).getFromClass().equals(fromClass))
			{
				System.out.println("<---> From class: " + refObject .get(j).getFromClass()
						+ " <---> To class: " + refObject.get(j).getToClass());

				if (refObject.get(j).getclassesCoCD() != null)
				{
					System.out.println("<---> Refactoring opportunities <--->");
					ArrayList<String> dependentClasses = new ArrayList<String>();
					ArrayList<String> dependentClassesToFrom = new ArrayList<String>();

					Iterator y = refObject.get(j).getclassesCoCD();
					while (y.hasNext())
					{
						// Step 1 : Pop class from CoCD
						String c1 = (String) y.next();
						System.out.println("<---> C1 class is : " + c1);
						dependentClasses.addAll(refObject.get(j).getListCoCDClasses());
						dependentClasses.remove(c1);
						dependentClassesToFrom.addAll(refObject.get(j).getListCoCDClassesFrom());
						dependentClassesToFrom.remove(c1);
						ArrayList<PackageAxis> tempPackageAxis = new ArrayList<PackageAxis> (packAxisObject);

						// Step 2 : Add c1 as class to be removed
						classesToBeMoved.add(c1);
						if (f == 1)
							classesToBeMoved.add(fromClass);

						// Step 3 : Remove dependency from Package Axis (packageFrom -> PackageTo)							
						ArrayList<Axis> axisToBeDeleted = new ArrayList<Axis>();
						for (int in = 0; in < tempPackageAxis.size(); in++)
						{
							if (tempPackageAxis.get(in).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(in).getToPackage().equals(packageTo))
							{
								int flag = 0;
								int flag1 = 0;
								int flag2 = 0;
								ArrayList <String> tempToCl =  new ArrayList<String>();
								ListIterator axisListIterator = tempPackageAxis.get(in).getAxisListIterator();
								while (axisListIterator.hasNext())
								{
									Axis axisObject = (Axis) axisListIterator.next();

									String innerClassTo = axisObject.getToClass();

									if (innerClassTo.contains("$")) 
									{
										int pos = innerClassTo.lastIndexOf("$");
										innerClassTo = innerClassTo.substring(0,pos);
									}

									String innerClassFrom = axisObject.getFromClass();

									if (innerClassFrom.contains("$")) 
									{
										int pos = innerClassFrom.lastIndexOf("$");
										innerClassFrom = innerClassFrom.substring(0,pos);
									}

									String classToPackage = innerClassTo;
									int p = classToPackage.lastIndexOf(".");
									classToPackage = classToPackage.substring(0,p);

									System.out.println();
									System.out.println("Dependency : " + axisObject.getFromClass() + " ---> " + axisObject.getToClass() );
									if (axisObject.getFromClass().equals(refObject.get(j).getFromClass()))
									{
										System.out.println("Delete this dependency from basic class: ");
										System.out.println(axisObject.getFromClass() + " ---- " + axisObject.getToClass());
										axisToBeDeleted.add(axisObject);
										
										// Add basic class
										int flag0 = 0;
										for (int c = 0; c < tempToCl.size(); c++) 
										{
											if (tempToCl.get(c).equals(innerClassTo)) 
											{
												flag0 = 1;
											}
										}

										if (flag0 == 0)
										{
											tempToCl.add(innerClassTo);
											flag++;
										}											
										
									}
									else if (innerClassFrom.equals(refObject.get(j).getFromClass()) )
									{
										System.out.println("Delete this dependency from inner class:  ");
										System.out.println(axisObject.getFromClass() + " ---- " + axisObject.getToClass());
										axisToBeDeleted.add(axisObject);
										
										// Add basic class
										int flag0 = 0;
										for (int c = 0; c < tempToCl.size(); c++) 
										{
											if (tempToCl.get(c).equals(innerClassTo)) 
											{
												flag0 = 1;
											}
										}

										if (flag0 == 0)
										{
											tempToCl.add(innerClassTo);
											flag1++;
										}
									}
									else if (c1.equals(innerClassFrom) )//&& innerClassTo.equals(refObject.get(j).getToClass()))
									{
										System.out.println("Delete this dependency from c1 class:  ");
										System.out.println(axisObject.getFromClass() + " ---- " + axisObject.getToClass());
										axisToBeDeleted.add(axisObject);
										flag2++;
									}
								}

								tempPackageAxis.get(in).DeleteAxis(axisToBeDeleted);

								resolvedCounter = flag + flag1 + flag2 ;
								
								if (f > 1)
								{
									resolvedCounter = 0;
									for (int cl = 0; cl < classesToBeMoved.size(); cl++)
									{
										for (int ref = 0; ref < initiallyRefObject.size(); ref++)
										{											
											if (initiallyRefObject.get(ref).getFromClass().equals(classesToBeMoved.get(cl)) &&
													initiallyRefObject.get(ref).getPackageNameTo().equals(refObject.get(j).getPackageNameTo()))
											{
												resolvedCounter = resolvedCounter + initiallyRefObject.get(ref).getCoRD();
												break;
											}
										}
									}
								}
							}

						} // End of Step 3

						// Step 4.1 : Check dependencies from c1 to all classes in package A (packageFrom)

						ListIterator it = system.getClassListIterator();
						int flagToBreak = 0;
						while(it.hasNext()) 
						{
							ClassAxisObject ca = (ClassAxisObject)it.next();	
							ListIterator axisListIt = ca.getAxisListIterator(); //internal
							
							if (ca.getPackageName().equals(packageFrom))
							{
								flagToBreak = 1;
							}
							if (flagToBreak == 1 && !ca.getPackageName().equals(packageFrom))
							{
								break;
							}
							
							if (ca.getPackageName().equals(packageFrom) && ca.getName().equals(c1))
							{
								while(axisListIt.hasNext()) 
								{
									Axis axisObject = (Axis)axisListIt.next();

									String innerClassTo = axisObject.getToClass();

									if (innerClassTo.contains("$")) 
									{
										int pos = innerClassTo.indexOf("$");
										innerClassTo = innerClassTo.substring(0,pos);
									}

									String innerClassFrom = axisObject.getFromClass();

									if (innerClassFrom.contains("$")) 
									{
										int pos = innerClassFrom.indexOf("$");
										innerClassFrom = innerClassFrom.substring(0,pos);
									}

									String classToPackage = innerClassTo;
									int p = classToPackage.lastIndexOf(".");
									classToPackage = classToPackage.substring(0,p);

									System.out.println("Inner class from : " + innerClassFrom + 
											" Inner class to: " + innerClassTo);

									// Step 4.2 : Add class to CoCD
									if (!innerClassFrom.equals(innerClassTo) && classToPackage.equals(packageFrom) )
									{
										System.out.println("Class to be added in  Cocd : " + axisObject.getToClass());
										if (!dependentClasses.contains(innerClassTo) &&
												!innerClassTo.equals(c1))
										{
											if (!classesToBeMoved.contains(axisObject.getToClass()))
											{
												dependentClasses.add(axisObject.getToClass());
											}
										}
										// Step 4.3 : Update PackageAxis (A->B)												
										int c = innerClassFrom.lastIndexOf(".");
										String newClass =  innerClassFrom.substring(c, innerClassFrom.length() );
										newClass = packageTo + newClass;											
										Axis newDependency = new Axis(axisObject.getDescription(),innerClassTo, innerClassFrom);
										newDependency.setMethodsCalled(axisObject.getMethodsCalled());
										newDependency.setNa(axisObject.getAttributes());
										newDependency.setNpm(axisObject.getNPM());
										newDependency.setNprfUsed(axisObject.getNPRF_USED());
										newDependency.setNop(axisObject.getNOP());
										newDependency.setRfc(axisObject.getRFC());
										newDependency.setProbability(axisObject.getProbability());
										// Β -- Α
										//axisToBeAdded.add(newDependency);
									}

								}
							}

							// Dependencies to class FROM
							else if (ca.getPackageName().equals(packageFrom) && !classesToBeMoved.contains(ca.getName()))
							{
								while(axisListIt.hasNext()) 
								{
									Axis axisObject = (Axis)axisListIt.next();

									String innerClassTo = axisObject.getToClass();

									if (innerClassTo.contains("$")) 
									{
										int pos = innerClassTo.indexOf("$");
										innerClassTo = innerClassTo.substring(0,pos);
									}

									String innerClassFrom = axisObject.getFromClass();

									if (innerClassFrom.contains("$")) 
									{
										int pos = innerClassFrom.indexOf("$");
										innerClassFrom = innerClassFrom.substring(0,pos);
									}

									String classFromPackage = innerClassFrom;
									int p = classFromPackage.lastIndexOf(".");
									classFromPackage = classFromPackage.substring(0,p);

									// Step 4.2 : Add class to CoCD

									if (classesToBeMoved.contains(innerClassTo) && classFromPackage.equals(packageFrom) )
									{
										System.out.println("CoCD TO-FROM: " + innerClassFrom + "-->" + innerClassTo);

										if (!dependentClassesToFrom.contains(innerClassFrom) && !fromClass.equals(innerClassFrom)
												&& !c1.equals(innerClassFrom) && !classesToBeMoved.contains(innerClassFrom))
										{
											dependentClassesToFrom.add(innerClassFrom);
											System.out.println("Class to be added in  CoCD TO-FROM: " + innerClassFrom);
										}
										// Step 4.3 : Update PackageAxis (A->B)												
										int c = innerClassFrom.lastIndexOf(".");
										String newClass =  innerClassFrom.substring(c, innerClassFrom.length() );
										newClass = packageTo + newClass;											
										Axis newDependency = new Axis(axisObject.getDescription(),innerClassTo, innerClassFrom);
										newDependency.setMethodsCalled(axisObject.getMethodsCalled());
										newDependency.setNa(axisObject.getAttributes());
										newDependency.setNpm(axisObject.getNPM());
										newDependency.setNprfUsed(axisObject.getNPRF_USED());
										newDependency.setNop(axisObject.getNOP());
										newDependency.setRfc(axisObject.getRFC());
										newDependency.setProbability(axisObject.getProbability());
										axisToBeAdded.add(newDependency);
										// Α -> Β
									}
								}
							} 
						} //while

						for (int count = 0; count < tempPackageAxis.size(); count++)
						{
							if (tempPackageAxis.get(count).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(count).getToPackage().equals(packageTo))
							{	
								for (int count1 = 0 ; count1 < axisToBeAdded.size(); count1++)
								{												
									tempPackageAxis.get(count).addAxis(axisToBeAdded.get(count1));
								}
								break;
							}
						}

						// Step 5 : Calculate Metrics (Intensity & MCPM)						

						Refactoring newRefObject = new Refactoring();
						newRefObject.setFromclass(fromClass + "#" + c1); 
						newRefObject.setToclass(refObject.get(j).getToClass());
						newRefObject.setPackageNameFrom(packageFrom);
						newRefObject.setPackageNameTo(packageTo);
						for (int k = 0; k < dependentClasses.size(); k++)
							newRefObject.setClassesCoCD(dependentClasses.get(k));
						for (int k = 0; k < dependentClassesToFrom.size(); k++)
							newRefObject.setClassesCoCDFrom(dependentClassesToFrom.get(k));
						newRefObject.setIntensityOld(refObject.get(j).getIntensityOld());
						newRefObject.setIntensityNew(refObject.get(j).getIntensityOld() - resolvedCounter + dependentClassesToFrom.size());
						newRefObject.setCoCD(dependentClasses.size());
						newRefObject.setCoCDFrom(dependentClassesToFrom.size());
						newRefObject.setCoRD(resolvedCounter);
						newRefObject.setMcpmOld(refObject.get(j).getMpcmOld());
						newRefObject.setRem(refObject.get(j).getRem());


						for (int in = 0; in < tempPackageAxis.size(); in++)
						{
							if (tempPackageAxis.get(in).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(in).getToPackage().equals(packageTo))
							{
								tempPackageAxis.get(in).updatePropagation();
								newRefObject.setMcpmNew(tempPackageAxis.get(in).getProbability());
								break;
							}
						}

						// Restore the object to start state
						for (int in = 0; in < tempPackageAxis.size(); in++)
						{
							if (tempPackageAxis.get(in).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(in).getToPackage().equals(packageTo))
							{
								for (int p = 0; p < axisToBeDeleted.size(); p++)
								{
									tempPackageAxis.get(in).addAxis(axisToBeDeleted.get(p));
								}
								tempPackageAxis.get(in).updatePropagation();
							}
						}

						for (int count = 0; count < tempPackageAxis.size(); count++)
						{
							if (tempPackageAxis.get(count).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(count).getToPackage().equals(packageTo))
							{	
								tempPackageAxis.get(count).DeleteAxis(axisToBeAdded);
								axisToBeAdded.clear();
								break;
							}
						}

						ArrayList<Refactoring> tempRef = new ArrayList<Refactoring>();
						tempRef.addAll(reverseCheckSetOfClasses (tempPackageAxis, system, packageFrom, packageTo, refObject.get(j).getToClass()));
						for (int p = 0; p < tempRef.size(); p++)
						{
							tempRef.get(p).setIntensityOld(refObject.get(j).getIntensityOld());
							tempRef.get(p).setMcpmOld(refObject.get(j).getMpcmOld());
						}
						refactoringObjectsReverse.addAll(tempRef);
						
						// Step 6 : Push new Refactoring Object to check for extra classes
						newRefactoring.add(newRefObject);

						dependentClasses.clear();
						dependentClassesToFrom.clear();
						classesToBeMoved.remove(c1);
						if (f == 1)
							classesToBeMoved.remove(fromClass);
					} // End of while there are CoCD classes
				}  // End of if there are CoCD classes	


				// OTHER CLASSES TO FROM				

				resolvedCounter = 0;

				// Case when the set include more than two classes together
				if (fromClass.contains("#"))
				{	
					classesToBeMoved.clear();
					String array[] = fromClass.split("#");
					for (String x : array)
					{
						System.out.print(x);
						classesToBeMoved.add(x);
					}
				}

				if (refObject.get(j).getclassesCoCDFrom() != null)
				{
					System.out.println("<---> Refactoring opportunities <--->");
					ArrayList<String> dependentClasses = new ArrayList<String>();
					ArrayList<String> dependentClassesToFrom = new ArrayList<String>();

					Iterator y = refObject.get(j).getclassesCoCDFrom();
					while (y.hasNext())
					{
						// Step 1 : Pop class from CoCD
						String c1 = (String) y.next();
						System.out.println("<---> C1 class is : " + c1);
						dependentClasses.addAll(refObject.get(j).getListCoCDClasses());
						dependentClasses.remove(c1);
						dependentClassesToFrom.addAll(refObject.get(j).getListCoCDClassesFrom());
						dependentClassesToFrom.remove(c1);
						//dependentClassesToFrom.removeAll(classesToBeMoved);
						ArrayList<PackageAxis> tempPackageAxis = new ArrayList<PackageAxis> (packAxisObject);

						// Step 2 : Add c1 as class to be removed
						classesToBeMoved.add(c1);
						if (f == 1)
							classesToBeMoved.add(fromClass);

						// Step 3 : Remove dependency from Package Axis (packageFrom -> PackageTo)							
						ArrayList<Axis> axisToBeDeleted = new ArrayList<Axis>();
						for (int in = 0; in < tempPackageAxis.size(); in++)
						{
							if (tempPackageAxis.get(in).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(in).getToPackage().equals(packageTo))
							{
								int flag = 0;
								int flag1 = 0;
								int flag2 = 0;
								ListIterator axisListIterator = tempPackageAxis.get(in).getAxisListIterator();
								while (axisListIterator.hasNext())
								{
									Axis axisObject = (Axis) axisListIterator.next();

									String innerClassTo = axisObject.getToClass();

									if (innerClassTo.contains("$")) 
									{
										int pos = innerClassTo.lastIndexOf("$");
										innerClassTo = innerClassTo.substring(0,pos);
									}

									String innerClassFrom = axisObject.getFromClass();

									if (innerClassFrom.contains("$")) 
									{
										int pos = innerClassFrom.lastIndexOf("$");
										innerClassFrom = innerClassFrom.substring(0,pos);
									}

									String classToPackage = innerClassTo;
									int p = classToPackage.lastIndexOf(".");
									classToPackage = classToPackage.substring(0,p);

									System.out.println();
									System.out.println("Dependency : " + axisObject.getFromClass() + " ---> " + axisObject.getToClass() );
									if (axisObject.getFromClass().equals(refObject.get(j).getFromClass()) &&
											axisObject.getToClass().equals(refObject.get(j).getToClass()))
									{
										System.out.println("Delete this dependency from basic class REVERSE: ");
										System.out.println(axisObject.getFromClass() + " ---- " + axisObject.getToClass());
										axisToBeDeleted.add(axisObject);
										flag = 1;
									}
									else if (innerClassFrom.equals(refObject.get(j).getFromClass()) &&
											innerClassTo.equals(refObject.get(j).getToClass()))
									{
										System.out.println("Delete this dependency from inner class REVERSE:  ");
										System.out.println(axisObject.getFromClass() + " ---- " + axisObject.getToClass());
										axisToBeDeleted.add(axisObject);
										if (flag == 0)
											flag1 = 1;
									}
									else if (c1.equals(innerClassFrom) && innerClassTo.equals(refObject.get(j).getToClass()))
									{
										System.out.println("Delete this dependency from c1 class REVERSE:  ");
										System.out.println(axisObject.getFromClass() + " ---- " + axisObject.getToClass());
										axisToBeDeleted.add(axisObject);
										flag2 = 1;
									}
								}

								tempPackageAxis.get(in).DeleteAxis(axisToBeDeleted);

								if (flag > 0)
								{
									resolvedCounter = flag2 + flag;
								}
								else if (flag == 0)
								{
									resolvedCounter = flag2 + flag1 + 1;
								}

								resolvedCounter = flag + flag1 + flag2 ;
								System.out.println(resolvedCounter);
								if (f > 1)
								{
									resolvedCounter = 0;
									for (int cl = 0; cl < classesToBeMoved.size(); cl++)
									{
										for (int ref = 0; ref < initiallyRefObject.size(); ref++)
										{											
											if (initiallyRefObject.get(ref).getFromClass().equals(classesToBeMoved.get(cl)) &&
													initiallyRefObject.get(ref).getToClass().equals(refObject.get(j).getToClass()))
											{
												resolvedCounter = resolvedCounter + initiallyRefObject.get(ref).getCoRD();
												break;
											}
										}
									}
								}
							}

						} // End of Step 3

						// Step 4.1 : Check dependencies from c1 to all classes in package A (packageFrom)

						ListIterator it = system.getClassListIterator();
						int flagToBreak = 0;

						while(it.hasNext()) 
						{
							ClassAxisObject ca = (ClassAxisObject)it.next();	
							ListIterator axisListIt = ca.getAxisListIterator(); //internal
							
							if (ca.getPackageName().equals(packageFrom))
							{
								flagToBreak = 1;
							}
							if (flagToBreak == 1 && !ca.getPackageName().equals(packageFrom))
							{
								break;
							}
							
							if (ca.getPackageName().equals(packageFrom) && ca.getName().equals(c1))
							{
								while(axisListIt.hasNext()) 
								{
									Axis axisObject = (Axis)axisListIt.next();

									String innerClassTo = axisObject.getToClass();

									if (innerClassTo.contains("$")) 
									{
										int pos = innerClassTo.indexOf("$");
										innerClassTo = innerClassTo.substring(0,pos);
									}

									String innerClassFrom = axisObject.getFromClass();

									if (innerClassFrom.contains("$")) 
									{
										int pos = innerClassFrom.indexOf("$");
										innerClassFrom = innerClassFrom.substring(0,pos);
									}

									String classToPackage = innerClassTo;
									int p = classToPackage.lastIndexOf(".");
									classToPackage = classToPackage.substring(0,p);

									System.out.println("Inner class from : " + innerClassFrom + 
											" Inner class to: " + innerClassTo);

									// Step 4.2 : Add class to CoCD
									if (!innerClassFrom.equals(innerClassTo) && classToPackage.equals(packageFrom) )
									{
										System.out.println("Class to be added in  Cocd : " + axisObject.getToClass());
										if (!dependentClasses.contains(innerClassTo) &&
												!innerClassTo.equals(c1))
										{
											if (!classesToBeMoved.contains(axisObject.getToClass()))
											{
												dependentClasses.add(axisObject.getToClass());
											}
										}
										// Step 4.3 : Update PackageAxis (A->B)												
										int c = innerClassFrom.lastIndexOf(".");
										String newClass =  innerClassFrom.substring(c, innerClassFrom.length() );
										newClass = packageTo + newClass;											
										Axis newDependency = new Axis(axisObject.getDescription(),innerClassTo, innerClassFrom);
										newDependency.setMethodsCalled(axisObject.getMethodsCalled());
										newDependency.setNa(axisObject.getAttributes());
										newDependency.setNpm(axisObject.getNPM());
										newDependency.setNprfUsed(axisObject.getNPRF_USED());
										newDependency.setNop(axisObject.getNOP());
										newDependency.setRfc(axisObject.getRFC());
										newDependency.setProbability(axisObject.getProbability());
										//axisToBeAdded.add(newDependency);
										// Β -> Α
									}

								}
							}

							// Dependencies to class FROM
							else if (ca.getPackageName().equals(packageFrom) && !classesToBeMoved.contains(ca.getName()))
							{
								while(axisListIt.hasNext()) 
								{
									Axis axisObject = (Axis)axisListIt.next();

									String innerClassTo = axisObject.getToClass();

									if (innerClassTo.contains("$")) 
									{
										int pos = innerClassTo.indexOf("$");
										innerClassTo = innerClassTo.substring(0,pos);
									}

									String innerClassFrom = axisObject.getFromClass();

									if (innerClassFrom.contains("$")) 
									{
										int pos = innerClassFrom.indexOf("$");
										innerClassFrom = innerClassFrom.substring(0,pos);
									}

									String classFromPackage = innerClassFrom;
									int p = classFromPackage.lastIndexOf(".");
									classFromPackage = classFromPackage.substring(0,p);

									// Step 4.2 : Add class to CoCD

									if (classesToBeMoved.contains(innerClassTo) && classFromPackage.equals(packageFrom) )
									{
										//System.out.println("TEST 1  CoCD TO-FROM: " + innerClassFrom + "-->" + innerClassTo);

										if (!dependentClassesToFrom.contains(innerClassFrom) && !fromClass.equals(innerClassFrom)
												&& !c1.equals(innerClassFrom) && !classesToBeMoved.contains(innerClassFrom))
										{
											dependentClassesToFrom.add(innerClassFrom);
											System.out.println("Class to be added in  CoCD TO-FROM: " + innerClassFrom);
										}
										// Step 4.3 : Update PackageAxis (A->B)												
										int c = innerClassFrom.lastIndexOf(".");
										String newClass =  innerClassFrom.substring(c, innerClassFrom.length() );
										newClass = packageTo + newClass;											
										Axis newDependency = new Axis(axisObject.getDescription(),innerClassTo, innerClassFrom);
										newDependency.setMethodsCalled(axisObject.getMethodsCalled());
										newDependency.setNa(axisObject.getAttributes());
										newDependency.setNpm(axisObject.getNPM());
										newDependency.setNprfUsed(axisObject.getNPRF_USED());
										newDependency.setNop(axisObject.getNOP());
										newDependency.setRfc(axisObject.getRFC());
										newDependency.setProbability(axisObject.getProbability());
										axisToBeAdded.add(newDependency);
										//Α->Β
									}
								}
							} 
						} //while

						for (int count = 0; count < tempPackageAxis.size(); count++)
						{
							if (tempPackageAxis.get(count).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(count).getToPackage().equals(packageTo))
							{	
								for (int count1 = 0 ; count1 < axisToBeAdded.size(); count1++)
								{												
									tempPackageAxis.get(count).addAxis(axisToBeAdded.get(count1));
								}
								break;
							}
						}

						// Step 5 : Calculate Metrics (Intensity & MCPM)						

						Refactoring newRefObject = new Refactoring();
						newRefObject.setFromclass(fromClass + "#" + c1); 
						newRefObject.setToclass(refObject.get(j).getToClass());
						newRefObject.setPackageNameFrom(packageFrom);
						newRefObject.setPackageNameTo(packageTo);
						for (int k = 0; k < dependentClasses.size(); k++)
							newRefObject.setClassesCoCD(dependentClasses.get(k));
						for (int k = 0; k < dependentClassesToFrom.size(); k++)
							newRefObject.setClassesCoCDFrom(dependentClassesToFrom.get(k));
						newRefObject.setIntensityOld(refObject.get(j).getIntensityOld());
						newRefObject.setIntensityNew(refObject.get(j).getIntensityOld() - resolvedCounter + dependentClassesToFrom.size());
						newRefObject.setCoCD(dependentClasses.size());
						newRefObject.setCoCDFrom(dependentClassesToFrom.size());
						newRefObject.setCoRD(resolvedCounter);
						newRefObject.setMcpmOld(refObject.get(j).getMpcmOld());
						newRefObject.setRem(refObject.get(j).getRem());

						for (int in = 0; in < tempPackageAxis.size(); in++)
						{
							if (tempPackageAxis.get(in).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(in).getToPackage().equals(packageTo))
							{
								tempPackageAxis.get(in).updatePropagation();
								newRefObject.setMcpmNew(tempPackageAxis.get(in).getProbability());
								break;
							}
						}

						// Restore the object to start state
						for (int in = 0; in < tempPackageAxis.size(); in++)
						{
							if (tempPackageAxis.get(in).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(in).getToPackage().equals(packageTo))
							{
								for (int p = 0; p < axisToBeDeleted.size(); p++)
								{
									tempPackageAxis.get(in).addAxis(axisToBeDeleted.get(p));
								}
								tempPackageAxis.get(in).updatePropagation();
							}
						}

						for (int count = 0; count < tempPackageAxis.size(); count++)
						{
							if (tempPackageAxis.get(count).getFromPackage().equals(packageFrom) &&
									tempPackageAxis.get(count).getToPackage().equals(packageTo))
							{	
								tempPackageAxis.get(count).DeleteAxis(axisToBeAdded);
								axisToBeAdded.clear();
								break;
							}
						}

						// Step 6 : Push new Refactoring Object to check for extra classes
						newRefactoring.add(newRefObject);

						dependentClasses.clear();
						dependentClassesToFrom.clear();
						classesToBeMoved.remove(c1);
						if (f == 1)
							classesToBeMoved.remove(fromClass);
					} // End of while there are CoCD classes
				}  // End of if there are CoCD classes


				j++;
				if (j ==  refObject.size())
					break;	
			}

			i = j-1;
		}

		//%%%%%%%%%%%%%%%%%%%%%%%% DELETE DUPLICATES %%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

		ArrayList <String> arrayL = new ArrayList<String>();
		ArrayList<Integer> indecies = new ArrayList<Integer>();
		for (int i = 0; i < newRefactoring.size(); i++) 
		{
			String fc = newRefactoring.get(i).getFromClass();
			String tc = newRefactoring.get(i).getToClass();
			arrayL.clear();
			if (fc.contains("#"))
			{					
				String array[] = fc.split("#");
				for (String x : array)
				{
					arrayL.add(x);
				}
				for (int j = i+1; j < newRefactoring.size(); j++)
				{
					String newfc = newRefactoring.get(j).getFromClass();
					String newtc = newRefactoring.get(j).getToClass();
					if (newfc.contains("#"))
					{
						String ar[] = newfc.split("#");
						int tem = 0;
						for (int k =0; k < f+1; k++)
						{
							for (int l =0; l<f+1; l++)
							{
								if (arrayL.get(k).equals(ar[l]) && newtc.equals(tc))
								{
									tem++;
									break;
								}
							}
						}

						if (tem == f+1)
						{
							if (!indecies.contains(j))
								indecies.add(j);
						}

					}
				}

			}
		}

		int p = 0;
		Collections.sort(indecies);
		while (p < indecies.size() )
		{
			newRefactoring.remove(indecies.get(p)-p);
			p++;
		}

		//%%%%%%%%%%%%%%%%%%%%%%%% WRITE IN FILE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
		int num = f + 1; 
		writeFile("refactoring_level"+ num +".csv",newRefactoring);

		writeFile("reverseRefactoringLevel" + num +".csv",refactoringObjectsReverse);
		//#############################################################################################
		f++;
		if ( f < numOfSets) 
		{
			System.out.println("end");
			moveClassSets(numOfSets, newRefactoring, packAxisObject, system, initiallyRefObject);
			System.out.println("end again");
		}

	}

	//metrics : intensity, created dependencies, removed dependencies, mcpm, rem
	public Map<String, List<Refactoring>> calculateMetrics (ArrayList<PackageAxis> packAxis, SystemAxisObject system)
	{
		System.out.println();
		System.out.println("-------- NEW FUNCTION OF CALCULATE METRICS --------");		
		//system --> SystemAxisObject
		String packageName = null;
		String classFrom = null;
		String classTo = null;
		double rem = 0.0;
		double mcpm = 0.0;
		int counterOfUnresolvedDepNoNewDepA_B = 0;
		int counterOfUnresolvedDepNoNewDepB_A = 0;
		int resolvedDependencies = 0;
		int newDependencies = 0;
		int newDependenciesFrom = 0;		

		int curr = 0;
		ArrayList<String> dependencies = new ArrayList<String>();
		ArrayList<String> dependenciesToFrom = new ArrayList<String>();
		ArrayList<String> resDependencies = new ArrayList<String>();
		ArrayList<String> intensityFromClass = new ArrayList<String>();
		ArrayList<String> intensityToClass = new ArrayList<String>();
		ArrayList<Refactoring> refactoringObjects = new ArrayList<Refactoring>();
		ArrayList<Refactoring> refactoringB_A = new ArrayList<Refactoring>();
		ArrayList<Refactoring> refactoringObjectsReverse = new ArrayList<Refactoring>();
		ArrayList<PackageAxis> packageAxisRefactoring = new ArrayList<PackageAxis>();

		// FROM PACKAGE A TO ALL
		for(int key = 0; key < packAxis.size(); key ++)
		{
			//packageAxis --> PackageAxis
			PackageAxis packageAxis = packAxis.get(key);
			packageName = packageAxis.getFromPackage();
			ListIterator axisIt = packageAxis.getAxisListIterator();
			mcpm = packageAxis.getProbability();
			resolvedDependencies = 0;
			System.out.println("-----------------New-------------------");
			System.out.println();
			System.out.println("Package name TO " + packageAxis.getToPackage() + " Package name FROM: " + packageName);
			while (axisIt.hasNext())
			{					
				// axis --> Axis
				Axis axis = (Axis)axisIt.next();
				classFrom = axis.getFromClass();  
				classTo = axis.getToClass();
				rem = axis.getProbability();		

				System.out.println("*******************************************************");
				System.out.println("Package : " + packageName + " From class: " + classFrom + " To class: " + classTo + "\n" + 
						" Rem: " + rem + " Mcpm: " + mcpm );
				System.out.println();

				Refactoring r = new Refactoring();
				r.setFromclass(classFrom);
				r.setToclass(classTo);
				r.setPackageNameFrom(packageName);
				r.setMcpmOld(mcpm);
				r.setRem(rem); 
				r.setPackageNameTo(packageAxis.getToPackage());
				r.setFlag(0);
				refactoringObjects.add(r);	

				// Find Basic class
				String innerClassTo = axis.getToClass();

				if (innerClassTo.contains("$")) 
				{
					int pos = innerClassTo.lastIndexOf("$");
					innerClassTo = innerClassTo.substring(0,pos);
				}

				String innerClassFrom = axis.getFromClass();

				if (innerClassFrom.contains("$")) 
				{
					int pos = innerClassFrom.lastIndexOf("$");
					innerClassFrom = innerClassFrom.substring(0,pos);
				}

				// Add basic class
				int flag0 = 0;
				for (int c = 0; c < intensityFromClass.size(); c++) 
				{
					// If the classes are stored in intensity arrayLists
					if (intensityFromClass.get(c).equals(innerClassFrom) && intensityToClass.get(c).equals(innerClassTo)) 
					{
						flag0 = 1;
					}
				}

				if (flag0 == 0)
				{
					intensityFromClass.add(innerClassFrom);
					intensityToClass.add(innerClassTo);
				}
				// Reverse check B -> A, find there isn't that connection
				refactoringObjectsReverse.addAll(reverseCheckOneClass (packAxis, system, packageName, packageAxis.getToPackage(), innerClassTo));
			} 

			// Set the old and new intensity in reverse check
			for (int a =0; a < refactoringObjectsReverse.size(); a++)
			{
				if (refactoringObjectsReverse.get(a).getIntensityOld() < 0)
				{
					refactoringObjectsReverse.get(a).setIntensityOld(intensityFromClass.size());
					int newIntensity = refactoringObjectsReverse.get(a).getIntensityOld() + refactoringObjectsReverse.get(a).getCoCD();				
					refactoringObjectsReverse.get(a).setIntensityNew(newIntensity);
				}
			}

			// Unique values of from class
			ArrayList<String> uniqueClasses =  new ArrayList<String>() ;
			ArrayList<String> uniqueClassesTo =  new ArrayList<String>() ;

			for (int i = curr; i < refactoringObjects.size(); i++) 
			{
				String o = refactoringObjects.get(i).getFromClass();					
				if (uniqueClasses.contains(o) == false && !o.contains("$"))
				{
					uniqueClasses.add(o);
					uniqueClassesTo.add(refactoringObjects.get(i).getToClass());
				}
			}

			//%%%%%%%%%%%%%%%%%%%%%%%% RESOLVED DEPENDENCIES %%%%%%%%%%%%%%%%%%%%%%%%%%%%%

			for (int i = 0; i<uniqueClasses.size(); i++)
			{
				String From = uniqueClasses.get(i);
				String To = uniqueClassesTo.get(i);
				resolvedDependencies = 0;
				resDependencies.clear();

				for (int j = curr; j < refactoringObjects.size(); j++)
				{
					String innerClass = refactoringObjects.get(j).getFromClass();

					if (innerClass.contains("$")) 
					{
						int pos = innerClass.indexOf("$");
						innerClass = innerClass.substring(0,pos);
					}

					String innerClassTo = refactoringObjects.get(j).getToClass();

					if (innerClassTo.contains("$")) 
					{
						int pos = innerClassTo.indexOf("$");
						innerClassTo = innerClassTo.substring(0,pos);
					}

					if(From.equals(refactoringObjects.get(j).getFromClass()) || innerClass.equals(From))
					{								
						// Add basic class
						int flag0 = 0;
						for (int c = 0; c < resDependencies.size(); c++) 
						{
							if (resDependencies.get(c).equals(innerClassTo)) 
							{
								flag0 = 1;
							}
						}

						if (flag0 == 0)
						{
							resDependencies.add(innerClassTo);
							resolvedDependencies++;	
							System.out.println("Resolved Dependencies " + resolvedDependencies );
							System.out.println(innerClassTo);
						}													
					}
				}

				System.out.println("Resolved Dependencies: " + resolvedDependencies + " From Class: " +  From );

				//%%%%%%%%%%%%%%%%%%%%%%%% NEW DEPENDENCIES %%%%%%%%%%%%%%%%%%%%%%%%%%%%%

				ListIterator classIt = system.getClassListIterator();
				List list = new ArrayList();
				dependencies.clear();
				dependenciesToFrom.clear();

				PackageAxis newPackageAxisA_B = new PackageAxis(packageAxis.getFromPackage(), packageAxis.getToPackage());
				PackageAxis newPackageAxisB_A = new PackageAxis(packageAxis.getToPackage(), packageAxis.getFromPackage());
				while(classIt.hasNext()) 
				{
					ClassAxisObject ca = (ClassAxisObject)classIt.next();
					ListIterator axisIterator = ca.getAxisListIterator();

					while(axisIterator.hasNext()) 
					{
						Axis axis = (Axis)axisIterator.next();

						String innerClassTo = axis.getToClass();
						if (innerClassTo.contains("$")) 
						{
							int poss = innerClassTo.indexOf("$");
							innerClassTo = innerClassTo.substring(0,poss);
						}

						String innerClassFrom = axis.getFromClass();
						if (innerClassFrom.contains("$")) 
						{
							int poss = innerClassFrom.indexOf("$");
							innerClassFrom = innerClassFrom.substring(0,poss);
						}

						// Find From and To packages
						String fromPackage; 
						int position1 = innerClassFrom.lastIndexOf(".");
						if (position1 == -1 ) 
						{
							fromPackage = "default";
						} else 
						{
							fromPackage = innerClassFrom.substring(0,position1);
						}

						int position2 = innerClassTo.lastIndexOf(".");
						String toPackage;
						if (position2 == -1) 
						{
							toPackage = "default";
						} else 
						{
							toPackage = innerClassTo.substring(0,position2);
						}

						if (axis.getFromClass().equals(From) || innerClassFrom.equals(From))
						{
							String cl = axis.getToClass();
							int pos = cl.lastIndexOf(".");
							cl = cl.substring(0,pos);

							if (cl.equals(packageName))
							{
								if ((!From.equals(axis.getToClass()) && !innerClassTo.equals(From)) )
								{										
									// Add basic class
									int flag0 = 0;
									for (int c = 0; c < dependencies.size(); c++) 
									{
										if (dependencies.get(c).equals(innerClassTo)) 
										{
											flag0 = 1;
										}
									}

									if (flag0 == 0)
									{
										dependencies.add(innerClassTo);
										newDependencies++;
										System.out.println("New Dependencies " + newDependencies );
										System.out.println(innerClassTo);
									}

									// Refactoring new metrics
									int poss = innerClassFrom.lastIndexOf(".");
									String cf = innerClassFrom.substring(poss,innerClassFrom.length());
									//new class after refactoring
									String newclass = packageAxis.getToPackage() + cf;
									Axis newAxis = new Axis(axis.getDescription(), innerClassTo, innerClassFrom);
									newAxis.setMethodsCalled(axis.getMethodsCalled());
									newAxis.setNa(axis.getAttributes());
									newAxis.setNpm(axis.getNPM());
									newAxis.setNprfUsed(axis.getNPRF_USED());
									newAxis.setNop(axis.getNOP());
									newAxis.setRfc(axis.getRFC());
									newAxis.setProbability(axis.getProbability());
									newPackageAxisB_A.addAxis(newAxis);
									System.out.println("B -> A");
								}	
							}
						}	

						// Dependencies from other classes in package A to class From
						else if (axis.getToClass().equals(From) || innerClassTo.equals(From))
						{
							String cl = axis.getFromClass();
							int pos = cl.lastIndexOf(".");
							cl = cl.substring(0,pos);

							String cl2 = axis.getToClass();
							int pos2 = cl2.lastIndexOf(".");
							cl2 = cl2.substring(0,pos2);

							if (cl.equals(cl2))
							{
								if ((!From.equals(axis.getFromClass()) && !innerClassFrom.equals(From)) )
								{								
									// Add basic class
									int flag0 = 0;
									for (int c = 0; c < dependenciesToFrom.size(); c++) 
									{
										if (dependenciesToFrom.get(c).equals(innerClassFrom)) 
										{
											flag0 = 1;
										}
									}

									if (flag0 == 0)
									{
										dependenciesToFrom.add(innerClassFrom);
										newDependenciesFrom++;
										System.out.println("New Dependencies From :" + innerClassFrom);
										System.out.println(innerClassTo);
									}

									int poss = innerClassFrom.lastIndexOf(".");
									String cf = innerClassFrom.substring(poss,innerClassFrom.length());
									String newclass = packageAxis.getToPackage() + cf;
									Axis newAxis = new Axis(axis.getDescription(), innerClassTo, innerClassFrom);
									newAxis.setMethodsCalled(axis.getMethodsCalled());
									newAxis.setNa(axis.getAttributes());
									newAxis.setNpm(axis.getNPM());
									newAxis.setNprfUsed(axis.getNPRF_USED());
									newAxis.setNop(axis.getNOP());
									newAxis.setRfc(axis.getRFC());
									newAxis.setProbability(axis.getProbability());
									newPackageAxisA_B.addAxis(newAxis);
									System.out.println("A -> B");
								}	

							}
						} 
						// A -> B remaining
						else if (packageAxis.getFromPackage().equals(fromPackage) &&
								packageAxis.getToPackage().equals(toPackage))
						{
							if (!fromPackage.equals(toPackage))
							{
								if ((!To.equals(axis.getToClass()) && !innerClassTo.equals(To)) )
								{
									if ((!From.equals(axis.getFromClass()) && !innerClassFrom.equals(From)) )
									{
										System.out.println("The moving class is: " + From);
										System.out.println("Unresolved dependency : ");
										System.out.println(axis.getFromClass() + " - " + axis.getToClass());

										String classfrom = axis.getFromClass();
										if (classfrom.contains("$")) 
										{
											int poss = classfrom.lastIndexOf("$");
											classfrom = classfrom.substring(0,poss);
										}

										Axis newAxis = new Axis(axis.getDescription(), innerClassTo, innerClassFrom);
										newAxis.setMethodsCalled(axis.getMethodsCalled());
										newAxis.setNa(axis.getAttributes());
										newAxis.setNpm(axis.getNPM());
										newAxis.setNprfUsed(axis.getNPRF_USED());
										newAxis.setNop(axis.getNOP());
										newAxis.setRfc(axis.getRFC());
										newAxis.setProbability(axis.getProbability());
										newPackageAxisA_B.addAxis(newAxis);
										counterOfUnresolvedDepNoNewDepA_B ++;
										System.out.println("A -> B");
									}
								}

							}
						}
						// B -> A remaining
						else if (packageAxis.getFromPackage().equals(toPackage) &&
								packageAxis.getToPackage().equals(fromPackage))
						{
							if (!fromPackage.equals(toPackage))
							{
								if ((!From.equals(axis.getToClass()) && !innerClassTo.equals(From)) )
								{
									System.out.println("The moving class is: " + From);
									System.out.println("Unresolved dependency : ");
									System.out.println(axis.getFromClass() + " - " + axis.getToClass());

									String classfrom = axis.getFromClass();
									if (classfrom.contains("$")) 
									{
										int poss = classfrom.lastIndexOf("$");
										classfrom = classfrom.substring(0,poss);
									}

									Axis newAxis = new Axis(axis.getDescription(), innerClassTo, innerClassFrom);
									newAxis.setMethodsCalled(axis.getMethodsCalled());
									newAxis.setNa(axis.getAttributes());
									newAxis.setNpm(axis.getNPM());
									newAxis.setNprfUsed(axis.getNPRF_USED());
									newAxis.setNop(axis.getNOP());
									newAxis.setRfc(axis.getRFC());
									newAxis.setProbability(axis.getProbability());
									newPackageAxisB_A.addAxis(newAxis);
									counterOfUnresolvedDepNoNewDepB_A ++;
									System.out.println("B -> A");
								}
							}
						}
					}	
				}

				// Set all the metrics in refactoring object
				if (counterOfUnresolvedDepNoNewDepA_B  > 0 || newDependencies > 0) 
				{
					newPackageAxisA_B.updatePropagation();
					packageAxisRefactoring.add(newPackageAxisA_B);	
				}

				if ( newDependenciesFrom > 0 || counterOfUnresolvedDepNoNewDepB_A > 0 ) 
				{
					newPackageAxisB_A.updatePropagation();
					packageAxisRefactoring.add(newPackageAxisB_A);	
				}

				// Add refactoring from B -> A

				// Delete duplicates
				if (newPackageAxisB_A.getProbability() !=null )
				{
					ListIterator packageB_A = newPackageAxisB_A.getAxisListIterator();
					ArrayList<Axis> forDelete = new ArrayList<Axis>();
					while (packageB_A.hasNext())
					{
						Axis nAxis = (Axis) packageB_A.next();
						int temp =0;
						ListIterator pack = newPackageAxisB_A.getAxisListIterator();
						while(pack.hasNext())
						{
							Axis nAxis2 = (Axis) pack.next();
							if (nAxis.getFromClass().equals(nAxis2.getFromClass()) &&
									nAxis.getToClass().equals(nAxis2.getToClass()))
							{
								temp++;
							}

							if (temp > 1)
							{
								forDelete.add(nAxis2);
								temp = 0;
							}
						}
					}					
					newPackageAxisB_A.DeleteAxis(forDelete);
				}
				// PRINT DATA ON CONSOLE B -- > A
				if (newPackageAxisB_A.getProbability() !=null )//&& !newPackageAxisB_A.getFromPackage().equals("artofillusion"))
				{
					System.out.println("NEW PACKAGE AXIS B->A");
					ListIterator packageB_A = newPackageAxisB_A.getAxisListIterator();
					while (packageB_A.hasNext())
					{
						Refactoring r = new Refactoring();
						r.setPackageNameFrom(newPackageAxisB_A.getFromPackage());
						r.setPackageNameTo(newPackageAxisB_A.getToPackage());

						Axis nAxis = (Axis) packageB_A.next();

						r.setFromclass(nAxis.getFromClass());
						r.setToclass(nAxis.getToClass());					
						r.setRem(nAxis.getProbability()); 	
						r.setIntensityNew(newPackageAxisB_A.getAxisList().size());
						r.setMcpmNew(newPackageAxisB_A.getProbability());


						r.setFlag(0);
						refactoringB_A.add(r);			
						System.out.println("From class: " + nAxis.getFromClass() + " To class: " + nAxis.getToClass()
						+ " Package From: " + newPackageAxisB_A.getFromPackage() + " Package To: " + newPackageAxisB_A.getToPackage()
						+ " Probability : " + newPackageAxisB_A.getProbability() + "Intensity : " + newPackageAxisB_A.getAxisList().size());

					}
				}	

				// Delete duplicates Α -- > B
				if (newPackageAxisA_B.getProbability() !=null )
				{
					ListIterator packageB_A = newPackageAxisA_B.getAxisListIterator();
					ArrayList<Axis> forDelete = new ArrayList<Axis>();
					while (packageB_A.hasNext())
					{
						Axis nAxis = (Axis) packageB_A.next();
						int temp =0;
						ListIterator pack = newPackageAxisA_B.getAxisListIterator();
						while(pack.hasNext())
						{
							Axis nAxis2 = (Axis) pack.next();
							if (nAxis.getFromClass().equals(nAxis2.getFromClass()) &&
									nAxis.getToClass().equals(nAxis2.getToClass()))
							{
								temp++;
							}

							if (temp > 1)
							{
								forDelete.add(nAxis2);
								temp = 0;
							}
						}
					}					
					newPackageAxisA_B.DeleteAxis(forDelete);
				}
				// PRINT DATA ON CONSOLE A -- > B
				if (newPackageAxisA_B.getProbability() !=null) //&& !newPackageAxisA_B.getFromPackage().equals("artofillusion"))
				{
					System.out.println("NEW PACKAGE AXIS A->B");
					ListIterator packageB_A = newPackageAxisA_B.getAxisListIterator();
					while (packageB_A.hasNext())
					{
						Axis nAxis = (Axis) packageB_A.next();
						System.out.println("From class: " + nAxis.getFromClass() + " To class: " + nAxis.getToClass()
						+ " Package From: " + newPackageAxisA_B.getFromPackage() + " Package To: " + newPackageAxisA_B.getToPackage()
						+ " Probability : " + newPackageAxisA_B.getProbability() + "Intensity : " + newPackageAxisA_B.getAxisList().size());
					}
				}					

				String p = refactoringObjects.get(refactoringObjects.size()-1).getPackageNameTo();					
				for (int k = curr; k < refactoringObjects.size(); k++) 
				{
					String m = refactoringObjects.get(k).getPackageNameTo();
					if (refactoringObjects.get(k).getFromClass().equals(From) && p.equals(m))
					{						
						refactoringObjects.get(k).setCoCD(newDependenciesFrom);
						refactoringObjects.get(k).setCoRD(resolvedDependencies);
						refactoringObjects.get(k).setCoCDFrom(dependenciesToFrom.size());
						//NEA PROSTHIKI
						refactoringObjects.get(k).setIntensityOld(intensityFromClass.size());
						int newIntensity = refactoringObjects.get(k).getIntensityOld() - refactoringObjects.get(k).getCoRD() + refactoringObjects.get(k).getCoCD();				
						refactoringObjects.get(k).setIntensityNew(newIntensity);
						for (int w = 0; w<newDependencies; w++)
						{
							refactoringObjects.get(k).setClassesCoCD(dependencies.get(w));						
						}

						for (int q = 0; q<newDependenciesFrom; q++)
						{
							refactoringObjects.get(k).setClassesCoCDFrom(dependenciesToFrom.get(q));							
						}

						if (newPackageAxisA_B.getProbability() != null)
						{
							refactoringObjects.get(k).setMcpmNew(newPackageAxisA_B.getProbability());
						}
					}
				}
				//case when only inner class has dependencies
				for (int k = 0; k < refactoringObjects.size(); k++) 
				{
					if (refactoringObjects.get(k).getPackageNameFrom().equals(packageName) && 
							refactoringObjects.get(k).getIntensityOld() == 0)
					{
						refactoringObjects.get(k).setCoCD(newDependenciesFrom);
						refactoringObjects.get(k).setCoRD(resolvedDependencies);
						refactoringObjects.get(k).setCoCDFrom(dependenciesToFrom.size());
						//NEA PROSTHIKI
						refactoringObjects.get(k).setIntensityOld(intensityFromClass.size());
						int newIntensity = refactoringObjects.get(k).getIntensityOld() - refactoringObjects.get(k).getCoRD() + refactoringObjects.get(k).getCoCD();				
						refactoringObjects.get(k).setIntensityNew(newIntensity);
					}
				}
				newDependencies = 0;
				newDependenciesFrom = 0;
				counterOfUnresolvedDepNoNewDepA_B  = 0;
				counterOfUnresolvedDepNoNewDepB_A  = 0;
			}	
			curr = refactoringObjects.size();
			intensityFromClass.clear();
			intensityToClass.clear();			

			// Set metrics on the B --> A relationship

			double oldMcpm = 0.0;
			int oldIntensity = 0;
			for(int q = 0; q < packAxis.size(); q ++)
			{
				//packageAxis --> PackageAxis
				PackageAxis packageAxis1 = packAxis.get(q);

				if (packageAxis1.getFromPackage().equals(packageAxis.getToPackage()) &&
						packageAxis1.getToPackage().equals(packageAxis.getFromPackage()))
				{
					oldMcpm = packageAxis1.getProbability();
					oldIntensity = packageAxis1.getAxisList().size();
				}
			}

			for (int l = 0; l < refactoringB_A.size(); l++)
			{
				if (refactoringB_A.get(l).getPackageNameFrom().equals(packageAxis.getToPackage()) &&
						refactoringB_A.get(l).getPackageNameTo().equals(packageAxis.getFromPackage()))
				{
					refactoringB_A.get(l).setIntensityOld(oldIntensity);
					refactoringB_A.get(l).setMcpmOld(oldMcpm);
				}
			}
		} 

		writeFile("reverseCheck_B-A_level1.csv",refactoringB_A);

		// Clean inner classes
		int k= 0;
		while (k < refactoringObjects.size()) 
		{
			String cleanClass = refactoringObjects.get(k).getFromClass();
			if (cleanClass.contains("$")) 
			{
				int poss = cleanClass.indexOf("$");
				cleanClass = cleanClass.substring(0,poss);
			}

			if (refactoringObjects.get(k).getFromClass().contains("$"))
			{
				int flag0 = 0;
				for (int c = 0; c < refactoringObjects.size(); c++) 
				{
					if (refactoringObjects.get(c).getFromClass().equals(cleanClass)) 
					{
						flag0 = 1;
						break;
					}
				}

				if (flag0 == 1)
				{
					refactoringObjects.remove(k);
				}
				else
				{
					refactoringObjects.get(k).setFromclass(cleanClass);
				}

				if (k !=0 )
					k = k - 2;
				else
					k = -1;
			}
			k++;
		}
		//refactoringObjects.addAll(refactoringObjectsReverse);
		
		

		//%%%%%%%%%%%%%%%%%%%%%%%% DELETE DUPLICATES %%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/

		ArrayList<Integer> indecies = new ArrayList<Integer>();
		for (int i = 0; i < refactoringObjects.size(); i++) 
		{
			String fc = refactoringObjects.get(i).getFromClass();
			String tc = refactoringObjects.get(i).getToClass();					

			for (int j = i+1; j < refactoringObjects.size(); j++)
			{
				String newfc = refactoringObjects.get(j).getFromClass();
				String newtc = refactoringObjects.get(j).getToClass();
				
				if (fc.equals(newfc) && tc.equals(newtc))
				{
					if (!indecies.contains(j))
						indecies.add(j);
				}					
			}			
		}

		int p = 0;
		Collections.sort(indecies);
		while (p < indecies.size() )
		{
			refactoringObjects.remove(indecies.get(p)-p);
			p++;
		}

		Map<String,List<Refactoring>> map =new HashMap();
		map.put("mainList",refactoringObjects);
		map.put("reverseList",refactoringObjectsReverse);
		return map;
		//return refactoringObjects;		
	}

	public void writeFile (String fileName, ArrayList<Refactoring> refactoringObjects)
	{		
		System.out.println();
		System.out.println("-------- WRITE IN FILE --------");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Count of Removed Dependencies are ToClasses
		StringBuilder sb = new StringBuilder();
		sb.append("From class");
		sb.append(',');
		sb.append("To class");
		sb.append(',');
		sb.append("Package Name From");
		sb.append(',');
		sb.append("Package Name To");
		sb.append(',');
		sb.append("Intensity Old");
		sb.append(',');
		sb.append("Intensity New");
		sb.append(',');
		sb.append("Intensity Benefit");
		sb.append(',');
		sb.append("Created Dependencies Classes from class A");
		sb.append(',');
		sb.append("Created Dependencies Classes to class A");
		sb.append(',');
		sb.append("Count of Removed Dependencies");
		sb.append(',');
		sb.append("Count of Created Dependencies");
		sb.append(',');
		sb.append("Count of Created Dependencies to class A");
		sb.append(',');
		sb.append("MCMP Old");
		sb.append(',');
		sb.append("MCMP New");
		sb.append(',');
		sb.append("REM");
		sb.append('\n');

		for (int i = 0; i< refactoringObjects.size(); i++)
		{
			if (refactoringObjects.get(i).getIntensityOld() - refactoringObjects.get(i).getIntensityNew() >= 0)
			{
				sb.append(refactoringObjects.get(i).getFromClass());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getToClass());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getPackageNameFrom());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getPackageNameTo());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getIntensityOld());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getIntensityNew());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getIntensityOld() - refactoringObjects.get(i).getIntensityNew());
				sb.append(',');
				Iterator y = refactoringObjects.get(i).getclassesCoCD();
				while (y.hasNext())
				{	        	
					sb.append(y.next() + " # ");
				}
				sb.append(',');

				Iterator x = refactoringObjects.get(i).getclassesCoCDFrom();
				while (x.hasNext())
				{	        	
					sb.append(x.next() + " # ");
				}
				sb.append(',');
				sb.append(refactoringObjects.get(i).getCoRD());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getCoCD());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getCoCDFrom());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getMpcmOld());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getMpcmNew());
				sb.append(',');
				sb.append(refactoringObjects.get(i).getRem());
				sb.append('\n');
			}
		}
		pw.write(sb.toString());
		pw.close();

	}

	public ArrayList<Refactoring> reverseCheckOneClass (ArrayList<PackageAxis> packAxis, SystemAxisObject system, String PFrom, String PTo, String classB)
	{	
		System.out.println();
		System.out.println("-------- REVERSE CALCULATE METRICS --------");		
		//system --> SystemAxisObject
		double mcpmOld1 = 0.0;
		double mcpmOld2 = 0.0;	

		ArrayList<String> dependencies = new ArrayList<String>();
		ArrayList<Refactoring> refactoringObjects = new ArrayList<Refactoring>();

		// Check if classB from packagaB has dependencies is PackageA
		ListIterator it = system.getClassListIterator();

		System.out.println("Package From : " + PTo + " Package To: " + PFrom + " Class To  (or from): " + classB);
		PackageAxis newPackageAxis = new PackageAxis(PFrom, PTo); // A -> B
		PackageAxis newPackageAxis2 = new PackageAxis(PTo, PFrom); // B -> A 

		while(it.hasNext()) 
		{
			ClassAxisObject ca = (ClassAxisObject)it.next();	
			int flagForBreak = 0;
			mcpmOld1 = ca.getProbability(); // internal
			mcpmOld2 = ca.getEProbability(); //external

			// Package B to A
			if (ca.getPackageName().equals(PTo) && ca.getName().equals(classB))
			{
				ListIterator axisListInternal = ca.getAxisListIterator(); //internal
				ListIterator axisListExternal =  ca.getEAxisListIterator(); // external

				//external check
				while(axisListExternal.hasNext()) 
				{					
					Axis axisObjectExternal = (Axis)axisListExternal.next();
					String packageTo = axisObjectExternal.getToClass();
					int pos = packageTo.lastIndexOf(".");
					packageTo = packageTo.substring(0,pos);
					if (packageTo.equals(PFrom))
					{
						System.out.println("There are dependencies between packages, stop reverse check.");
						Refactoring v1 = new Refactoring();
						v1.setFromclass("This check will take place later");
						refactoringObjects.add(v1);
						return refactoringObjects;
					}
				}

				//internal check
				// classB to classes in Package B
				while(axisListInternal.hasNext()) 
				{	
					Axis axisObjectInternal = (Axis)axisListInternal.next();

					String packageTo = axisObjectInternal.getToClass();
					int pos = packageTo.lastIndexOf(".");
					packageTo = packageTo.substring(0,pos);
					if (packageTo.equals(PTo))
					{										
						System.out.println("There are dependencies inside package : " + PTo);
						flagForBreak = 1;
						if ( !axisObjectInternal.getToClass().equals(classB))
						{
							Axis newAxis = new Axis(axisObjectInternal.getDescription(), axisObjectInternal.getToClass(),axisObjectInternal.getFromClass());
							newAxis.setMethodsCalled(axisObjectInternal.getMethodsCalled());
							newAxis.setNa(axisObjectInternal.getAttributes());
							newAxis.setNpm(axisObjectInternal.getNPM());
							newAxis.setNprfUsed(axisObjectInternal.getNPRF_USED());
							newAxis.setNop(axisObjectInternal.getNOP());
							newAxis.setRfc(axisObjectInternal.getRFC());
							newAxis.setProbability(axisObjectInternal.getProbability());
							newPackageAxis.addAxis(newAxis);
							System.out.println("A -> B");
						}						
					}
				}
			}

			// classes in package B to classB
			else if (ca.getPackageName().equals(PTo) && !ca.getName().equals(classB))
			{
				ListIterator axisListInternal = ca.getAxisListIterator(); //internal
				//internal check
				while(axisListInternal.hasNext()) 
				{	
					Axis axisObjectInternal = (Axis)axisListInternal.next();

					String packageTo = axisObjectInternal.getToClass();
					int pos = packageTo.lastIndexOf(".");
					packageTo = packageTo.substring(0,pos);

					String inClassTo = axisObjectInternal.getToClass();
					int p = inClassTo.indexOf("$");
					if (p >=0)
						inClassTo = inClassTo.substring(0, p);

					if (packageTo.equals(PTo) && inClassTo.equals(classB))
					{										
						System.out.println("There are dependencies inside package to class B: " + axisObjectInternal.getFromClass());
						if ( !axisObjectInternal.getFromClass().equals(classB))
						{
							Axis newAxis = new Axis(axisObjectInternal.getDescription(), axisObjectInternal.getToClass(),axisObjectInternal.getFromClass());
							newAxis.setMethodsCalled(axisObjectInternal.getMethodsCalled());
							newAxis.setNa(axisObjectInternal.getAttributes());
							newAxis.setNpm(axisObjectInternal.getNPM());
							newAxis.setNprfUsed(axisObjectInternal.getNPRF_USED());
							newAxis.setNop(axisObjectInternal.getNOP());
							newAxis.setRfc(axisObjectInternal.getRFC());
							newAxis.setProbability(axisObjectInternal.getProbability());
							newPackageAxis2.addAxis(newAxis);
							System.out.println("B -> A");
						}

					}
				}
			}	
			// A -> B all other relationships
			else if (ca.getPackageName().equals(PFrom) )
			{
				ListIterator axisListExternal = ca.getEAxisListIterator(); //external
				//external check
				while(axisListExternal.hasNext()) 
				{	
					Axis axisObjectInternal = (Axis)axisListExternal.next();

					String packageTo = axisObjectInternal.getToClass();
					int pos = packageTo.lastIndexOf(".");
					packageTo = packageTo.substring(0,pos);

					String inClassTo = axisObjectInternal.getToClass();
					int p = inClassTo.indexOf("$");
					if (p>=0)
						inClassTo = inClassTo.substring(0, p);

					if (packageTo.equals(PTo) && !inClassTo.equals(classB))
					{										
						System.out.println("Other dependencies from package A to package B: " + axisObjectInternal.getFromClass());

						Axis newAxis = new Axis(axisObjectInternal.getDescription(), axisObjectInternal.getToClass(),axisObjectInternal.getFromClass());
						newAxis.setMethodsCalled(axisObjectInternal.getMethodsCalled());
						newAxis.setNa(axisObjectInternal.getAttributes());
						newAxis.setNpm(axisObjectInternal.getNPM());
						newAxis.setNprfUsed(axisObjectInternal.getNPRF_USED());
						newAxis.setNop(axisObjectInternal.getNOP());
						newAxis.setRfc(axisObjectInternal.getRFC());
						newAxis.setProbability(axisObjectInternal.getProbability());
						newPackageAxis.addAxis(newAxis);
						System.out.println("A -> B");
					}
				}
			}
			// B -> A all other relationships
			else if (ca.getPackageName().equals(PTo) )
			{
				ListIterator axisListExternal = ca.getEAxisListIterator(); //external
				//external check
				while(axisListExternal.hasNext()) 
				{	
					Axis axisObjectInternal = (Axis)axisListExternal.next();

					String packageTo = axisObjectInternal.getToClass();
					int pos = packageTo.lastIndexOf(".");
					packageTo = packageTo.substring(0,pos);

					String inClassFrom = axisObjectInternal.getFromClass();
					int p = inClassFrom.indexOf("$");
					inClassFrom = inClassFrom.substring(0, p);

					if (packageTo.equals(PFrom) && !inClassFrom.equals(classB))
					{										
						System.out.println("Other dependencies from package A to package B: " + axisObjectInternal.getFromClass());

						Axis newAxis = new Axis(axisObjectInternal.getDescription(), axisObjectInternal.getToClass(),axisObjectInternal.getFromClass());
						newAxis.setMethodsCalled(axisObjectInternal.getMethodsCalled());
						newAxis.setNa(axisObjectInternal.getAttributes());
						newAxis.setNpm(axisObjectInternal.getNPM());
						newAxis.setNprfUsed(axisObjectInternal.getNPRF_USED());
						newAxis.setNop(axisObjectInternal.getNOP());
						newAxis.setRfc(axisObjectInternal.getRFC());
						newAxis.setProbability(axisObjectInternal.getProbability());
						newPackageAxis2.addAxis(newAxis);
						System.out.println("B -> A");
					}
				}
			}

		}

		// A -- > B
		newPackageAxis.updatePropagation();
		ListIterator t = newPackageAxis.getAxisListIterator();		
		System.out.println("PACKAGEAXIS REVERSE A --> B ");
		Refactoring v1 = new Refactoring();
		v1.setFromclass("A --- > B");
		refactoringObjects.add(v1);

		while (t.hasNext())
		{
			Axis w = (Axis) t.next();
			System.out.println("From class: " + w.getFromClass() + " To class: " + w.getToClass() +
					" From package : " + PFrom + " To package : " + PTo);

			// Find Basic class
			String innerClassTo = w.getToClass();

			if (innerClassTo.contains("$")) 
			{
				int pos1 = innerClassTo.lastIndexOf("$");
				innerClassTo = innerClassTo.substring(0,pos1);
			}

			String innerClassFrom = w.getFromClass();

			if (innerClassFrom.contains("$")) 
			{
				int pos1 = innerClassFrom.lastIndexOf("$");
				innerClassFrom = innerClassFrom.substring(0,pos1);
			}

			if(!innerClassTo.equals(innerClassFrom))
			{
				Refactoring newRef = new Refactoring();
				newRef.setFromclass(innerClassFrom);
				newRef.setToclass(innerClassTo);
				newRef.setPackageNameFrom(PFrom);
				newRef.setPackageNameTo(PTo);
				newRef.setCoRD(0);
				newRef.setCoCDFrom(0);
				newRef.setFlag(1);
				newRef.setRem(w.getProbability());
				newRef.setMcpmNew(newPackageAxis.getProbability());
				newRef.setMcpmOld(mcpmOld1);
				newRef.setIntensityOld(-10);
				dependencies.add(innerClassTo);
				refactoringObjects.add(newRef);
			}
		}

		for (int i = 0; i<refactoringObjects.size(); i++)
		{
			refactoringObjects.get(i).setCoCD(dependencies.size());
			for (int j=0; j<dependencies.size(); j++)
				refactoringObjects.get(i).setClassesCoCD(dependencies.get(j));

		}

		// B --> Z
		newPackageAxis2.updatePropagation();
		Refactoring v = new Refactoring();
		v.setFromclass("B --- > A");
		refactoringObjects.add(v);
		dependencies.clear();
		int size = refactoringObjects.size();
		ListIterator t1 = newPackageAxis2.getAxisListIterator();		
		System.out.println("PACKAGEAXIS B --> A ^^^");

		while (t1.hasNext())
		{
			Axis w = (Axis) t1.next();
			System.out.println("From class: " + w.getFromClass() + " To class: " + w.getToClass() +
					" From package : " + PFrom + " To package : " + PTo);

			// Find Basic class
			String innerClassTo = w.getToClass();

			if (innerClassTo.contains("$")) 
			{
				int pos1 = innerClassTo.lastIndexOf("$");
				innerClassTo = innerClassTo.substring(0,pos1);
			}

			String innerClassFrom = w.getFromClass();

			if (innerClassFrom.contains("$")) 
			{
				int pos1 = innerClassFrom.lastIndexOf("$");
				innerClassFrom = innerClassFrom.substring(0,pos1);
			}

			if(!innerClassTo.equals(innerClassFrom))
			{
				Refactoring newRef = new Refactoring();
				newRef.setFromclass(innerClassFrom);
				newRef.setToclass(innerClassTo);
				newRef.setPackageNameFrom(PTo);
				newRef.setPackageNameTo(PFrom);
				newRef.setCoRD(0);
				newRef.setCoCD(0);
				newRef.setCoCDFrom(newPackageAxis2.getAxisList().size());
				newRef.setFlag(1);
				newRef.setRem(w.getProbability());
				newRef.setMcpmNew(newPackageAxis2.getProbability());
				newRef.setMcpmOld(mcpmOld2);
				newRef.setClassesCoCDFrom(innerClassFrom);
				newRef.setIntensityOld(0);
				newRef.setIntensityNew(newPackageAxis2.getAxisList().size());
				refactoringObjects.add(newRef);
			}
		}


		return refactoringObjects;
	}

	public ArrayList<Refactoring> reverseCheckSetOfClasses (ArrayList<PackageAxis> packAxis, SystemAxisObject system, String PFrom, String PTo, String classB)
	{
		System.out.println();
		System.out.println("-------- REVERSE CALCULATE METRICS SETS OF CLASSES--------");		
		//system --> SystemAxisObject
		double mcpmOld1 = 0.0;
		double mcpmOld2 = 0.0;
		int index = 0;
		int intensity = 0;


		int curr = 0;
		ArrayList<String> dependencies = new ArrayList<String>();
		ArrayList<Refactoring> refactoringObjects = new ArrayList<Refactoring>();
		ArrayList<PackageAxis> packageAxisRefactoring = new ArrayList<PackageAxis>();
		ArrayList<Axis> axisToBeDeleted = new ArrayList<Axis>();
		ArrayList<Axis> axisToBeAdded = new ArrayList<Axis>();

		// Check if classB from packagaB has dependencies is PackageA
		ListIterator it = system.getClassListIterator();

		System.out.println("Package From : " + PTo + " Package To: " + PFrom + " Class To  (or from): " + classB);
		PackageAxis newPackageAxis = new PackageAxis(PFrom, PTo); // A -> B
		PackageAxis newPackageAxis2 = new PackageAxis(PTo, PFrom); // B -> A dimiourgeitai

		while(it.hasNext()) 
		{
			ClassAxisObject ca = (ClassAxisObject)it.next();	
			int flagForBreak = 0;
			mcpmOld1 = ca.getProbability();
			mcpmOld2 = ca.getEProbability();

			if (ca.getPackageName().equals(PTo) && ca.getName().equals(classB))
			{
				ListIterator axisListInternal = ca.getAxisListIterator(); //internal
				ListIterator axisListExternal =  ca.getEAxisListIterator(); // external

				//external check
				while(axisListExternal.hasNext()) 
				{					
					Axis axisObjectExternal = (Axis)axisListExternal.next();
					String packageTo = axisObjectExternal.getToClass();
					int pos = packageTo.lastIndexOf(".");
					packageTo = packageTo.substring(0,pos);
					if (packageTo.equals(PFrom))
					{
						System.out.println("End this, there are dependencies between package :" + PFrom + 
								" and " + PTo);
						Refactoring v1 = new Refactoring();
						v1.setToclass("NO NEED TO CHECK NOW");
						v1.setPackageNameFrom(PTo);
						v1.setPackageNameTo(PFrom);
						v1.setFromclass(classB);
						refactoringObjects.add(v1);
						return refactoringObjects;
					}
				}
							
				//internal check
				while(axisListInternal.hasNext()) 
				{					
					Axis axisObjectInternal = (Axis)axisListInternal.next();
					
					String innerClassTo = axisObjectInternal.getToClass();
					if (innerClassTo.contains("$")) 
					{
						int poss = innerClassTo.indexOf("$");
						innerClassTo = innerClassTo.substring(0,poss);
					}

					String innerClassFrom = axisObjectInternal.getFromClass();
					if (innerClassFrom.contains("$")) 
					{
						int poss = innerClassFrom.indexOf("$");
						innerClassFrom = innerClassFrom.substring(0,poss);
					}
					
					String packageTo = axisObjectInternal.getToClass();
					int pos = packageTo.lastIndexOf(".");
					packageTo = packageTo.substring(0,pos);
					
					if (packageTo.equals(PTo) && !axisToBeAdded.contains(axisObjectInternal))
					{
						if (!innerClassTo.equals(classB)) 
						{
							System.out.println("New dependency: " + innerClassFrom + "->"  + innerClassTo );
							axisToBeAdded.add(axisObjectInternal);
							dependencies.add(innerClassTo);
						}
					}
				}
				flagForBreak = 1;
			}
			if (flagForBreak == 1)
				break;
		}
		
		for (int i = 0; i < packAxis.size(); i++)
		{		
			if (packAxis.get(i).getFromPackage().equals(PFrom))
			{
				ListIterator axisListInternal = packAxis.get(i).getAxisListIterator(); //internal
				//internal check
				while(axisListInternal.hasNext()) 
				{	
					Axis axisObjectInternal = (Axis)axisListInternal.next();

					String innerClassTo = axisObjectInternal.getToClass();
					if (innerClassTo.contains("$")) 
					{
						int poss = innerClassTo.indexOf("$");
						innerClassTo = innerClassTo.substring(0,poss);
					}

					String innerClassFrom = axisObjectInternal.getFromClass();
					if (innerClassFrom.contains("$")) 
					{
						int poss = innerClassFrom.indexOf("$");
						innerClassFrom = innerClassFrom.substring(0,poss);
					}

					String packageTo = axisObjectInternal.getToClass();
					int pos = packageTo.lastIndexOf(".");
					packageTo = packageTo.substring(0,pos);
					if (packageTo.equals(PTo) && innerClassTo.equals(classB))
					{																
						if ( !axisObjectInternal.getFromClass().equals(classB) && !axisToBeAdded.contains(axisObjectInternal))
						{
							System.out.println("DELETE: " + innerClassFrom + "->" + innerClassTo);
							axisToBeDeleted.add(axisObjectInternal);
						}

					}
				}

				if (axisToBeDeleted.size() > 0)
				{
					intensity = packAxis.get(i).getAxisList().size() - axisToBeDeleted.size() + axisToBeAdded.size();
					packAxis.get(i).DeleteAxis(axisToBeDeleted);
					for (int p = 0 ; p < axisToBeAdded.size(); p ++)
						packAxis.get(i).addAxis(axisToBeAdded.get(p));
					index  = i;
					break;
				}
			}
		}
		// A -- > B
		packAxis.get(index).updatePropagation();
		ListIterator t = packAxis.get(index).getAxisListIterator();		
		System.out.println("see the new packageAxis A --> B ^^^");
		Refactoring v1 = new Refactoring();
		v1.setFromclass("A --- > B");
		refactoringObjects.add(v1);

		while (t.hasNext())
		{
			Axis w = (Axis) t.next();
			System.out.println("From class: " + w.getFromClass() + " To class: " + w.getToClass() +
					" From package : " + PFrom + " To package : " + PTo);

			// Find Basic class
			String innerClassTo = w.getToClass();

			if (innerClassTo.contains("$")) 
			{
				int pos1 = innerClassTo.lastIndexOf("$");
				innerClassTo = innerClassTo.substring(0,pos1);
			}

			String innerClassFrom = w.getFromClass();

			if (innerClassFrom.contains("$")) 
			{
				int pos1 = innerClassFrom.lastIndexOf("$");
				innerClassFrom = innerClassFrom.substring(0,pos1);
			}

			if(!innerClassTo.equals(innerClassFrom))
			{
				Refactoring newRef = new Refactoring();
				newRef.setFromclass(innerClassFrom);
				newRef.setToclass(innerClassTo);
				newRef.setPackageNameFrom(PFrom);
				newRef.setPackageNameTo(PTo);
				newRef.setCoRD(axisToBeDeleted.size());
				newRef.setCoCD(axisToBeAdded.size());
				for (int q = 0; q < dependencies.size() ; q++)
					newRef.setClassesCoCD(dependencies.get(q));
				newRef.setFlag(1);
				newRef.setRem(w.getProbability());
				newRef.setMcpmNew(packAxis.get(index).getProbability());
				newRef.setMcpmOld(mcpmOld1);
				newRef.setIntensityNew(intensity);
				refactoringObjects.add(newRef);

			}
		}
				
		// Restore the object to start state
		packAxis.get(index).DeleteAxis(axisToBeAdded);
		for (int p = 0 ; p < axisToBeDeleted.size(); p ++)
			packAxis.get(index).addAxis(axisToBeDeleted.get(p));
		

		return refactoringObjects;
	}

}
