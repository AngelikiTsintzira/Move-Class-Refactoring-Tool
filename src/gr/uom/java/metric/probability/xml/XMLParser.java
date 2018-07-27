package gr.uom.java.metric.probability.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class XMLParser {
	
	public SystemAxisObject parseSystemXML(File xmlFile) throws Exception {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(xmlFile);
		
		// root element --> <system>
		Element root = doc.getRootElement();
		
		// <system> element --> <class> elements
		List classList = root.getChildren("class");
		
		// <system> element --> <name> attribute
		// create System object
		SystemAxisObject system = new SystemAxisObject();
		
		system.setName(root.getChildText("name"));
		
		ListIterator cit = classList.listIterator();
		while(cit.hasNext()) {
			Element aClass = (Element)cit.next();
			
			// <class> element --> <axis> elements
			List axisList = aClass.getChildren("axis");
			
			// <class> element --> <name> attribute
			// create Class object
			ClassAxisObject c = new ClassAxisObject(aClass.getChildText("name"));
			
			ListIterator ait = axisList.listIterator();
			while(ait.hasNext()) {
				Element anAxis = (Element)ait.next();
				
				// add Axis object to Class object
				c.addAxis(new Axis(anAxis.getChildText("description"),anAxis.getChildText("to"), c.getName()),null);
			}
			
			// add Class object to System object
			system.addClass(c);
		}
		
		return system;
	}
}