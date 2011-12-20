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
    public static ITriColorLEDArray   leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    public static long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    //data for send to node to init new node
    public static long hostAddr;

    //
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Node started");
        BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
        
        int shortAddr= (int) (ourAddr);
        String sendAddr= Integer.toBinaryString(shortAddr);
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(shortAddr)+" "+shortAddr);
        tLL.start();
        sD.start();
            try {
                System.out.println("This is connector");

                HC = new HostConnect(14);
                HC.dg.writeLong(0);//set to broadcast
                HC.writeByte((byte)14);//send hello msg
                hostAddr=HC.send();
                System.out.println("Addr Host="+IEEEAddress.toDottedHex(hostAddr));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
