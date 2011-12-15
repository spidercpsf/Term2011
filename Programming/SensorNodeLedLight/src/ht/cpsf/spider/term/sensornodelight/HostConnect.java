/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term.sensornodelight;
import com.sun.spot.io.j2me.radiogram.*;
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
    String masterKey;
    public HostConnect(String addr,int port){
        try {
            // Open up a broadcast connection to the host port
            // where the 'on Desktop' portion of this demo is listening
            rCon = (RadiogramConnection) Connector.open("radiogram://broadcast:" + port);
            dg = rCon.newDatagram(50);  // only sending 12 bytes of data
        } catch (Exception e) {
            System.err.println("Caught " + e + " in connection initialization.");
            
        }
    }
    public void setMasterKey(String mk){
        this.masterKey=mk;
    }
    boolean send() throws IOException{
        rCon.send(dg);
        //encrypted data at here
        
        //
        return true;
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
}
