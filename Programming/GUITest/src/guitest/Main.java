/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guitest;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cpsf
 */
public class Main {
    static nodeManager nM= new nodeManager(20,10);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        nM.addNode("1", "aaa");
        nM.addNode("2", "bbb");
        nM.addNode("3", "cc");
        GUIFrame gF= new GUIFrame("Node Info");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        nM.updateNode("2", "testata\nkfkajf");
    }

}
