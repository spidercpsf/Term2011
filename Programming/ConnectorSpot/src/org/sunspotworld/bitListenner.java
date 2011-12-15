/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;

import java.io.IOException;
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
    //for check
    int test[][]= new int[2][2];
    //
    public bitListenner(){
        test[0][0]=0;
        test[0][1]=0;
        test[1][0]=0;
        test[1][1]=0;
    }
    void check(byte a){
        char tmp,tmpx;
        int tmp2=(a<0?256+a:a);
            for(int j=0;j<8;j++){
		tmp=(char)((tmp2<<j)%256);
                tmpx=(char)((85<<j)%256);
		tmp>>=7;
                tmpx>>=7;
                test[tmpx][tmp]++;
        }
        System.out.println(test[0][0]+":"+test[0][1]+","+test[1][1]+":"+test[1][0]);
    }
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
                //check(data[1]);
                if(checkCrC== data[0] || checkCrC - data[0]==128 ||  data[0] - checkCrC==128){//OK ACK send
                    System.out.println("Send OK ACK");
                    SunSpotApplication.sD.OKACK();
                    try {
                        doWithData(data);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    

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
    private void doWithData(byte[] data) throws IOException{
        switch(data[1]){
            case (byte)170:
                SunSpotApplication.HC.reset();
                SunSpotApplication.HC.writeByteArr(data, 1, 13);
                SunSpotApplication.HC.send();
                break;
            case (byte) 85:
                System.out.println("Hello Packet\n");
                break;
        }
    }
}
