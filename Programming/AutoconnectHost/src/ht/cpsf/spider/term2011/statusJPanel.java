/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;

import com.sun.spot.util.Utils;

import java.awt.Color;
import java.awt.Graphics;
import java.nio.ByteBuffer;

import javax.swing.JPanel;

/**
 *
 * @author cpsf
 */
public class statusJPanel extends JPanel{
    public statusJPanel(int W,int H){
        this.setSize(W, H);
    }
    void run(){
        while(true){
            Utils.sleep(1000);
            System.out.println("aa");
        }
    }
    
    public void paintComponent(Graphics g) {
        ByteBuffer bb= ByteBuffer.allocate(100);
        int width = getWidth();
        int height = getHeight();
        //clear
        g.fillRect(0, 0, width, height);
        //
        int size= (width>height?height:width);
        int x,y;
        double value = 0;
        g.setColor(new Color(244, 0, 0));
        g.drawOval(0, 0, size, size);
        //
        /*for(int i=0;i<SunSpotHostApplication.NM.listNode.size();i++){
            x=0;
            y=((SunSpotHostApplication.NM.listNode.get(i).rssi+60)*size)/60;
            bb.clear();
            bb.put(SunSpotHostApplication.NM.listNode.get(i).data, 0, 12);
            bb.putInt(4501);
            //value = bb.getDouble();
            value=bb.getInt(0);
            System.out.println("Draw:"+SunSpotHostApplication.NM.listNode.get(i).addr+" "+x+ " "+y+" "+value);
            g.drawString(SunSpotHostApplication.NM.listNode.get(i).addr+ "\n"+value, x, y);
        }*/

        //
        //draw node
        //
        System.out.println("aa");
    }
    public void repain(){
        repaint();
    }
    public void main(){
        while(true){
            Utils.sleep(1000);
            System.out.println("aa");
        }
    }

}
