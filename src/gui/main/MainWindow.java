package gui.main;

import filecopy.FileCopy;
import gui.Settings;
import gui.selection.view.ListsPanel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class MainWindow extends JFrame implements Settings {

  private JPanel jContentPane = null;
  private JPanel mainPanel = null;
  private ListsPanel listsPanel = null;
  private JPanel buttonsPanel = null;
  private JTextField destField = null;

  /**
   * This is the default constructor
   */

  public MainWindow() {
    super();
    initialize();
  }

  /**
   * This method initializes this
   * 
   * @return void
   */
  private void initialize() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ie) {
    }
    JFrame.setDefaultLookAndFeelDecorated(true);

    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    this.setJMenuBar(new MainMenuBar(this));
    this.setSize(WINDOW_SIZE);
    this.setLocation(WINDOW_POS_X, WINDOW_POS_Y);
    this.setContentPane(getJContentPane());
    this.setTitle(TITLE);
    this.pack();
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJContentPane() {
    if (jContentPane == null) {
      jContentPane = new JPanel();
      jContentPane.setLayout(new BorderLayout());
      jContentPane.add(getMainPanel(), java.awt.BorderLayout.NORTH); // Generated
    }
    return jContentPane;
  }

  /**
   * This method initializes mainPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getMainPanel() {
    if (mainPanel == null) {
      mainPanel = new JPanel();
      mainPanel.setLayout(new BorderLayout());
      mainPanel.add(getListsPanel(), java.awt.BorderLayout.CENTER); // Generated
      mainPanel.add(getButtonsPanel(), java.awt.BorderLayout.SOUTH); // Generated
    }
    return mainPanel;
  }

  /**
   * This method initializes listsPanel 
   *  
   * @return javax.swing.JPanel 
   */
  public ListsPanel getListsPanel() {
    if (listsPanel == null) {
      listsPanel = new ListsPanel();
    }
    return listsPanel;
  }

  /**
   * This method initializes buttonsPanel	
   * 	
   * @return javax.swing.JPanel	
   */
  private JPanel getButtonsPanel() {
    if (buttonsPanel == null) {
      buttonsPanel = new JPanel();
      buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 4, 5));
      JButton b = new JButton("Copy to");
      b.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
          boolean ok = true;
          List files = getListsPanel().getToTree().getNodeFiles();
          for (int i = 0; i < files.size(); i++) {
            File f = (File) files.get(i);
            if (f.isDirectory()) {
              for (int j = 0; j < f.listFiles().length; j++)
                if (!copy(f.listFiles()[j].getAbsolutePath(), getDestField().getText() + "\\" + f.getName() + "\\"))
                  ok = false;
//              if (ok)
//                success.add(f);
            } else {
              if (copy(f.getAbsolutePath(), getDestField().getText()))
                System.out.println("ok.");//success.add(f);
            }

            FileCopy.getFileCopy().copyTo(f.getAbsolutePath(), getDestField().getText());
          }
        }

      });

      buttonsPanel.add(b);
      buttonsPanel.add(getDestField());
    }
    return buttonsPanel;
  }

  private boolean copy(String f, String t) {
    boolean success = true;
    //      if (frame.getStopCopying()) return false;
    if (!FileCopy.getFileCopy().copyTo(f, t))
      success = false;
    return success;
  }

  private JTextField getDestField() {
    if (destField == null) {
      destField = new JTextField("d:/tmp", 12);
    }
    return destField;
  }

}
