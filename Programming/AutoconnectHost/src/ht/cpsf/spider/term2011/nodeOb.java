/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;

import java.awt.Color;
import java.awt.Graphics;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 *x
 * @author cpsf
 */
public class nodeOb {
    String ID;
    String info;
    byte[] data=null;
    int x,y;
    int weight,height;
    Date lastUpdate;
    byte[] code;
    public nodeOb(String ID,byte[] code,int x,int y,int weight,int height){
        this.ID=ID;
        this.code=code;
        this.x=x;
        this.y=y;
        this.weight=weight;
        this.height=height;
        lastUpdate=new Date();
    }
    public void update(String info){
        this.info=info;
        lastUpdate=new Date();
    }
    public boolean update(byte[] data){//check if match key ->......
        this.data=data;
        lastUpdate=new Date();
        return true;
    }
    public void draw(Graphics h,int W,int H){
        //
        int light=-1;
        double temp=-1;
        if(data!=null){
            ByteBuffer bb= ByteBuffer.allocate(100);
            bb.clear();
            bb.put(data, 0, 4);
            light=bb.getInt(0);
            
            bb.clear();
            bb.put(data, 4, 8);
            temp=bb.getDouble(0);
        }
        //
        System.out.println("Draw "+ID);
        h.setColor(Color.BLACK);
        h.drawRect((int) (x * W / 100.0)+10, (int) (y * H / 100.0)+10,(int)(weight*W/100.0),(int)(height*H/100.0));
        h.drawString(ID+":", (int) (x * W / 100.0)+20, (int) (y * H / 100.0)+25);
        h.drawString("Light:"+light+ " Temp:"+temp, (int) (x * W / 100.0)+20, (int) (y * H / 100.0)+45);
        h.drawString(lastUpdate.toString(), (int) (x * W / 100.0)+20, (int) (y * H / 100.0)+65);
    }

}
