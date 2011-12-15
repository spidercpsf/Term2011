/*
 * SunSpotApplication.java
 *
 * Created on Oct 10, 2011 3:19:47 PM;
 */

package ht.cpsf.spider.term.sensornodeled;

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
    public static sendData sD= new sendData();
    public static HostConnect HC=null;
    
    ITriColorLEDArray   leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    ITriColorLED    led3= leds.getLED(3);
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Node started");
        BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
        long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
        int shortAddr= (int) (ourAddr);
        String sendAddr= Integer.toBinaryString(shortAddr);
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(shortAddr)+" "+shortAddr);
        sD.start();
        if(shortAddr==19361){
            try {
                System.out.println("This is connector");
                HC = new HostConnect("C0A8.0B11.0000.DCC8", 14);
                HC.writeInt(1412);
                HC.send();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }else{
            System.out.println("This is sensor-node");
            //sD.add(new byte[]{0,85});//hello packet
            //sD.add(new byte[]{0,(byte)170,(byte)((shortAddr>>24)%256),(byte)((shortAddr>>16)%256),(byte)((shortAddr>>8)%256),(byte)((shortAddr)%256)});//send addr
            Random rd= new Random(new Date().getTime());
            byte data[]=new byte[14];
            byte randomCode[]= new byte[8];
            data[0]=0;
            data[1]=(byte)170;//
            data[2]=(byte)((shortAddr>>24)%256);
            data[3]=(byte)((shortAddr>>16)%256);
            data[4]=(byte)((shortAddr>>8)%256);
            data[5]=(byte)((shortAddr)%256);
            for(int i=6;i<14;i++) data[i]=randomCode[i-6]=(byte)(rd.nextInt()%255);
            EnDeCode.setKey(randomCode);
            sD.add(data);
            while(!sD.checkFin()) Utils.sleep(3000);//check for finish send data
            HostConnect HC= new HostConnect("", 15);
            ILightSensor lightS = EDemoBoard.getInstance().getLightSensor();
            for(int i=0;i<8;i++){
                        leds.getLED(i).setRGB(0, 0, 180);
            }
            while(true){//sendding data
                try {
                    //sendding data
                    Utils.sleep(500);
                    for(int i=0;i<8;i++){
                        leds.getLED(i).setOn();
                    }
                    Utils.sleep(500);
                    for(int i=0;i<8;i++){
                        leds.getLED(i).setOff();
                    }
                    HC.reset();
                    HC.writeInt(lightS.getAverageValue());
                    HC.send();
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
