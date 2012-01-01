/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
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
    JLabel statusLB;
    Date connectorTime=null;
    GUIPanel sJP;
    public GUIFrame(String name){
        this.setName(name);
        this.setSize(1000, 1000);
        sJP= new GUIPanel(900, 900);
        Container ct= this.getContentPane();
        ct.setLayout(new BorderLayout(5,5));
        statusLB = new JLabel("Not connect connector");
        //
        
        //
        ct.add(statusLB, BorderLayout.PAGE_START);
        ct.add(sJP, BorderLayout.CENTER);
        this.add(sJP);
        this.setVisible(true);
        this.show();
    }
    public void update(){
        sJP.repain();
    }
}
