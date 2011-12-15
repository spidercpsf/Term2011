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
import com.sun.spot.util.Utils;
import java.io.IOException;
import javax.microedition.io.*;
/**
 *
 * @author cpsf
 */
public class HostConnect{
    RadiogramConnection rCon = null;
    Datagram dg = null;
    RadiogramConnection rConIn = null;
    Datagram dgIn = null;
    int port;
    int port2;
    String addr;
    public HostConnect(int port){
        this.port=port;
        this.port2=port+1;
        try {
            // Open up a broadcast connection to the host port
            // where the 'on Desktop' portion of this demo is listening
            rCon = (RadiogramConnection) Connector.open("radiogram://broadcast:" + port);
            dg = rCon.newDatagram(50);  // only sending 12 bytes of data
            
            rConIn = (RadiogramConnection) Connector.open("radiogram://:" + port);
            dgIn = rConIn.newDatagram(rConIn.getMaximumLength());
            rConIn.setTimeout(1000);
        } catch (Exception e) {
            System.err.println("Caught " + e + " in connection initialization.");
            
        }
    }
    String send() throws IOException {
        while(true){
            rCon.send(dg);
            try{
                rConIn.receive(dgIn);
                //String addr = dg.getAddress();  // read sender's Id
                //long time = dg.readLong();      // read time of the reading
                int val = dgIn.readByte();         // read the sensor value
                if(val==80) {
                    System.out.println("Radio:OK ACK");
                    return dgIn.getAddress();
                }
            }catch(TimeoutException e){
                
            }
        }
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
