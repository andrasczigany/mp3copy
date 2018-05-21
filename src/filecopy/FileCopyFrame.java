package filecopy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.text.NumberFormat;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;


import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

/**
 * @author Andras Czigany - SIEMENS PSE, ECT AES4
 * 
 * Description:
 *
 **/
public class FileCopyFrame extends JFrame {
    
    // search in:
    private static final String SEARCH_DIR = "D:\\stuff\\music\\";
    private static final String DEFAULT_DIR = "C:\\";
    private static final String DEFAULT_TARGET = "D:\\";
    
    // frame position:
    private static final int POS_X = 150;
    private static final int POS_Y = 150;
    
    // field parameters:
    private static final int LIST_X = 150;
    private static final int LIST_Y = 100;
    private static final int LOG_X = 100;
    private static final int LOG_Y = 5;
    private static final int SIZE_LENGTH = 20;
    private static final int SPACE_LENGTH = 10;
    private static final int DIR_LENGTH = 40;

    // button labels:
    private static final String SELECT = "Select";		// in filechooser
    private static final String ADD_FILE = "Add";
    private static final String UP = "Up";
    private static final String DOWN = "Down";
    private static final String REMOVE_FILE = "Remove";
    private static final String SELECT_DIR = "Select directory";
    private static final String DELETE = "Free up space";
    private static final String COPY = "Copy selected files";
    private static final String EXIT = "Exit";
    
    // labels:
    private static final String TITLE = "MP3 Copy Tool";
    private static final String FILE_CHOOSER_LABEL = "Select files or directories to copy";
    private static final String DIR_CHOOSER_LABEL = "Select target directory";
    private static final String SELECTION_LABEL = "File selection";
    private static final String LOGGING = "Operation";
    
    
    private static FileCopyFrame instance = null;
    
    // main panels:
    private JPanel selectionPanel = null;
    private JPanel toPanel = null;
    private JPanel buttonPanel = null;
    private JPanel logPanel = null;
    private JPanel mainPanel = null;

    // selection panel:
    private JButton addFilesButton = null;
    private JButton upButton = null;
    private JButton downButton = null;
    private JButton removeFilesButton = null;
    private JTextField sizeField = null;
    
    // to panel:
    private JButton selectDirButton = null;
    private JTextField dirField = null;
    private JTextField spaceField = null;
    
    // button panel:
    private JButton deleteButton = null;
    private JButton copyButton = null;
    private JButton exitButton = null;
    
    // log panel:
    private TextArea logArea = null;
    
    private JFileChooser fileChooser = null;
    private JFileChooser dirChooser = null;
    private JList list = null;
    private DefaultListModel listModel = null;
    private JScrollPane sp = null;
    private long totalSize = 0;
    private boolean cancelled = false;
    private boolean stopCopying = false;
    
    
    public FileCopyFrame() {
        super();
        instance = this;
        initialize();
    }
    
    public static FileCopyFrame getFileCopyFrame() {
        return instance;
    }
    
    private File getRootDir() {
        File f = new File(SEARCH_DIR);

        if (!f.exists()) {
            f = new File(DEFAULT_DIR);
            if (!f.exists())
                return null;
        }
        
        return f;
    }
    
    private JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            
            fileChooser.setDialogTitle(FILE_CHOOSER_LABEL);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(true);
            if (getRootDir() != null) fileChooser.setCurrentDirectory(getRootDir());
            
