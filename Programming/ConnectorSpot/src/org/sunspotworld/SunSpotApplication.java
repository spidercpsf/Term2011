/*
 * SunSpotApplication.java
 *
 * Created on Oct 10, 2011 3:19:47 PM;
 */

package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.SwitchEvent;
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
    public static EnDeCode edc;
    public static sendData sD= new sendData((char)6);
    public static threadListenLight tLL= new threadListenLight();
    public static HostConnect HC=null;
    public static RadioListenner RL= null;
    public static ITriColorLEDArray   leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    public static long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    int shortAddr= (int) (ourAddr);
    static boolean recvLight=false;
    static boolean isPushed=false;
    //data for send to node to init new node
    public static long hostAddr=0;
    public static long addrN;
    static byte randomCode[]= new byte[8];
    static byte hostCode[] = new byte[]{14,5,24,64,76,87,54,12};


    //
    protected void startApp() throws MIDletStateChangeException {
        ISwitch sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
        ISwitch sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");
        System.out.println("Node started");
        BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
        String sendAddr= Integer.toBinaryString(shortAddr);
        System.out.println("Our radio address = " + IEEEAddress.toDottedHex(shortAddr)+" "+shortAddr);
        tLL.start();
        sD.start();
        edc= new EnDeCode(hostCode);
        System.out.println("This is connector");
        RL = new RadioListenner(14);
        RL.start();
        //
        byte[] data= edc.EnCode(new byte[]{1,2,3,4,5,6,7,8}, new byte[]{14,12});
        byte[] deData= edc.DeCode(new byte[]{1,2,3,4,5,6,7,8}, data);
        if(deData==null) System.out.println("False");
        else System.out.println(deData[0]+" "+deData[1]);
        //
        HC = new HostConnect(14);
            try {
                //send hello packet to HOST
                HC.initSend(0, 1, hostCode);
                HC.dos.writeByte((byte)14);//send hello msg
                hostAddr=HC.send();
                //
                System.out.println("Addr Host="+IEEEAddress.toDottedHex(hostAddr));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        sw1.addISwitchListener(new ISwitchListener() {
            public void switchPressed(SwitchEvent evt) {
                recvLight=false;
                if(!isPushed){
                    isPushed=true;
                    for(int i=4;i<8;i++){
                        leds.getLED(i).setRGB(0, 100, 0);
                        if(i>0) leds.getLED(i-1).setOff();
                        leds.getLED(i).setOn();
                        Utils.sleep(2000);
                    }
                    for(int i=4;i<8;i++){
                        leds.getLED(i).setOff();
                    }
                    if(recvLight==false)sendToLightSensorOnlyNode();
                    Utils.sleep(3000);
                    isPushed=false;
                }
            }
            public void switchReleased(SwitchEvent evt) {
                
            }
        });
        sw2.addISwitchListener(new ISwitchListener() {

            public void switchPressed(SwitchEvent evt) {
            }

            public void switchReleased(SwitchEvent evt) {
                sD.tmpQ.empty();
            }
        });
        //tmp for send data to light sensor only node
        //Utils.sleep(5000);
        //sendToLightSensorOnlyNode();
        //
    }
    private void sendToLightSensorOnlyNode(){
            System.out.println("This is connector-node");
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
    public static void blinkLED(LEDColor cl){
        //
        SunSpotApplication.leds.getLED(3).setColor(cl);
        SunSpotApplication.leds.getLED(4).setColor(cl);
        //
        for(int j=0;j<2;j++){
                    //sendding data
                    Utils.sleep(300);
                    for(int i=3;i<5;i++){
                        leds.getLED(i).setOn();
                    }
                    Utils.sleep(300);
                    for(int i=3;i<5;i++){
                        leds.getLED(i).setOff();
                    }
        }
    }
}
