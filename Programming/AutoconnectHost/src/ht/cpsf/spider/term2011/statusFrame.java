/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;

import javax.swing.JFrame;

/**
 *
 * @author cpsf
 */
public class statusFrame extends JFrame{
    statusJPanel sJP;
    public statusFrame(String name){
        this.setName(name);
        this.setSize(500, 500);
        sJP= new statusJPanel(500, 500);
        this.add(sJP);
        this.setVisible(true);
        this.show();
    }
    public void update(){
        sJP.repain();
    }
}
