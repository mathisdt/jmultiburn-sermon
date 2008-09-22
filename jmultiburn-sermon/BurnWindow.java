import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

class BurnWindow extends javax.swing.JFrame
implements
  java.awt.event.ActionListener
{
  private Runtime runEnviron;
  private javax.swing.JScrollPane burnDispScrl;
  private javax.swing.JTextArea burnDisplay;
  private boolean userQuit;
  private SermonSelector parent = null;

  BurnWindow(String s1, String s2, String[] s3, SermonSelector parent1) {
	parent = parent1;
	setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	this.addWindowListener(
		new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
		}
	);
    Container c4;
    Box b5;
    Box b6;
    Box b7;
    Font f8;
    int i9;
    JButton j10;
    JLabel j11;
    String[] s12;
    int i13;
    userQuit = false;
    setTitle("Brennen...");
    c4 = getContentPane();
    c4.setLayout(new java.awt.BorderLayout());
    b5 = new javax.swing.Box(0);
    b6 = new javax.swing.Box(0);
    b7 = new javax.swing.Box(0);
    c4.add(b5, "North");
    c4.add(b6, "Center");
    c4.add(b7, "South");
    burnDisplay = new javax.swing.JTextArea(24, 80);
	burnDisplay.setBorder(BorderFactory.createEmptyBorder(2, 5, 15, 0));
    burnDispScrl = new javax.swing.JScrollPane(burnDisplay, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    f8 = burnDisplay.getFont();
    i9 = f8.getSize();
    burnDisplay.setFont(new java.awt.Font("Monospaced", 0, i9));
    burnDisplay.setEditable(false);
    j10 = new javax.swing.JButton("Schlie�en");
    j10.setActionCommand("quit");
    j10.addActionListener(this);
    j11 = new javax.swing.JLabel("Meldungen:");
    b5.add(j11);
    b5.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 0, 5));
    b6.add(burnDispScrl);
    b6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 5, 5));
    b7.add(javax.swing.Box.createHorizontalGlue());
    b7.add(j10);
    b7.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pack();
    setSize(getSize().width+20, getSize().height+30);
    setResizable(false);
    s12 = new String[s3.length + 3];
    s12[0] = "multiburn-sermon";
    s12[1] = s1;
    s12[2] = s2;
    i13 = 0;
    while (i13 < s3.length) {
      s12[i13 + 3] = s3[i13];
      i13++;
    }
    runMultiburn(s12);
  }

  private Process multiburnProcess;
  private void runMultiburn(String[] s1) {
    runEnviron = Runtime.getRuntime();
    try {
      multiburnProcess = runEnviron.exec(s1);
      new BurnMonitor(burnDisplay, burnDispScrl, multiburnProcess);
      setVisible(true);
    }
    catch (IOException i1) {
      javax.swing.JOptionPane.showMessageDialog(this, "Could not run multiburn\nPlease check that it is in your $PATH", "Error", 0);
      dispose();
    }
  }

  public boolean getUserQuitState() {
    return userQuit;
  }

  public void actionPerformed(java.awt.event.ActionEvent a1) {
    if (a1.getActionCommand().equals("quit")) {
      exit();
    }
  }
  
  protected void exit() {
	  Object[] options = {"Ja, wirklich!", "Nein, lieber doch nicht..."};
	  int answer = javax.swing.JOptionPane.showOptionDialog(this, "Wirklich das Brenn-Fenster schlie�en?\n\nJede CD, die gerade gebrannt wird, ist damit unbrauchbar!\n\nJetzt schlie�en?", "Beenden?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
	  if (answer != JOptionPane.CLOSED_OPTION && answer == 0) {
		  Process p2;
		  Process p3;
		  userQuit = true;
	      try {
	    	multiburnProcess.destroy();
	        p2 = runEnviron.exec("killall -9 multiburn");
	        p3 = runEnviron.exec("rm -rf " + DB.getTempDir() + ".multiburn");
	      }
	      catch (IOException i8) {
	        javax.swing.JOptionPane.showMessageDialog(this, "Could not kill multiburn\nPlease run 'killall -9 multiburn'", "Error", 0);
	      }
		  parent.closeBurnWindow();
	  }
  }
}
