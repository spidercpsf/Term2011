/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term.sensornodeledlight;

import java.util.Date;
/**
 *
 * @author cpsf
 */
public class bitListenner implements signalListener{
    boolean isStart=false;
    long timeB;
    long timeE;
    int countData;
    int countByte;
    crc8 cr8= new crc8();
    byte checkCrC;
    int maxLen=33;
    byte data[]= new byte[maxLen];

    public void newBit(char b) {

        if(b=='S'){
            //output="S";
            isStart=true;
            System.out.println("DATA:");
            System.out.print("S");
            countData=0;
            timeB= new Date().getTime();
        }
        else if(b=='T'){
            //output+='T';
            if(isStart==true){
                System.out.println("T");
                System.out.println("Send "+ countData +" Speed="+countData*1000.0/(new Date().getTime()-timeB));
                for(int i=0;i<countData/8;i++) System.out.print(data[i]+" ");
                checkCrC= (byte) cr8.compute(data, 1, countData/8-1);
                System.out.println("TRUE??"+checkCrC+ "vs" + data[0]);
                if(checkCrC== data[0] || checkCrC - data[0]==128 ||  data[0] - checkCrC==128){//OK ACK send
                    System.out.println("Send OK ACK");
                    SunSpotApplication.sD.OKACK();
                }else{
                    System.out.println("Send FALSE ACK");
                    //SunSpotApplication.sD.FALSEACK();
                }
                //System.out.println(output);
                //check

            }
            countData=0;
            isStart=false;
        }else if(b=='O'){//OK ACK
            System.out.println("OK ACK");
            SunSpotApplication.sD.tmpQ.empty();

        }else if(b=='F'){//FALSE ACK
            System.out.println("FALSE ACK");
        }
        else {
            if(isStart) {
                //System.out.print(b);
                //output+=b;
                if(countData>=0&&countData/8<maxLen){
                    if(countData%8==0){
                        data[countData/8]=0;
                    }
                    data[countData/8]=(byte) (data[countData / 8] * 2 + (b == '1' ? 1 : 0));
                    countData+=1;
                }

            }
        }
    }

}
