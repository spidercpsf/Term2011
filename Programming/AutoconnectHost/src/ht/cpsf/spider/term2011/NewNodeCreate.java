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
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;
import javax.swing.JOptionPane;
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
        
        DataInputStream dis;
        byte[] data;
        byte[] dt;
        int addrA;
        int size;
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
                    //System.out.println(IEEEAddress.toDottedHex(recvAddr));
                    if(recvAddr!=0 && recvAddr!= SunSpotHostApplication.ourAddr ){
                        System.out.println("MSG for "+recvAddr+" => "+ IEEEAddress.toDottedHex(recvAddr));
                        if(recvAddr==1412){//msg HELLO from sensor
                            System.out.println("New node hello");
                            SunSpotHostApplication.gCNM.addAddr(addr);
                        }
                        continue;
                    }
                    size =  dg.readInt();//read length
                    data= new byte[size];//create new array for store data
                    for(int i=0;i<size;i++) data[i]=dg.readByte();//get data
                    System.out.println("from: " + addr + "   Size = " + size);
                    //encode and check
                    data=SunSpotHostApplication.edc.DeCode(IEEEAddress.toLong(addr),data);
                    if(data==null){
                        continue;
                    }
                    System.out.println("Size of pack:"+data.length);
                    dis = new DataInputStream(new ByteArrayInputStream(data));
                    //
                    int val = dis.readByte();         // read the sensor value
                    System.out.println("from: " + addr + "   value = " + val);
                    /*if(!dg.getAddress().equals("0014.4F01.0000.4BA1")){
                        System.out.println("    Check FALSE");
                        continue;
                    }*/
                    switch (val){
                        case (byte) 0:
                            System.out.println("**********************************Log:"+dis.readDouble()+" "+dis.readDouble());
                            break;
                        case (byte) 1:
                            System.out.println("**********************************Recv:"+dis.readInt()+ " Status:"+dis.readBoolean());
                            break;
                        case (byte) 2:
                            System.out.println("**********************************MAXMIN:"+dis.readDouble()+"   min="+dis.readDouble()+" # minmax"+dis.readDouble()+" ** "+dis.readDouble());
                            break;
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
                            dt = SunSpotHostApplication.edc.EnCode(SunSpotHostApplication.key,new byte[]{80});
                            dgOut.writeInt(dt.length);
                            dgOut.write(dt);
                            rConOut.send(dgOut);
                            //
                            SunSpotHostApplication.gF.connectorTime= new Date();
                            break;
                        case (byte)162:// -> tempory code to connect to node (node havent light sensor,only LED)
                            System.out.println("Code=161:to connect to node (node have only light sensor)");
                            if(!connectorAddr.equals(addr)){
                                connectorAddr=addr;
                                rConOut = (RadiogramConnection) Connector.open("radiogram://"+addr+":" + HOST_PORT);
                                dgOut = rConOut.newDatagram(50);  // only sending 12 bytes of data
                            }
                            //read data from MSG
                            addrFull=dis.readLong();//read ADDR of new Node
                            System.out.println(IEEEAddress.toDottedHex(addrFull));
                            for(int i=0;i<8;i++) {
                                System.out.println(i);
                                randomCode[i]=dis.readByte();
                            }
                            //add to list node
                            SunSpotHostApplication.nM.addNode(IEEEAddress.toDottedHex(addrFull),randomCode);
                            break;
                        case (byte)161:// -> tempory code to connect to node (node havent light sensor,only LED)
                            System.out.println("Code=161:to connect to node (node havent light sensor,only LED)");
                            if(!connectorAddr.equals(addr)){
                                connectorAddr=addr;
                                rConOut = (RadiogramConnection) Connector.open("radiogram://"+addr+":" + HOST_PORT);
                                dgOut = rConOut.newDatagram(50);  // only sending 12 bytes of data
                            }
                            //read data from MSG
                            addrFull=dis.readLong();//read ADDR of new Node
                            for(int i=0;i<8;i++) randomCode[i]=dis.readByte();
                            //add to list node
                            SunSpotHostApplication.nM.addNode(IEEEAddress.toDottedHex(addrFull),randomCode);
                            //
                            System.out.println("Sendding Hellomsg to Connector..");
                            dgOut.reset();
                            dgOut.writeLong(IEEEAddress.toLong(addr));
                            dt = SunSpotHostApplication.edc.EnCode(SunSpotHostApplication.key,new byte[]{80});
                            dgOut.writeInt(dt.length);
                            dgOut.write(dt);
                            rConOut.send(dgOut);
                            //
                            
                            break;
                        case (byte) 143://ok ack for create new node with manual input
                            if(IEEEAddress.toLong(addr)==(SunSpotHostApplication.gCNM.addrN)) {
                                SunSpotHostApplication.nM.addNode(addr,SunSpotHostApplication.gCNM.randomCode);
                                JOptionPane.showConfirmDialog(null, "Connect to "+ addr +" finish!");
                            }

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
