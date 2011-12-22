/*
 * SunSpotApplication.java
 *
 * Created on Oct 10, 2011 3:19:47 PM;
 */

package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * The startApp method of this class is called by the VM to start the
 * application.
 *
 * The manifest specifies this class as MIDlet-1, which means it will
 * be selected for execution.
 * ID map
 * 10101010->170: send addr
 * 
 */
public  class SunSpotApplication extends MIDlet implements defineThreshold{
    public static sendData sD= new sendData();
    public static threadListenLight tLL= new threadListenLight();
    public static HostConnect HC=null;
    public static RadioListenner RL= null;
    public static ITriColorLEDArray   leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    public static long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    int shortAddr= (int) (ourAddr);
    //data for send to node to init new node
    public static long hostAddr;
    static byte randomCode[]= new byte[8];
    //
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Node started");
        BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
        String sendAddr= Integer.toBinaryString(shortAddr);
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(shortAddr)+" "+shortAddr);
        tLL.start();
        sD.start();
        
            try {
                System.out.println("This is connector");
                RL = new RadioListenner(14);
                RL.start();
                HC = new HostConnect(14);
                HC.dg.writeLong(0);//set to broadcast
                HC.writeByte((byte)14);//send hello msg
                hostAddr=HC.send();
                System.out.println("Addr Host="+IEEEAddress.toDottedHex(hostAddr));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        
        //tmp for send data to light sensor only node
        Utils.sleep(5000);
        sendToLightSensorOnlyNode();
        //
    }
    private void sendToLightSensorOnlyNode(){
            System.out.println("This is sensor-node");
            Random rd= new Random(new Date().getTime());
            byte data[]=new byte[14];
            data[0]=0;
            data[1]=(byte)172;//data code for LED only
            data[2]=(byte)((shortAddr>>24)%256);
            data[3]=(byte)((shortAddr>>16)%256);
            data[4]=(byte)((shortAddr>>8)%256);
            data[5]=(byte)((shortAddr)%256);
            for(int i=6;i<14;i++) data[i]=randomCode[i-6]=(byte)(rd.nextInt()%255);
            sD.add(data);
            while(!sD.checkFin()) Utils.sleep(3000);//check for finish send data
    }
    protected void pauseApp() {
    }

    /**
     * Called if the MIDlet is terminated by the system.
     * It is not called if MIDlet.notifyDestroyed() was called.
     *
     * @param unconditional If true the MIDlet must cleanup and release all resources.
     */
    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }
}
