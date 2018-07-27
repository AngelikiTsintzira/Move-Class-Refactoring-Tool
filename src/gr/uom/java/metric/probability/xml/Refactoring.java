package gr.uom.java.metric.probability.xml;

import java.awt.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Refactoring {

	private String fromClass;
	private String toClass;
	private String packageNameFrom;
	private String packageNameTo;
	private ArrayList<String> classesCoCD = new ArrayList<String>(); //apo tin from
	private ArrayList<String> classesCoCDFrom = new ArrayList<String>(); //pros tin from
	private int intensityOld;
	private int intensityNew;
	private int CoRD; 			// Count of Removed Dependencies
	private int CoCD;			// Count of Created Dependencies
	private int CoCDFrom;       // Count of Created Dependencies To From
	private double mcpmOld;
	private double mcpmNew;
	private double rem;
	private int flag ;

	public Refactoring() {}
	
	public void setFlag(int set)
	{
		this.flag = set;
	}
	
	public int getFlag ()
	{
		return this.flag;
	}

	public Iterator<String> getclassesCoCD()
	{
		return  this.classesCoCD.iterator();
	}
	
	public ArrayList<String> getListCoCDClasses()
	{
		return this.classesCoCD;
	}
	
	public Iterator<String> getclassesCoCDFrom()
	{
		return  this.classesCoCDFrom.iterator();
	}
	
	public ArrayList<String> getListCoCDClassesFrom()
	{
		return this.classesCoCDFrom;
	}

	public int getCoRD()
	{
		return this.CoRD;
	}

	public int getCoCD()
	{
		return this.CoCD;
	}
	
	public int getCoCDFrom()
	{
		return this.CoCDFrom;
	}

	public String getFromClass()
	{
		return this.fromClass;
	}

	public String getToClass()
	{
		return this.toClass;
	}

	public String getPackageNameFrom()
	{
		return this.packageNameFrom;
	}

	public String getPackageNameTo()
	{
		return this.packageNameTo;
	}

	public int getIntensityOld()
	{
		return this.intensityOld;
	}

	public int getIntensityNew()
	{
		return this.intensityNew;
	}

	public double getMpcmOld()
	{
		return this.mcpmOld;
	}

	public double getMpcmNew()
	{
		return this.mcpmNew;
	}

	public double getRem()
	{
		return this.rem;
	}

	public void setPackageNameFrom(String packageN)
	{
		this.packageNameFrom = packageN;
	}

	public void setPackageNameTo(String packageNTo)
	{
		this.packageNameTo = packageNTo;
	}

	public void setFromclass(String classF)
	{
		this.fromClass = classF;
	}

	public void setToclass(String classT)
	{
		this.toClass = classT;
	}

	public void setIntensityOld(int intOld)
	{
		this.intensityOld = intOld;
	}

	public void setIntensityNew(int intNew)
	{
		this.intensityNew = intNew;
	}

	public void setMcpmOld(double mcpmO)
	{
		this.mcpmOld = mcpmO;
	}

	public void setMcpmNew(double mcpmN)
	{
		this.mcpmNew = mcpmN;
	}

	public void setRem(double rem)
	{
		this.rem = rem;
	}

	public void setCoRD(int cord)
	{
		this.CoRD = cord;
	}

	public void setCoCD(int cocd)
	{
		this.CoCD = cocd;
	}
	
	public void setCoCDFrom(int cocd)
	{
		this.CoCDFrom = cocd;
	}
	
	public void setClassesCoCD(String cl)
	{
		this.classesCoCD.add(cl);
	}
	
	public void setClassesCoCDFrom(String cl)
	{
		this.classesCoCDFrom.add(cl);
	}

	public String toString()
	{
		return "Package From: " + this.packageNameFrom + " Package To: " + this.packageNameTo + " From class : " + this.fromClass + " To class: " + this.toClass 
				+ " Old intensity: " + this.intensityOld + " New intensity: " + this.intensityNew 
				+ " Mcpm Old: " + this.mcpmOld + " Mcpm New: " + this.mcpmNew + " Rem: " + this.rem + 
				" CoRD: " + this.CoRD + " CoCD: " + this.CoCD;
	}

}
