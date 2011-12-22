/*
 * SunSpotApplication.java
 *
 * Created on Oct 10, 2011 3:19:47 PM;
 */

package ht.cpsf.spider.term.sensornodeledlight;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.sensorboard.EDemoBoard;
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
 *           85 -> hello packet
 * 
 */
public  class SunSpotApplication extends MIDlet implements defineThreshold{
    public static threadListenLight tLL= new threadListenLight();
    public static HostConnect HC=null;
    public static RadioListenner RL= null;
    public static byte randomCode[]=new byte[8];
    public static boolean isConfig=false;
    public static long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    //data for radio commucation
    static long hostAddr;
    
    //
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Node started");
        BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
        ILightSensor lightS = EDemoBoard.getInstance().getLightSensor();
        int shortAddr= (int) (ourAddr);
        String sendAddr= Integer.toBinaryString(shortAddr);
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(shortAddr)+" "+shortAddr);
        tLL.start();
        HC= new HostConnect(14);
        RL = new RadioListenner(14);
                RL.start();
        {
            System.out.println("This is sensor-node only Light Sensor");
            while(!isConfig) Utils.sleep(1000);
            HC= new HostConnect(15);
            while(true){//sendding data
                try {
                    //sendding data
                    Utils.sleep(1000);
                    HC.reset();
                    HC.writeInt(lightS.getAverageValue());
                    HC.dg.writeDouble(1412);
                    HC.send2();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
        /*Utils.sleep(5000);
        sD.add(new byte[]{12,45,65,76,4,78,92,123});
        Utils.sleep(3000);
        sD.add(new byte[]{12,45,65,4,78,92,123});
        Utils.sleep(8000);
        sD.add(new byte[]{12,65,76,4,78,92,123});*/

    }

    protected void pauseApp() {
        // This is not currently called by the Squawk VM
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
