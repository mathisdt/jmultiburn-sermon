import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JViewport;

class BurnMonitor
implements
  Runnable
{
  javax.swing.JTextArea displayArea;
  javax.swing.JScrollPane scroller;
  Process burnProc;

  BurnMonitor(javax.swing.JTextArea j1, javax.swing.JScrollPane j2, Process p3) {
    Thread t4;
    burnProc = p3;
    scroller = j2;
    displayArea = j1;
    t4 = new Thread(this, "Std Out Monitoring Thread");
    t4.start();
  }

  public void run() {
    BufferedReader b1;
    BurnWindow b2;
    String s3;
    b1 = new java.io.BufferedReader(new java.io.InputStreamReader(burnProc.getInputStream()));
    b2 = (BurnWindow) displayArea.getTopLevelAncestor();
    try {
      while ((s3 = b1.readLine()) != null) {
        displayArea.append(s3);
        displayArea.append("\n");
        scroller.getViewport().setViewPosition(new java.awt.Point(0, displayArea.getSize().height - scroller.getSize().height));
      }
      if (((BurnWindow) displayArea.getTopLevelAncestor()).getUserQuitState() == false) {
        displayArea.append("\n\n");
        displayArea.append("----- multiburn has exited. -----");
        javax.swing.JOptionPane.showMessageDialog(displayArea, "multiburn has exited.\nPlease check the 'Messages' window\nfor error messages.", "Error", 0);
      }
    }
    catch (IOException i2) {
      System.out.println("Error: An error has ocurred while monitoring multiburn.");
    }
  }
}
