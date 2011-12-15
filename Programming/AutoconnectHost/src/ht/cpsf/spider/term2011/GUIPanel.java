/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author cpsf
 */
public class GUIPanel extends JPanel{
    public GUIPanel(int W,int H){
        this.setSize(W, H);
    }
    void run(){
        System.out.println("b");
    }
    public void paintComponent(Graphics g) {
        //clear screen
        int width = getWidth();
        int height = getHeight();
        //clear
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        //
        //
        ArrayList<nodeOb> listNode= SunSpotHostApplication.nM.getList();
        System.out.println("List have "+listNode.size()+" node");
        for(int i=0;i<listNode.size();i++){
            listNode.get(i).draw(g,getWidth(),getHeight());
        }
        //repaint
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(GUIPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        repaint();
        System.out.println("aa");
    }
    public void repain(){
        repaint();
    }
}