            FileCopyFilter filter = new FileCopyFilter();
            filter.addExtension("mp3");
            filter.addExtension("m3u");
            filter.addExtension("pls");
            filter.addExtension("wav");
            filter.addExtension("amr");
            filter.setDescription("Music files and playlists");
            fileChooser.setFileFilter(filter);
            
        }
        return fileChooser;
    }

    private JFileChooser getDirChooser() {
        if (dirChooser == null) {
            dirChooser = new JFileChooser();
            
            dirChooser.setDialogTitle(DIR_CHOOSER_LABEL);
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setMultiSelectionEnabled(false);
        }
        return dirChooser;
    }

    public JList getList() {
        if (list == null) {
            list = new JList(new DefaultListModel());
            list.addKeyListener(new KeyListener() {

                public void keyPressed(KeyEvent arg0) {
                    if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
                        removeFiles(getList().getSelectedValues());
                    }
                    else if (arg0.isControlDown()) {
                        if (arg0.getKeyCode() == KeyEvent.VK_DOWN)
                        	moveItems(getList().getSelectedValues(), 1);
                        else if (arg0.getKeyCode() == KeyEvent.VK_UP)
                          	moveItems(getList().getSelectedValues(), -1);
                    }
                }

                public void keyReleased(KeyEvent arg0) {
                }

                public void keyTyped(KeyEvent arg0) {
                }
                
            });
        }
        return list;
    }
    
    public boolean getCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean c) {
        this.cancelled = c;
    }
    
    public boolean getStopCopying() {
        return this.stopCopying;
    }
    
    public void setStopCopying(boolean c) {
        this.stopCopying = c;
    }
    
    private JScrollPane getScroll() {
        if (sp == null) {
            sp = new JScrollPane();
            sp.setAutoscrolls(true);
            sp.setWheelScrollingEnabled(true);
            sp.setPreferredSize(new Dimension(LIST_X,LIST_Y));
            sp.setViewportView(getList());
        }
        return sp;
    }
    
    private JButton getAddFilesButton() {
        if (addFilesButton == null) {
            addFilesButton = new JButton(ADD_FILE);
            addFilesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openFileChooser();
                }
            });
        }
        return addFilesButton;
    }

    private JButton getUpButton() {
        if (upButton == null) {
            upButton = new JButton(new ImageIcon("up.gif"));
            upButton.setBorderPainted(false);
            upButton.setFocusPainted(false);
            upButton.setContentAreaFilled(false);

            upButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    moveItems(getList().getSelectedValues(), -1);
                }
            });
        }
        return upButton;
    }

    private JButton getDownButton() {
        if (downButton == null) {
            downButton = new JButton(DOWN);
            downButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    moveItems(getList().getSelectedValues(), 1);
                }
            });
        }
        return downButton;
    }

    private JButton getRemoveFilesButton() {
        if (removeFilesButton == null) {
            removeFilesButton = new JButton(REMOVE_FILE);
            removeFilesButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                   removeFiles(getList().getSelectedValues());
                }
            });
        }
        return removeFilesButton;
    }

    private JTextField getSizeField() {
        if (sizeField == null) {
            sizeField = new JTextField();
            sizeField.setEditable(false);
            sizeField.setBackground(Color.WHITE);
            sizeField.setColumns(SIZE_LENGTH);
        }
        return sizeField;
    }

    private JButton getSelectDirButton() {
        if (selectDirButton == null) {
            selectDirButton = new JButton(SELECT_DIR);
            selectDirButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openDirectoryChooser();
                }
            });
        }
        return selectDirButton;
    }

    public JTextField getDirField() {
        if (dirField == null) {
            dirField = new JTextField();
            dirField.setText(DEFAULT_TARGET);
            dirField.setColumns(DIR_LENGTH);
        }
        return dirField;
    }

    private JTextField getSpaceField() {
        if (spaceField == null) {
            spaceField = new JTextField();
            spaceField.setEditable(false);
            spaceField.setBackground(Color.WHITE);
            spaceField.setColumns(SPACE_LENGTH);
        }
        return spaceField;
    }

    private JButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = new JButton(DELETE);
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                }
            });
        }
        return deleteButton;
    }
    
    private JButton getCopyButton() {
        if (copyButton == null) {
            copyButton = new JButton(COPY);
            copyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    copySelection();
                }
            });
        }
        return copyButton;
    }
    
    private JButton getExitButton() {
        if (exitButton == null) {
            exitButton = new JButton(EXIT);
            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    exitProgram();
                }
            });
        }
        return exitButton;
    }

    private TextArea getLogArea() {
        if (logArea == null) {
            logArea = new TextArea();
            logArea.setEditable(false);
            logArea.setBackground(Color.WHITE);
            logArea.setColumns(LOG_X);
            logArea.setRows(LOG_Y);
        }
        return logArea;
    }

    
    private JPanel getSelectedPanel() {
        if (selectionPanel == null) {
            selectionPanel = new JPanel();
            
            JPanel p1 = new JPanel();
            p1.setLayout(new GridLayout(4,1,0,10));
            p1.add(getAddFilesButton());
            p1.add(getUpButton());
            p1.add(getDownButton());
            p1.add(getRemoveFilesButton());

            JPanel p2 = new JPanel();
            p2.setLayout(new BorderLayout());
            p2.add(getSizeField(), BorderLayout.SOUTH);
            
            selectionPanel.setBorder(new TitledBorder(SELECTION_LABEL));
            selectionPanel.setLayout(new BorderLayout(20,0));
            selectionPanel.add(p1, BorderLayout.WEST);
            selectionPanel.add(getScroll(), BorderLayout.CENTER);
            selectionPanel.add(p2, BorderLayout.EAST);
        }
        return selectionPanel;
    }

    private JPanel getToPanel() {
        if (toPanel == null) {
            toPanel = new JPanel();
            
            JPanel p1 = new JPanel();
            p1.add(getDirField());    
            
            JPanel p2 = new JPanel();
            p2.add(getSpaceField());    

            toPanel.add(getSelectDirButton());
            toPanel.add(p1);
            toPanel.add(p2);
        }
        return toPanel;
    }

    private JPanel getButtonPanel() {
        if (buttonPanel == null) {
            buttonPanel = new JPanel();
            
            buttonPanel.add(getDeleteButton());
            buttonPanel.add(getCopyButton());
            buttonPanel.add(getExitButton());
        }
        return buttonPanel;
    }

    private JPanel getLogPanel() {
        if (logPanel == null) {
            logPanel = new JPanel();
            logPanel.setBorder(new TitledBorder(LOGGING));
            
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   cancelled = true;
               }
            });
            
            JButton cancelAll = new JButton("Cancel All");
            cancelAll.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   stopCopying = true;
                   cancelled = true;
               }
            });
            
            JPanel bp = new JPanel();
            bp.setLayout(new GridLayout(2,1,0,5));
            bp.add(cancel, null);
            bp.add(cancelAll, null);
            
            JPanel cp = new JPanel();
            cp.setLayout(new BorderLayout());
            cp.add(bp, BorderLayout.SOUTH);
            
            logPanel.setLayout(new BorderLayout(20,0));
            logPanel.add(getLogArea(), BorderLayout.CENTER);
            logPanel.add(cp, BorderLayout.EAST);
        }
        return logPanel;
    }

    private JPanel getMainPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            
            JPanel p = new JPanel();
            p.setLayout(new BorderLayout());
            p.add(getSelectedPanel(), BorderLayout.CENTER);
            p.add(getToPanel(), BorderLayout.SOUTH);

            JPanel mp = new JPanel();
            mp.setLayout(new BorderLayout());
            mp.add(p, BorderLayout.CENTER);
            mp.add(getButtonPanel(), BorderLayout.SOUTH);
            
            mainPanel.setLayout(new BorderLayout(0,00));
            mainPanel.add(mp, BorderLayout.CENTER);
            mainPanel.add(getLogPanel(), BorderLayout.SOUTH);
        }
        return mainPanel;
    }

    private void openFileChooser() {
        if (!(getFileChooser().showDialog(this, SELECT) == JFileChooser.CANCEL_OPTION))
        	addFiles(getFileChooser().getSelectedFiles());
    }
    
    private void addFiles(File[] selection) {
        listModel = ((DefaultListModel)getList().getModel());
        for (int i=0; i < selection.length; i++) {
            listModel.addElement(selection[i]);
            totalSize += ((File)selection[i]).length();
            if ( selection[i].isDirectory()) {
                totalSize += getSizeOfDir(selection[i].listFiles());
            }
        }
        
        getList().setModel(listModel);
        
        getSizeField().setText(NumberFormat.getInstance().format(totalSize/(1024)) + " KByte in " + getFileCountInSelection() + " file(s)");
    }
    
    private void moveItems (Object[] selection, int direction) {
        int index = 0;
        int[] selected = new int[selection.length];
        
        if (selected.length == 0) return;
        
        listModel = ((DefaultListModel)getList().getModel());
        
        switch (direction) {
	        case -1: {
	            for (int i = 0; i < selection.length; i++) {
	                index = listModel.indexOf(selection[i]);
	                
	                if (index+direction < 0) break;
	                else {
	                    listModel.removeElement(selection[i]);
	                    listModel.insertElementAt(selection[i],index+direction);
	  	                selected[i] = listModel.indexOf(selection[i]);
	                }

	            }
	            break;
	        }
	        
	        case 1: {
	            for (int i = selection.length-1; i >= 0; i--) {
	                index = listModel.indexOf(selection[i]);
	                
	                if (index+direction >= listModel.getSize()) break;
	                else {
	                    listModel.removeElement(selection[i]);
	                    listModel.insertElementAt(selection[i],index+direction);
	                }
	            }
	            break;
	        }
        }

        for (int i = 0; i < selection.length; i++) {
            selected[i] = listModel.indexOf(selection[i]);
        }
        
        getList().setModel(listModel);
        getList().setSelectedIndices(selected);
        getList().ensureIndexIsVisible(
                direction == 1 ? selected[selected.length-1] : selected[0]
        );
    }
    
    private void moveDownFiles (Object[] selection) {
        
    }
    
    public void removeFiles(Object[] selection) {
        listModel = ((DefaultListModel)getList().getModel());
        for (int i = 0; i < selection.length; i++) {
            listModel.removeElement(selection[i]);
            totalSize -= ((File)selection[i]).length();
            if ( ((File)selection[i]).isDirectory()) {
                totalSize -= getSizeOfDir(((File)selection[i]).listFiles());
            }
        }

        getList().setModel(listModel);
        
        getSizeField().setText(
                getList().getModel().getSize() != 0
                ? 
                    NumberFormat.getInstance().format(totalSize/(1024)) + " KByte in " + getFileCountInSelection() + " file(s)"
                :
                    ""
        );
    }
    
    private long getSizeOfDir(File[] files) {
        long s = 0;

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                s += getSizeOfDir(files[i].listFiles());
            else
                s += files[i].length();
        }
        
        return s;
    }
    
    private int getFileCountInSelection() {
        int n = 0;
        
        for (int i = 0; i < getList().getModel().getSize(); i++) {
            File f = (File)getList().getModel().getElementAt(i);
            if ( f.isDirectory())
                n += getFileCountInDir(f);
            else
                n++;
        }
        
        return n;
    }
    
    private int getFileCountInDir(File f) {
        int n = 0;
        File[] files = f.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                n += getFileCountInDir(files[i]);
            else
                n++;
        }
        return n;
    }
    
    private void openDirectoryChooser() {
        if (!(getDirChooser().showDialog(this, SELECT) == JFileChooser.CANCEL_OPTION))
                getDirField().setText(getDirChooser().getSelectedFile().getAbsolutePath());
    }
    
    private void copySelection() {
        clearLog();
        new FileCopyThread(this);
    }
    
    
    public void log(String s) {
        getLogArea().append(s);
    }
    
    public void clearLog() {
        getLogArea().setText("");
    }
    
    private void initialize() {
        this.setContentPane(getMainPanel());
        this.setLocation(POS_X,POS_Y);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle(TITLE);
        this.pack();
        
        try {
        	UIManager.setLookAndFeel(new WindowsLookAndFeel());
        }
        catch(Exception ie) {}
        SwingUtilities.updateComponentTreeUI(this.getContentPane());

        this.show();
    }
    
    private void exitProgram() {
        System.exit(0);
    }
    
    public static void main(String[] args) {
        new FileCopyFrame();
    }
    
//    static {
//        System.load("d:/Test.dll");
//    }
//    
//    public static native long freeSpace(char driveLetter);

}
