/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guitest;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Date;

/**
 *x
 * @author cpsf
 */
public class nodeOb {
    String ID;
    String info;
    int x,y;
    int weight,height;
    Date lastUpdate;
    public nodeOb(String ID,String info,int x,int y,int weight,int height){
        this.ID=ID;
        this.info=info;
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
    public void draw(Graphics h,int W,int H){
        System.out.println("Draw "+ID);
        h.setColor(Color.BLACK);
        h.drawRect((int) (x * W / 100.0), (int) (y * H / 100.0),(int)(weight*W/100.0),(int)(height*H/100.0));
        h.drawString(ID+":", (int) (x * W / 100.0)+10, (int) (y * H / 100.0)+10);
        h.drawString(info, (int) (x * W / 100.0)+10, (int) (y * H / 100.0)+30);
        h.drawString(lastUpdate.toString(), (int) (x * W / 100.0)+10, (int) (y * H / 100.0)+50);
    }
}
