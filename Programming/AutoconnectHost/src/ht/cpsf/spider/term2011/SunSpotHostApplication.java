/*
 * SunSpotHostApplication.java
 *
 * Created on Dec 4, 2011 6:58:18 PM;
 */

package ht.cpsf.spider.term2011;

import com.sun.spot.peripheral.radio.RadioFactory;
import com.sun.spot.peripheral.radio.IRadioPolicyManager;
import com.sun.spot.io.j2me.radiostream.*;
import com.sun.spot.io.j2me.radiogram.*;
import com.sun.spot.util.IEEEAddress;

import java.io.*;
import javax.microedition.io.*;


/**
 * Sample Sun SPOT host application
 */
public class SunSpotHostApplication {
    NewNodeCreate NNC;
    //public static NodeManager_tmp NM;
    private sensorDataListenner sDL;
    private GUIFrame gF= new GUIFrame("Node Info");
    //public static statusFrame sF;
    static nodeManager nM= new nodeManager(20,10);
    static long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    /**
     * Print out our radio address.
     */
    public void run() throws IOException {
        
        //DatagramConnection conCnt=(DatagramConnection)Connector.open("radiogram://:1412");//connector
        //DatagramConnection sensorData=(DatagramConnection)Connector.open("radiogram://:1412");//recv from sensor node
        //open conect (listener)
        NNC= new NewNodeCreate();
        //NM= new NodeManager_tmp();
        //sF= new statusFrame("Status Node Sensor");
        sDL= new sensorDataListenner();
        System.out.println("Host radio address = " + IEEEAddress.toDottedHex(ourAddr));
        NNC.start();
        sDL.start();
        
    }

    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) throws IOException {
        SunSpotHostApplication app = new SunSpotHostApplication();
        app.run();
    }
}
