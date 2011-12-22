/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;



import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.peripheral.TimeoutException;
import com.sun.spot.util.IEEEAddress;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;
/**
 *
 * @author cpsf
 */
public class NewNodeCreate implements Runnable{
    DatagramConnection conCnt ;
    Datagram packet;
    Thread th;
    String connectorAddr="";
    int HOST_PORT =14;
    int HOST_PORT2 =15;
    public void start(){
        th= new Thread(this);
        th.start();
    }
    public void run() {
        RadiogramConnection rCon = null;
        Datagram dg = null;
        RadiogramConnection rConOut = null;
        Datagram dgOut = null;
        int addrA;
        long addrFull;
        long recvAddr;
        byte[] randomCode=new byte[8];
        try {
            // Open up a server-side broadcast radiogram connection
            // to listen for sensor readings being sent by different SPOTs
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + HOST_PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
            //rCon.setTimeout(1000);
            
        }catch (Exception e) {
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
                try{
                    rCon.receive(dg);
                    String addr = dg.getAddress();  // read sender's Id
                    //long time = dg.readLong();      // read time of the reading
                    recvAddr= dg.readLong();//get addr
                    if(recvAddr!=0 && recvAddr!= SunSpotHostApplication.ourAddr ){
                        System.out.println("MSG for "+ IEEEAddress.toDottedHex(recvAddr));
                        continue;
                    }
                    //encode and check
                    
                    //

                    int val = dg.readByte();         // read the sensor value
                    System.out.println("from: " + addr + "   value = " + val);
                    if(!dg.getAddress().equals("0014.4F01.0000.4BA1")){
                        System.out.println("    Check FALSE");
                        continue;
                    }
                    switch (val){
                        case (byte)14:
                            System.out.println("Hello messenger from connector");
                            if(!connectorAddr.equals(addr)){
                                connectorAddr=addr;
                                rConOut = (RadiogramConnection) Connector.open("radiogram://"+addr+":" + HOST_PORT);
                                dgOut = rConOut.newDatagram(50);  // only sending 12 bytes of data

                            }
                            //
                            System.out.println("Sendding Hellomsg to Connector..");
                            dgOut.reset();
                            dgOut.writeLong(IEEEAddress.toLong(addr));
                            dgOut.writeByte((byte)80);
                            rConOut.send(dgOut);
                            //
                            break;
                        case (byte)162:// -> tempory code to connect to node (node havent light sensor,only LED)
                            System.out.println("Code=161:to connect to node (node have only light sensor)");
                            if(!connectorAddr.equals(addr)){
                                connectorAddr=addr;
                                rConOut = (RadiogramConnection) Connector.open("radiogram://"+addr+":" + HOST_PORT);
                                dgOut = rConOut.newDatagram(50);  // only sending 12 bytes of data
                            }
                            //
                            dgOut.reset();
                            dgOut.writeLong(IEEEAddress.toLong(addr));
                            dgOut.writeByte((byte)80);
                            rConOut.send(dgOut);
                            //read data from MSG
                            addrFull=dg.readLong();//read ADDR of new Node
                            //add to list node
                            SunSpotHostApplication.nM.addNode(IEEEAddress.toDottedHex(addrFull),"");
                            break;
                        case (byte)161:// -> tempory code to connect to node (node havent light sensor,only LED)
                            System.out.println("Code=161:to connect to node (node havent light sensor,only LED)");
                            if(!connectorAddr.equals(addr)){
                                connectorAddr=addr;
                                rConOut = (RadiogramConnection) Connector.open("radiogram://"+addr+":" + HOST_PORT);
                                dgOut = rConOut.newDatagram(50);  // only sending 12 bytes of data
                            }
                            //
                            dgOut.reset();
                            dgOut.writeLong(IEEEAddress.toLong(addr));
                            dgOut.writeByte((byte)80);
                            rConOut.send(dgOut);
                            //read data from MSG
                            addrFull=dg.readLong();//read ADDR of new Node
                            //add to list node
                            SunSpotHostApplication.nM.addNode(IEEEAddress.toDottedHex(addrFull),"");
                            break;
                        case (byte)160:// -> node have LED and light sensor -> direct sent host and node info
                            if(!connectorAddr.equals(addr)){
                                connectorAddr=addr;
                                rConOut = (RadiogramConnection) Connector.open("radiogram://"+addr+":" + HOST_PORT);
                                dgOut = rConOut.newDatagram(50);  // only sending 12 bytes of data
                            }
                            //
                            dgOut.reset();
                            dgOut.writeLong(IEEEAddress.toLong(addr));
                            dgOut.writeByte((byte)80);
                            rConOut.send(dgOut);
                            //
                            addrA=dg.readInt();
                            
                            for(int i=0;i<8;i++) randomCode[i]=dg.readByte();
                            addrFull=IEEEAddress.toLong("0014.4F01.0000.0000")+addrA;
                            //add to list node
                            SunSpotHostApplication.nM.addNode(IEEEAddress.toDottedHex(addrFull),"");
                            break;
                }
                }catch(TimeoutException e){
                    System.out.println("Listener timeout");
                }
                
            } catch (Exception e) {
                System.err.println("Caught " + e +  " while reading connector msg.");
                try {
                    throw e;
                } catch (Exception ex) {
                    Logger.getLogger(NewNodeCreate.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
