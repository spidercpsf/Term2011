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
import com.sun.spot.util.Utils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.microedition.io.*;


/**
 * Sample Sun SPOT host application
 */
public class SunSpotHostApplication {
    NewNodeCreate NNC;
    //public static NodeManager_tmp NM;
    private sensorDataListenner sDL;
    //public static statusFrame sF;
    static nodeManager nM= new nodeManager(20,10);
    static long ourAddr = RadioFactory.getRadioPolicyManager().getIEEEAddress();
    public static byte key[]= new byte[]{14,5,24,64,76,87,54,12};
    public static EnDeCode edc= new EnDeCode(SunSpotHostApplication.key);
    //public static GUICreateNodeManual gCNM = new GUICreateNodeManual();
    public static GUICreateNodeManual2 gCNM = new GUICreateNodeManual2();
    public static HostConnect HC = new HostConnect(14);
    public static  GUIFrame gF= new GUIFrame("Node Info");

    //for count bad connect count
    public static int misCount=0;
    public static int batteryConnector=0;
    /*
     * Print out our radio address.
     */
    public void run() throws IOException, NoSuchAlgorithmException {
        
        byte[] data= edc.EnCode(new byte[]{1,2,3,4,5,6,7,8}, new byte[]{14,12});
        byte[] deData= edc.DeCode(new byte[]{1,2,3,4,5,6,7,8}, data);
        if(deData==null) System.out.println("False");
        else System.out.println(deData[0]+" "+deData[1]);
        crc8 cc= new crc8();

        System.out.println(cc.compute(new byte[]{0})+" "+cc.compute(new byte[]{(byte)255}) );


        Thread GUI = new Thread(new Runnable() {
            public void run() {

                while(true){
                    try {
                        Thread.sleep(3000);
                        if( gF.connectorTime!=null )gF.statusLB.setText("Connect time:"+(new Date().getTime()-gF.connectorTime.getTime())/1000 + " bad count="+misCount + "battery="+batteryConnector);
                        else gF.statusLB.setText("Not connect to connector, bad count="+misCount);
                        gF.update();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SunSpotHostApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });
        GUI.start();
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
        //create code:
        Utils.sleep(1000);
        String[] listNode=new String[]{"6A27","4F7D","4F74","59E0","66B2","3F81","5955","66F1","72D9","687B","526D","59C0","744F","4B00","3F83","574D","7233","5A75","7456"};
        long addrTmp1;
        String addrTmp2;
        for(int j=0;j<listNode.length;j++){
            addrTmp2 = "0014.4F01.0000."+listNode[j];
            addrTmp1= IEEEAddress.toLong(addrTmp2);
            System.out.println("****");
            System.out.println("Address:"+listNode[j]);
            System.out.println("Code:"+calcCode(addrTmp1));
            System.out.println("****");
            //gCNM.addAddr(calcCode(ourAddr+j));
            //Utils.sleep(1000);
        }
        //+""
    }

    /**
     * Start up the host application.
     *
     * @param args any command line arguments
     */
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        SunSpotHostApplication app = new SunSpotHostApplication();

        app.run();
    }
    String calcCode(long addr){
        byte[] rt= new byte[8];
        long code;
        addr%=10000;
        crc8 cc= new crc8();
        String addrStr=IEEEAddress.toDottedHex(addr);
        //System.out.println(addrStr);
        code=crc8.checksum(addrStr.getBytes());
        code+=crc8.checksum(IEEEAddress.toDottedHex(code).getBytes(),6)%10000;
        return IEEEAddress.toDottedHex(code);
    }
    String MD5(long addr) throws NoSuchAlgorithmException{
           String s=IEEEAddress.toDottedHex(addr);
           MessageDigest m=MessageDigest.getInstance("MD5");
           m.update(s.getBytes(),0,s.length());
           s=new BigInteger(1,m.digest()).toString(16);
           m.update(s.getBytes(),0,s.length());
           String rt=new BigInteger(1,m.digest()).toString(16);
           System.out.println("MD5: "+rt);
           return rt;
    }
}
