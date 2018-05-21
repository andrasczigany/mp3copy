package gui.selection.view;

import gui.Settings;
import gui.selection.model.SelectionNode;
import gui.util.Tools;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class SelectedTree extends JTree implements Settings , DropTargetListener {

    private static SelectedTree instance = null;
    private List willExpand;
    private boolean isExpanding = false;
    DropTarget target;

    private SelectedTree() {
        super();
        this.initialize();
    }
    
    public static SelectedTree getInstance() {
        if (instance == null)
            instance = new SelectedTree();
        return instance;
    }
    
    private void initialize() {
        willExpand = Collections.synchronizedList(new ArrayList());
        this.setModel(new DefaultTreeModel(new SelectionNode(null)));
        this.setRootVisible(SELECTED_SHOW_ROOT);
        this.setShowsRootHandles(SELECTED_SHOW_HANDLES);
        this.getSelectionModel().setSelectionMode(SELECTED_SELECTION_MODE);
        this.putClientProperty("JTree.lineStyle", SELECTED_LINE_STYLE);
        
        DefaultTreeCellRenderer render = new DefaultTreeCellRenderer() {
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                        row, hasFocus);
                if (!(value instanceof SelectionNode))
                    return this;
                if (((SelectionNode)value).getFile() == null)   // root
                    return this;
                if ( ((SelectionNode)value).getFile().isDirectory() )
                    setIcon(expanded ? super.getDefaultOpenIcon() : super.getDefaultClosedIcon());
                else
                    setIcon(Tools.isFileMatching(((SelectionNode)value).getName(), FILTER_MUSIC)
                            ? ICON_MP3 : ICON_FILE);
                return this;
            }
        };
        this.setCellRenderer(render);
        
        this.addTreeWillExpandListener(new TreeWillExpandListener() {

            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                if (!isExpanding)
                    willExpand.remove(event.getPath());
            }

            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                if (!isExpanding)
                    willExpand.add(event.getPath());
            }
            
        });
        
