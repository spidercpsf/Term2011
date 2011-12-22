/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.TimeoutException;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.io.*;
/**
 *
 * @author cpsf
 */
public class HostConnect{
    RadiogramConnection rCon = null;
    public Datagram dg = null;
    RadiogramConnection rConIn = null;
    Datagram dgIn = null;
    int port;
    int port2;
    String addr;
    public boolean okACK;
    public long sender;
    public HostConnect(int port){
        this.port=port;
        this.port2=port+1;
        try {
            // Open up a broadcast connection to the host port
            // where the 'on Desktop' portion of this demo is listening
            rCon = (RadiogramConnection) Connector.open("radiogram://broadcast:" + port);
            dg = rCon.newDatagram(5000);  // only sending 12 bytes of data
        } catch (Exception e) {
            System.err.println("Caught " + e + " in connection initialization.");
            
        }
    }
    void send2() throws IOException{
        rCon.send(dg);
        
    }
    long send() throws IOException {
        okACK=false;
        while(!okACK){
            System.out.println("Send radio data");
            rCon.send(dg);
            Utils.sleep(100);
        }
        return sender;
    }
    void writeByte(byte d) throws IOException{
        dg.writeByte(d);
    }
    void writeInt(int d) throws IOException{
        dg.writeInt(d);
    }
    void reset(){
        dg.reset();
    }
    void writeByteArr(byte[] arr,int off,int len) throws IOException{
        dg.write(arr, off, len);
    }
    public void start(){
        
    }
    
}
