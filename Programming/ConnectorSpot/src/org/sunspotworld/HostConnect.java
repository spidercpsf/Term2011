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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
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
    DataOutputStream dos=null;
    ByteArrayOutputStream baos=null;
    byte code[];
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
    void initSend(long addr,int size,byte[] code) throws IOException{
        this.code=code;
        dg.reset();
        dg.writeLong(addr);
        if(dos!=null){
            baos.close();
            dos.close();
        }
        baos= new ByteArrayOutputStream(size);
        dos=new DataOutputStream(baos);
    }
    void send2() throws IOException{
        //encode
        byte[] data=baos.toByteArray();
        data=SunSpotApplication.edc.EnCode(code, data);
        dg.writeInt(data.length);
        System.out.println("Send msg len="+data.length);
        dg.write(data);
        //
        rCon.send(dg);
        //
    }
    long send() throws IOException {
        //encode
        byte[] data=baos.toByteArray();
        data=SunSpotApplication.edc.EnCode(code, data);
        dg.writeInt(data.length);
        System.out.println("Send msg len="+data.length);
        dg.write(data);
        //
        okACK=false;
        while(!okACK){
            System.out.println("Send radio data");
            rCon.send(dg);
            Utils.sleep(100);
        }
        return sender;
    }
    void sendOK(long addr,byte[] code) throws IOException{
        System.out.println("SEND OK ACK to " + addr);
        initSend(addr, 1,code);
        dos.writeByte((byte) 80); //hello
        send2();
    }
    public void start(){
        
    }
    
}
