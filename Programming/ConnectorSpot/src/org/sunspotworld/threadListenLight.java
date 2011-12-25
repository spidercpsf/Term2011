/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.sunspotworld;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ILightSensor;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.sensorboard.EDemoBoard;
import com.sun.spot.service.BootloaderListenerService;
import com.sun.spot.util.IEEEAddress;
import com.sun.spot.util.Utils;
import java.io.IOException;
import java.util.Date;

/**
 *
 * @author giangminh
 */
public class threadListenLight implements Runnable,defineThreshold{
    private Thread runThread;
    private ITriColorLEDArray leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
    private ISwitch sw1 = (ISwitch) Resources.lookup(ISwitch.class, "SW1");
    private ITriColorLED led = leds.getLED(0);
    private ILightSensor lightS = EDemoBoard.getInstance().getLightSensor();
    public static boolean stopFlag;
    public threadListenLight(){

    }
    public void start(){
        runThread= new Thread(this);
        runThread.start();
        stopFlag=false;
    }
    public void run() {
        int rawVl=0;
        double l;
        smoothSignal sS= new smoothSignal();
        bitListenner bL= new bitListenner();
        //bitDetect bD= new bitDetect(3, 40, 60, 14,28,50,9,bL);
        bitDetect bD= new bitDetect(3, 40, 60, 14,26,50,7,bL);
        while(!stopFlag){
            try {
                rawVl = lightS.getValue();
                Utils.sleep(speedGetData);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                l=sS.getData(rawVl);
                //System.out.println(l);
                bD.addSignal(l);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
