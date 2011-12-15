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
        RadiogramConnection rCon = null;
        Datagram dg = null;
        RadiogramConnection rConOut = null;
        Datagram dgOut = null;
        int addrA;
        long addrFull;
        byte[] data=new byte[24];
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
                switch(dg.readByte()){//code for create connect from HOST is 01
                    case 1://ID for request from HOST, datagram: 8byteID, 8byteRandomCode, 8byte masterkey for matching
                        for(int i=0;i<24;i++)data[i]= dg.readByte();
                        if(EnDeCode.check(data)){
                            data=EnDeCode.decode(data);
                            byte[] mk=new byte[8];
                            for(int i=0;i<8;i++) mk[i]=data[i+16];
                            long addrH=0;
                            for(int i=0;i<8;i++) addrH=addrH*256+data[i];
                            HostConnect.setMasterKey(new byte[]{});
                            HostConnect.addr= IEEEAddress.toDottedHex(addrH);
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
