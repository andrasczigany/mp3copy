package gui.selection.model;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

public class SelectionNode extends DefaultMutableTreeNode {

    private File file = null;
    private String name = "";
    
    public SelectionNode(File f) {
        this.file = f;
        if (f != null && f.getName() != null)
            this.name = f.getName();
    }
    
    public File getFile() {
        return this.file;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String n) {
        this.name = n;
    }
    
    public String toString() {
        return this.name;
    }
    
    public boolean isLeaf() {
        if (file == null)
            return false;
        return file.isFile();
    }
}
