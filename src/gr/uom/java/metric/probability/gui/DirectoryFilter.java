package gr.uom.java.metric.probability.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class DirectoryFilter extends FileFilter {
	
	public boolean accept(File f) {
        return f.isDirectory();
    }
	
	public String getDescription() {
        return "Only Directories";
    }
}