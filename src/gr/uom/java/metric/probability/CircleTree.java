package gr.uom.java.metric.probability;

import java.util.ListIterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import gr.uom.java.metric.probability.xml.*;

public class CircleTree {
	private SystemAxisObject system;
	private DefaultMutableTreeNode root;
	private List circleClassList;
	private List nodesAlreadyChecked;
	//TASK 3
	private List circlePackageList;
	
	public CircleTree(SystemAxisObject system) {
		this.system = system;
		circleClassList = new ArrayList();
		circlePackageList = new ArrayList();
		nodesAlreadyChecked = new ArrayList();
		root = new DefaultMutableTreeNode();
		searchCircles();
	}
	
	public List getCircleClassList() {
		return circleClassList;
	}
	
	public List getCirclePackageList()
	{
		return circlePackageList;
	}
	
	private void searchCircles() {
        ListIterator it = system.getClassListIterator();
        while(it.hasNext()) {
        	ClassAxisObject c = (ClassAxisObject)it.next();
        	DefaultMutableTreeNode node = new DefaultMutableTreeNode(c.getName());
        	nodesAlreadyChecked.clear();
        	recurseClassNode(node,c.getName());
        	root.add(node);
        }
	}
	
	private void searchPCircles()
	{
        Iterator<PackageAxisObject> pit = system.getPackageSetIterator();
        while(pit.hasNext()) {
        	PackageAxisObject p = (PackageAxisObject)pit.next();
        	DefaultMutableTreeNode node = new DefaultMutableTreeNode(p.getName());
        	nodesAlreadyChecked.clear();
        	recurseClassNode(node,p.getName());
        	root.add(node);
        }
	}
	
	private void recurseClassNode(DefaultMutableTreeNode node, String terminalClass) {
		ClassAxisObject c = system.getClass((String)node.getUserObject());
		ListIterator it = c.getAxisListIterator();
		
		while(it.hasNext()) {
			Axis axis = (Axis)it.next();
			if(!axis.getToClass().equals(c.getName()) && !nodesAlreadyChecked.contains(axis.getToClass())) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(axis.getToClass());
				node.add(child);
				if(!axis.getToClass().equals(terminalClass)) {
					nodesAlreadyChecked.add(axis.getToClass());
					recurseClassNode(child,terminalClass);
				}
				else {
					TreeNode[] path = node.getPath();
					for(int i=0;i<path.length;i++) {
						if(!circleClassList.contains(path[i].toString())) {
							circleClassList.add(path[i].toString());
						}
					}
				}
			}
		}
	}
	
	//TASK 3
	private void recursePackageNode(DefaultMutableTreeNode node, String terminalClass) {
		PackageAxisObject p = system.getPackage((String)node.getUserObject());
		Iterator pit = p.getPackageAxisSetIterator();
		
		while(pit.hasNext()) {
			PackageAxis paxis = (PackageAxis)pit.next();
			if(!paxis.getToPackage().equals(p.getName()) && !nodesAlreadyChecked.contains(paxis.getToPackage())) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(paxis.getToPackage());
				node.add(child);
				if(!paxis.getToPackage().equals(terminalClass)) {
					nodesAlreadyChecked.add(paxis.getToPackage());
					recurseClassNode(child,terminalClass);
				}
				else {
					TreeNode[] path = node.getPath();
					for(int i=0;i<path.length;i++) {
						if(!circleClassList.contains(path[i].toString())) {
							circleClassList.add(path[i].toString());
						}
					}
				}
			}
		}
	}
	
}