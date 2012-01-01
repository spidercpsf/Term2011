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
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.sensorboard.peripheral.TemperatureInput;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
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
    public static EnDeCode edc;
    public static sendData sD= new sendData();
    public static HostConnect HC=null;
    public static RadioListenner RL= null;
    static long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    static byte randomCode[]= new byte[8];
    static byte hostCode[]= new byte[8];
    static boolean isConfig=false;
    //data for radio commucation
    static long hostAddr;
    //
    ITriColorLEDArray   leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    TemperatureInput temp = (TemperatureInput) Resources.lookup(TemperatureInput.class);
    ITriColorLED    led3= leds.getLED(3);
    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Node started");
        BootloaderListenerService.getInstance().start();   // monitor the USB (if connected) and recognize commands from host
        int shortAddr= (int) (ourAddr);
        String sendAddr= Integer.toBinaryString(shortAddr);
        System.out.println("Node radio address = " + IEEEAddress.toDottedHex(shortAddr)+" "+shortAddr);
        sD.start();
        RL = new RadioListenner(14);
        RL.start();
        HC= new HostConnect(14);
        System.out.println("This is sensor-node");
        //this is for manual connect
        ISwitch sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
        /*ISwitch sw2 = (ISwitch) Resources.lookup(ISwitch.class, "SW2");
        sw2.addISwitchListener(new ISwitchListener() {

            public void switchPressed(SwitchEvent evt) {
            }

            public void switchReleased(SwitchEvent evt) {
                SunSpotApplication.sD.tmpQ.empty();//switch to manual mode
                sD.add(new byte[]{0,0});
            }
        });*/
        sw1.addISwitchListener(new ISwitchListener() {
            public void switchPressed(SwitchEvent evt) {
            }
            public void switchReleased(SwitchEvent evt) {
                isConfig=false;
                SunSpotApplication.sD.tmpQ.empty();//switch to manual mode
                //init randomCode
                //create byte array of code
                DataOutputStream dos=null;
                ByteArrayOutputStream baos=null;
                baos= new ByteArrayOutputStream(8);
                dos=new DataOutputStream(baos);
                long rdc=calcCode(ourAddr);
                try {
                    dos.writeLong(rdc);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                randomCode=baos.toByteArray();
                System.out.println("My code:"+IEEEAddress.toDottedHex(rdc));
                System.out.print("My code:");
                for(int i=0;i<8;i++)System.out.print(randomCode[i]+" ");System.out.println();
                //
                //
                try {
                    HC.initSend(1412, 12, randomCode); //after replace randomCode with true Code, 1412 is the addresf of hello msg
                    HC.dos.writeByte((byte)13);
                    HC.send2();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                //
                //
            }
        });
        //
        Random rd= new Random(new Date().getTime());
        byte data[]=new byte[14];
        data[0]=0;
        data[1]=(byte)171;//data code for LED only
        data[2]=(byte)((shortAddr>>24)%256);
        data[3]=(byte)((shortAddr>>16)%256);
        data[4]=(byte)((shortAddr>>8)%256);
        data[5]=(byte)((shortAddr)%256);
        for(int i=6;i<14;i++) data[i]=randomCode[i-6]=(byte)(rd.nextInt()%255);
        edc= new EnDeCode(randomCode);
        sD.add(data);
        while(!isConfig||!sD.checkFin()) Utils.sleep(3000);//check for finish send data
        //send light sensor demo
        HostConnect HC2= new HostConnect(15);
        ILightSensor lightS = EDemoBoard.getInstance().getLightSensor();
        for(int i=0;i<8;i++){
              leds.getLED(i).setRGB(0, 0, 20);
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
                    HC2.initSend(hostAddr, 12,randomCode);//after replace randomCode with true Code
                    HC2.dos.writeInt(lightS.getValue());
                    HC2.dos.writeDouble(1412.0);
                    HC2.send2();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
       }
       //

    }
    long calcCode(long addr){
        byte[] rt= new byte[8];
        long code;
        addr%=10000;
        crc8 cc= new crc8();
        String addrStr=IEEEAddress.toDottedHex(addr);
        System.out.println(addrStr);
        code=crc8.checksum(addrStr.getBytes());
        code+=crc8.checksum(IEEEAddress.toDottedHex(code).getBytes(),6)%10000;
        return code;
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
