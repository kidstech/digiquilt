package umm.digiquilt.runtime;

import javax.swing.UIManager;

import umm.digiquilt.view.server.ServerGui;

/**
 * Class to start the server.
 * 
 * @author Jason Biatek, last changed by $Author: biatekjt $
 * on $Date: 2009-07-04 01:49:19 $
 * @version $Revision: 1.2 $
 *
 */
public class DigiQuiltServer {

    /**
     * @param args
     */
    public static void main(String[] args){
        try {
            UIManager.setLookAndFeel(
              UIManager.getCrossPlatformLookAndFeelClassName());
          } catch (Exception e) {
              e.printStackTrace();
          }
        ServerGui gui = new ServerGui();
        gui.setVisible(true);
    }

}
