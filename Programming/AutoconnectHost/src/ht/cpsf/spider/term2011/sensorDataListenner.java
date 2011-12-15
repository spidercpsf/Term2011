/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;

import com.sun.spot.io.j2me.radiogram.Radiogram;
import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 *
 * @author cpsf
 */
public class sensorDataListenner implements Runnable{
    Thread th;
    int HOST_PORT =14;
    int HOST_PORT2 =15;
    public void start(){
        th= new Thread(this);
        th.start();
    }
    public void run() {
        RadiogramConnection rCon = null;
        Datagram dg = null;
        
        try {
            // Open up a server-side broadcast radiogram connection
            // to listen for sensor readings being sent by different SPOTs
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT2);
            dg = rCon.newDatagram(rCon.getMaximumLength());
            
            //rCon.setTimeout(1000);

        } catch (Exception e) {
            System.err.println("setUp caught " + e.getMessage());
            try {
                throw e;
            } catch (Exception ex) {
                Logger.getLogger(NewNodeCreate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Main data collection loop
        while (true) {
            try {
                // Read sensor sample received over the radio
                    rCon.receive(dg);
                    String addr = dg.getAddress();  // read sender's Id
                    //long time = dg.readLong();      // read time of the reading
                    //int val = dg.readInt();         // read the sensor value
                    byte[] data=new byte[dg.getLength()];
                    /*for(int i=0;i<100&&i<dg.getLength();i++) {
                        data[dg.getLength()-i-1]=dg.readByte();
                        System.out.println(i+" "+data[i]+" "+dg.getLength());
                    }*/
                    dg.readFully(data);
                    
                    //System.out.println(SunSpotHostApplication.NM.checkNodeAddr(addr)+ " DATASENSOR from: " + addr);
                    //if(SunSpotHostApplication.NM.checkNodeAddr(addr)){
                        SunSpotHostApplication.nM.updateNode(addr,data);
                    //}
                    dg.reset();
            } catch (Exception e) {
                System.err.println("Caught " + e +  " while reading sensor samples.");
                try {
                    throw e;
                } catch (Exception ex) {
                    Logger.getLogger(NewNodeCreate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