//        this.setDragEnabled(true);
//        this.setUI(new MultiDragTreeUI());
        target = new DropTarget(this, this);
    }
    
    public DefaultTreeModel getTreeModel() {
        return (DefaultTreeModel)getModel();
    }
    
    public SelectionNode getRoot() {
        return (SelectionNode)getTreeModel().getRoot();
    }
    
    public List getNodes() {
      List l = new ArrayList();
      SelectionNode root = this.getRoot();
      for (int r = 0; r < getTreeModel().getChildCount(root); r++)
        l.add(this.getTreeModel().getChild(root, r));
      
      return l;
    }
    
    public List getNodeFiles() {
      List l = new ArrayList();
      SelectionNode root = this.getRoot();
      for (int r = 0; r < getTreeModel().getChildCount(root); r++)
        l.add(((SelectionNode)this.getTreeModel().getChild(root, r)).getFile());
      
      return l;
    }
    
    public int getNodeCount(SelectionNode node) {
        return getTreeModel().getChildCount(node);
    }
    
    public void insertNode(SelectionNode node, SelectionNode parent) {
        if (node.getFile().isDirectory()) {
            getRoot().add(node);
            getTreeModel().reload(getRoot());
            File[] files = node.getFile().listFiles();
            for (int f = 0; f < files.length; f++)
                insertNode(new SelectionNode(files[f]), node);
        }
        if (parent == null)
            getRoot().add(node);
        else
            parent.add(node);
        getTreeModel().nodeStructureChanged(getRoot());
    }
    
    public void removeNode(SelectionNode node) {
        ((DefaultMutableTreeNode)node.getParent()).remove(node);
        getTreeModel().nodeStructureChanged((DefaultMutableTreeNode)getTreeModel().getRoot());
    }
    
    public void moveNodes(TreePath[] nodes, int dir) {
        SelectionNode parent = (SelectionNode)((SelectionNode)nodes[0].getLastPathComponent()).getParent();
        if (dir == 0 || nodes.length == getNodeCount(parent))
            return;  // do not move when everything is selected
        SelectionNode node = null;
        int index = -1;
        for (int n = 0; n < nodes.length; n++) {
            node = (SelectionNode)nodes[n].getLastPathComponent();
            parent = (SelectionNode)node.getParent();
            index = parent.getIndex(node);
            if (index+dir < 0 || index+dir >= parent.getChildCount())
                return;     // do not move if any node is top or bottom
        }
        
        if (dir > 0) {  // moving down nodes from bottom node to top node
            for (int n = nodes.length-1; n > -1; n--) {
                node = (SelectionNode)nodes[n].getLastPathComponent();
                parent = (SelectionNode)node.getParent();
                parent.insert(node, parent.getIndex(node)+dir);
                getTreeModel().nodeStructureChanged(parent);
            }
        }
        else {
            for (int n = 0; n < nodes.length; n++) {
                node = (SelectionNode)nodes[n].getLastPathComponent();
                parent = (SelectionNode)node.getParent();
                parent.insert(node, parent.getIndex(node)+dir);
                getTreeModel().nodeStructureChanged(parent);
            }
        }
        
        setSelectionPaths(nodes);   // restore selection
    }
    
    public void reExpand() {
        isExpanding = true;
        Iterator it = willExpand.iterator();
        while(it.hasNext())
            expandPath((TreePath)it.next());
        isExpanding = false;
    }
    
    /*
     * Drop Event Handlers
     */
    private TreeNode getNodeForEvent(DropTargetDragEvent dtde) {
        Point p = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        JTree tree = (JTree)dtc.getComponent();
        TreePath path = tree.getClosestPathForLocation(p.x, p.y);
        if (path == null)
            path = new TreePath(getRoot());
        return (TreeNode)path.getLastPathComponent();
    }
    
    public void dragEnter(DropTargetDragEvent dtde) {
        TreeNode node = getNodeForEvent(dtde);
        if (node.isLeaf()) {
            dtde.rejectDrag();
        }
        else {
            dtde.acceptDrag(dtde.getDropAction());
        }
    }
    
    public void dragOver(DropTargetDragEvent dtde) {
        dtde.acceptDrag(dtde.getDropAction());
    }
    
    public void dragExit(DropTargetEvent dte) { }
    public void dropActionChanged(DropTargetDragEvent dtde) { }
    
    public void drop(DropTargetDropEvent dtde) {
        Point pt = dtde.getLocation();
        DropTargetContext dtc = dtde.getDropTargetContext();
        SelectedTree tree = (SelectedTree)dtc.getComponent();
        TreePath[] selected = tree.getSelectionPaths();
        TreePath parentpath = null;
        parentpath = tree.getPathForLocation(pt.x, pt.y);
        if (parentpath == null)
            parentpath = new TreePath(getRoot());
        SelectionNode parent = 
            (SelectionNode)parentpath.getLastPathComponent();
        if (parent.isLeaf()) {
            parent = (SelectionNode)parent.getParent();
        }
        
        try {
            Transferable tr = dtde.getTransferable();
            DataFlavor[] flavors = tr.getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (tr.isDataFlavorSupported(flavors[i])) {
                    dtde.acceptDrop(dtde.getDropAction());
//                    ArrayList l = (ArrayList)tr.getTransferData(flavors[i]);
//                    File[] files = new File[l.size()];
//                    l.toArray(files);
                    File[] files = (File[])tr.getTransferData(flavors[i]);
                    File f;
                    for (int k = 0; k < files.length; k++) {
                        f = files[k];
                        tree.insertNode(new SelectionNode(f), parent);
                        tree.reExpand();
                    }
                    tree.setSelectionPaths(selected);
                    dtde.dropComplete(true);
                    return;
                }
            }
            dtde.rejectDrop();
        } catch (Exception e) {
            e.printStackTrace();
            dtde.rejectDrop();
        }
    }
}