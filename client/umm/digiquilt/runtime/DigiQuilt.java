/*
 * Created by ohsbw on Mar 18, 2006
 *
 * University of Minnesota, Morris
 * http://www.morris.umn.edu/
 */

package umm.digiquilt.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import javax.jmdns.JmDNS;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import umm.digiquilt.savehandler.SaveHandler;
import umm.digiquilt.server.MdnsNetworkUtil;
import umm.digiquilt.server.QuiltZeroconf;
import umm.digiquilt.view.DigiQuiltFrame;
import umm.digiquilt.view.login.QuiltLogin;

/**
 * Contains main method to start DigiQuilt.
 * 
 * @author Main.BrianOhs and Main.AndyMitchell, last changed by $Author: biatekjt $
 *         on $Date: 2009-07-29 20:50:26 $
 * @version $Revision: 1.6 $
 */

public class DigiQuilt {

    /**
     * @param args
     */
    public static void main(String[] args) {
        configureMacLookAndFeel();

        
        JmDNS jmdns;
        QuiltZeroconf qz;
        try {
            jmdns = MdnsNetworkUtil.createJmDNS();
            qz = new QuiltZeroconf(jmdns);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        QuiltLogin login = new QuiltLogin(qz, new File("quilts")); //Pauses until login has been dismissed
        qz.addServiceListener(login);
        login.setVisible(true);
        
        SaveHandler handler = login.getSaveHandler();
        if (handler == null){
            System.exit(0);
        }
        try {
            handler.synchronize();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DigiQuiltFrame frame = new DigiQuiltFrame(handler);
        frame.setVisible(true);
    }
    
    private static void configureMacLookAndFeel(){
        if (!System.getProperty("os.name").startsWith("Mac")){
            return;
        }
        
        // We want to use the regular Mac menu bar, but we don't want
        // to use Aqua look and feel. Luckily, there's a hack for that.
        try {
            // This tricky business comes from:
            // http://www.pushing-pixels.org/?p=366
            
            // First we need to make sure we're using Apple's LAF:
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
            // We grab just these menubar bits:
            Object menuBarUI = UIManager.get("MenuBarUI");
            Object menuUI = UIManager.get("MenuUI");
            Object menuItemUI = UIManager.get("MenuItemUI");
            Object checkBoxMenuItemUI = UIManager.get("CheckBoxMenuItemUI");
            Object radioButtonMenuItemUI = UIManager.get("RadioButtonMenuItemUI");
            Object popupMenuUI = UIManager.get("PopupMenuUI");
            
            // Now we set the one we really want
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
            
            // And put back the menubar bits.
            UIManager.put("MenuBarUI", menuBarUI);
            UIManager.put("MenuUI", menuUI);
            UIManager.put("MenuItemUI", menuItemUI);
            UIManager.put("CheckBoxMenuItemUI", checkBoxMenuItemUI);
            UIManager.put("RadioButtonMenuItemUI", radioButtonMenuItemUI);
            UIManager.put("PopupMenuUI", popupMenuUI);
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        
        
    }

}
