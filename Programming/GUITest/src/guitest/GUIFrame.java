/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guitest;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author cpsf
 */
public class GUIFrame extends JFrame{
    private int numberElement=0;
    private ArrayList<JLabel> listElement=new ArrayList<JLabel>();
    GridLayout gLay;
    JPanel jP;
    GUIPanel sJP;
    public GUIFrame(String name){
        this.setName(name);
        this.setSize(1000, 1000);
        sJP= new GUIPanel(1000, 1000);
        this.add(sJP);
        this.setVisible(true);
        this.show();
    }
    public void update(){
        sJP.repain();
    }
}
