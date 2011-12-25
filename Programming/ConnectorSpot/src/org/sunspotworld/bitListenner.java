/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;

import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.util.IEEEAddress;
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
                try {
                    System.out.println("T");
                    System.out.println("Send " + countData + " Speed=" + countData * 1000.0 / (new Date().getTime() - timeB));
                    for (int i = 0; i < countData / 8; i++) {
                        System.out.print(data[i] + " ");
                    }
                    checkCrC = (byte) cr8.compute(data, 1, countData/8-1);
                    System.out.println("TRUE??" + checkCrC + "vs" + data[0]);
                    //check(data[1]);
                    //send hello packet to HOST
                    SunSpotApplication.HC.initSend(0, 1, SunSpotApplication.hostCode);
                    SunSpotApplication.HC.dos.writeByte((byte) 1); //send log
                    SunSpotApplication.HC.dos.writeInt(countData); //send log
                    SunSpotApplication.HC.send2();
                    //*/
                    if (checkCrC == data[0] || checkCrC - data[0] == 128 || data[0] - checkCrC == 128) {
                        //OK ACK send
                        System.out.println("Send OK ACK");
                        SunSpotApplication.sD.OKACK();
                        try {
                            doWithData(data);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        System.out.println("Send FALSE ACK");
                        //SunSpotApplication.sD.FALSEACK();
                    }
                    //System.out.println(output);
                    //check
                    //System.out.println(output);
                    //check
                    //System.out.println(output);
                    //check
                    //System.out.println(output);
                    //check
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                

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
        byte i;
        //byte[] code= new byte[8];
        switch(data[1]){
            /**
             * this case Node  have LED -> recv Node ID, random key of node
             * after that using random key to create crypt msg to send to node by node ID
             */
            case (byte) 171:
                System.out.println("Have LED node");
                for(SunSpotApplication.addrN=0,i=0;i<4;i++){
                    SunSpotApplication.addrN=SunSpotApplication.addrN*256 + (data[i+2]<0?256+data[i+2]:data[i+2]);
                }
                for(i=0;i<8;i++)SunSpotApplication.randomCode[i]=data[i+6];
                SunSpotApplication.addrN+=IEEEAddress.toLong("0014.4F01.0000.0000");
                System.out.println("Rcv from "+ IEEEAddress.toDottedHex(SunSpotApplication.addrN));
                for(i=0;i<8;i++) {
                    SunSpotApplication.leds.getLED(i).setColor(LEDColor.RED);
                    SunSpotApplication.leds.getLED(i).setOn();
                }
                {//send data to NODE (loop send ultil recv OK ACK)
                    System.out.println("Send data to node");
                    SunSpotApplication.HC.initSend(SunSpotApplication.addrN, 9,SunSpotApplication.randomCode);
                    //write data for init secure in node, encode by recved RandomCode from LED NODE
                    SunSpotApplication.HC.dos.writeByte((byte)171);//code of node
                    //data for init 
                    SunSpotApplication.HC.dos.writeLong(SunSpotApplication.hostAddr);//code of node
                    SunSpotApplication.HC.dos.write(SunSpotApplication.hostCode, 0, 8);
                    //
                    SunSpotApplication.HC.send();
                }
                {//send data to HOST
                    System.out.println("Send data to HOST");
                    SunSpotApplication.HC.initSend(SunSpotApplication.hostAddr, 18,SunSpotApplication.hostCode);
                    //
                    SunSpotApplication.HC.dos.writeByte((byte)161);
                    //write data for create connect
                    SunSpotApplication.HC.dos.writeLong(SunSpotApplication.addrN);
                    SunSpotApplication.HC.dos.write(SunSpotApplication.randomCode, 0, 8);
                    //
                    SunSpotApplication.HC.send();
                }
                for(i=0;i<8;i++) {
                    SunSpotApplication.leds.getLED(i).setOff();
                }
                break;
            case (byte) 85:
                System.out.println("Hello Packet\n");
                break;
        }
    }
}
