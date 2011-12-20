/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term.sensornodeled;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.util.IEEEAddress;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 *
 * @author cpsf
 */
public class RadioListenner implements  Runnable{
    Thread runThread;
    boolean stopFlag;
    int PORT=0;
    public RadioListenner(int PORT){
        this.PORT=PORT;
    }
    public void start(){
        Thread runThread = new Thread(this);
        runThread.start();
        stopFlag=false;
    }
    public void run() {
        byte code;
        RadiogramConnection rCon = null;
        Datagram dg = null;
        RadiogramConnection rConOut = null;
        Datagram dgOut = null;
        int addrA;
        long addrFull;
        long recvAddr;
        byte[] data=new byte[8];
        try {
            // Open up a server-side broadcast radiogram connection
            // to listen for sensor readings being sent by different SPOTs
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
            //rCon.setTimeout(1000);

        } catch (Exception e) {
            System.err.println("setUp caught " + e.getMessage());
            
        }
        // Main data collection loop
        while (true) {
            try {
                rCon.receive(dg);
                String addr = dg.getAddress(); // read sender's Id
                recvAddr= dg.readLong();
                if(recvAddr!=0 && recvAddr!= SunSpotApplication.ourAddr ){
                        System.out.println("MSG for "+ IEEEAddress.toDottedHex(recvAddr));
                        continue;
                    }
                code= dg.readByte();
                System.out.println("Recv radio:"+addr+" vl="+code);
                switch(code){//code for create connect from HOST is 01
                    case (byte)171://ID for request from Connector, datagram: 8byteID of host (long), 8byteRandomCode, 8byte masterkey for matching
                        for(int i=0;i<8;i++)data[i]= dg.readByte();
                        if(EnDeCode.check(data)){
                            data=EnDeCode.decode(data);
                            SunSpotApplication.hostAddr=0;
                            for(int i=0;i<8;i++) SunSpotApplication.hostAddr=SunSpotApplication.hostAddr*256+(char)data[i];
                            System.out.println("Recv data for commucation: addrHost="+IEEEAddress.toDottedHex(SunSpotApplication.hostAddr));
                            //send OK ACK
                            System.out.println("SEND OK ACK to "+ addr);
                            SunSpotApplication.HC.dg.reset();
                            SunSpotApplication.HC.dg.writeLong(IEEEAddress.toLong(addr));
                            SunSpotApplication.HC.dg.writeByte((byte)80);//hello
                            SunSpotApplication.HC.send();
                            //
                            SunSpotApplication.sD.tmpQ.empty();
                        }
                        break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
