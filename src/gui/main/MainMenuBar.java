package gui.main;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MainMenuBar extends JMenuBar {

    private JMenu fileMenu = null;
    private JMenu editMenu = null;
    private JMenu helpMenu = null;
    private JMenuItem exitMenuItem = null;
    private JMenuItem aboutMenuItem = null;
    private JMenuItem cutMenuItem = null;
    private JMenuItem copyMenuItem = null;
    private JMenuItem pasteMenuItem = null;
    private JMenuItem saveMenuItem = null;
    
    private JFrame owner = null;
    
    public MainMenuBar(JFrame c) {
        super();
        this.owner = c;
        initialize();
    }
    
    private void initialize() {
        this.add(getFileMenu());
        this.add(getEditMenu());
        this.add(getMyHelpMenu());
    }
    
    /**
     * This method initializes jMenu    
     *  
     * @return javax.swing.JMenu    
     */
    private JMenu getFileMenu() {
        if (fileMenu == null) {
            fileMenu = new JMenu();
            fileMenu.setText("File");
            fileMenu.add(getSaveMenuItem());
            fileMenu.add(getExitMenuItem());
        }
        return fileMenu;
    }

    /**
     * This method initializes jMenu    
     *  
     * @return javax.swing.JMenu    
     */
    private JMenu getEditMenu() {
        if (editMenu == null) {
            editMenu = new JMenu();
            editMenu.setText("Edit");
            editMenu.add(getCutMenuItem());
            editMenu.add(getCopyMenuItem());
            editMenu.add(getPasteMenuItem());
        }
        return editMenu;
    }

    /**
     * This method initializes jMenu    
     *  
     * @return javax.swing.JMenu    
     */
    private JMenu getMyHelpMenu() {
        if (helpMenu == null) {
            helpMenu = new JMenu();
            helpMenu.setText("Help");
            helpMenu.add(getAboutMenuItem());
        }
        return helpMenu;
    }

    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getExitMenuItem() {
        if (exitMenuItem == null) {
            exitMenuItem = new JMenuItem();
            exitMenuItem.setText("Exit");
            exitMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
        }
        return exitMenuItem;
    }

    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getAboutMenuItem() {
        if (aboutMenuItem == null) {
            aboutMenuItem = new JMenuItem();
            aboutMenuItem.setText("About");
            aboutMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    new JDialog(owner, "About", true).show();
                }
            });
        }
        return aboutMenuItem;
    }

    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getCutMenuItem() {
        if (cutMenuItem == null) {
            cutMenuItem = new JMenuItem();
            cutMenuItem.setText("Cut");
            cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                    Event.CTRL_MASK, true));
        }
        return cutMenuItem;
    }

    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getCopyMenuItem() {
        if (copyMenuItem == null) {
            copyMenuItem = new JMenuItem();
            copyMenuItem.setText("Copy");
            copyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,
                    Event.CTRL_MASK, true));
        }
        return copyMenuItem;
    }

    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getPasteMenuItem() {
        if (pasteMenuItem == null) {
            pasteMenuItem = new JMenuItem();
            pasteMenuItem.setText("Paste");
            pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                    Event.CTRL_MASK, true));
        }
        return pasteMenuItem;
    }

    /**
     * This method initializes jMenuItem    
     *  
     * @return javax.swing.JMenuItem    
     */
    private JMenuItem getSaveMenuItem() {
        if (saveMenuItem == null) {
            saveMenuItem = new JMenuItem();
            saveMenuItem.setText("Save");
            saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                    Event.CTRL_MASK, true));
        }
        return saveMenuItem;
    }

}
