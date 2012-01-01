/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term.sensornodeled;

import com.sun.spot.io.j2me.radiogram.RadiogramConnection;
import com.sun.spot.util.IEEEAddress;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;

/**
 *
 * @author cpsf
 */
public class RadioListenner implements  Runnable{
    Thread runThread;
    boolean stopFlag;
    int PORT=0;
    public RadioListenner(int PORT){
        this.PORT=PORT;
    }
    public void start(){
        Thread runThread = new Thread(this);
        runThread.start();
        stopFlag=false;
    }
    public void run() {
        byte code;
        RadiogramConnection rCon = null;
        Datagram dg = null;
        RadiogramConnection rConOut = null;
        Datagram dgOut = null;
        EnDeCode edc= new EnDeCode(SunSpotApplication.randomCode);
        DataInputStream dis;
        int addrA;
        int size;
        long addrFull;
        long recvAddr;
        byte[] data=new byte[8];
        try {
            // Open up a server-side broadcast radiogram connection
            // to listen for sensor readings being sent by different SPOTs
            rCon = (RadiogramConnection) Connector.open("radiogram://:" + PORT);
            dg = rCon.newDatagram(rCon.getMaximumLength());
            //rCon.setTimeout(1000);

        } catch (Exception e) {
            System.err.println("setUp caught " + e.getMessage());
        }
        // Main data collection loop
        while (true) {
            try {
                rCon.receive(dg);
                String addr = dg.getAddress(); // read sender's Id
                //long time = dg.readLong();      // read time of the reading
                recvAddr = dg.readLong(); //get addr
                if (recvAddr != 0 && recvAddr != SunSpotApplication.ourAddr) {
                    System.out.println("MSG for " + IEEEAddress.toDottedHex(recvAddr));
                    continue;
                }
                size = dg.readInt(); //read length
                data = new byte[size]; //create new array for store data
                for (int i = 0; i < size; i++) {
                    data[i] = dg.readByte(); //get data
                    //encode and check
                } //get data
                //encode and check
                data = edc.DeCode(IEEEAddress.toLong(addr),data);
                if (data==null) {//check data from addr Node
                    continue;
                }
                
                dis = new DataInputStream(new ByteArrayInputStream(data));
                //
                int val = dis.readByte(); // read the sensor value
                System.out.println("from: " + addr + "   value = " + val);
                switch (val) {
                    case (byte) 171:
                        //ID for request from Connector, datagram: 8byteID of host (long)
                        //read data for init sensor
                        SunSpotApplication.hostAddr = dis.readLong();
                        for(int i=0;i<8;i++) SunSpotApplication.hostCode[i]= dis.readByte();
                        //
                        System.out.println("Recv data for commucation: addrHost=" + IEEEAddress.toDottedHex(SunSpotApplication.hostAddr));
                        SunSpotApplication.HC.sendOK(IEEEAddress.toLong(addr),SunSpotApplication.randomCode);//send OK ACK
                        SunSpotApplication.sD.tmpQ.empty();
                        SunSpotApplication.isConfig=true;
                        break;
                     case (byte) 142://code for create connect
                        System.out.println("REQUEST CONNECT TRUE CODE");
                        SunSpotApplication.hostAddr= IEEEAddress.toLong(addr);
                        SunSpotApplication.HC.initSend(IEEEAddress.toLong(addr), 12, SunSpotApplication.randomCode); //after replace randomCode with true Code
                        SunSpotApplication.HC.dos.writeByte((byte)143);
                        SunSpotApplication.HC.send2();
                        SunSpotApplication.isConfig=true;
                        break;
                    case (byte) 80://recv okack -> set
                        System.out.println("OKACK");
                        SunSpotApplication.HC.sender= IEEEAddress.toLong(dg.getAddress());
                        SunSpotApplication.HC.okACK=true;
                        break;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

    }

}
