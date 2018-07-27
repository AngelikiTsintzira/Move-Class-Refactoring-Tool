package gr.uom.java.metric.probability.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.ListIterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLGenerator {
	
	public void generateSystemXML(SystemAxisObject system,File file) {	
		Element root = new Element("root");
		root.setName("system");
		Document doc = new Document(root);
		
		Element n = new Element("name");
		n.setText(system.getName());
		root.addContent(n);
		
		ListIterator it = system.getClassListIterator();
		while(it.hasNext()) {
			ClassAxisObject co = (ClassAxisObject)it.next();
			Element c = new Element("class");
			root.addContent(c);
			
			Element name = new Element("name");
			name.setText(co.getName());
			c.addContent(name);
			
			ListIterator iterator = co.getAxisListIterator();
			while(iterator.hasNext()) {
				Axis a = (Axis)iterator.next();
				Element axis = new Element("axis");
			
				Element description = new Element("description");
				description.setText(a.getDescription());
				axis.addContent(description);
				
				Element to = new Element("to");
				to.setText(a.getToClass());
				axis.addContent(to);
				
				Element probability = new Element("probability");
				probability.setText(a.getProbability().toString());
				axis.addContent(probability);
				
				c.addContent(axis);
			}
		}

        Format f = Format.getRawFormat();
        f.setIndent("\t");
        XMLOutputter outp = new XMLOutputter(f);

        try {
			FileOutputStream fileStream = new FileOutputStream(file);
			outp.output(doc, fileStream);
            fileStream.close();
        }
		catch(FileNotFoundException fnfe) {}
		catch(IOException ioe) {}		
	}
}