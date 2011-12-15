/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;

/**
 *
 * @author cpsf
 */
public class smoothSignal {
    double weight[]= new double[]{0.1174,0.1975,0.2349,0.1975,0.1174};
    int temp[]=new int[]{0,0,0,0,0};
    int nowID=4;
    double getData(int nowSgn){
        int i;
       double kq = 0;
       nowID=(nowID+1)%5;
       temp[nowID]= nowSgn;
       //System.out.print(nowSgn+" "+nowID+"(");
       if(temp[(nowID+3)%5]<7) return temp[(nowID+3)%5];
       for(i=0;i<5;i++){
           kq+=temp[(nowID-i+5)%5]*weight[4-i];
           //System.out.print(temp[(nowID-i+5)%5]+"*"+weight[4-i]+",");
       }
       //System.out.print(")");
       return kq;
    }
}
