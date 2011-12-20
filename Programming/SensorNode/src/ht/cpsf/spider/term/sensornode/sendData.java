/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term.sensornode;

import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ITriColorLED;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.util.Queue;
import com.sun.spot.util.Utils;

/**
 *
 * @author giangminh
 */
public class sendData  implements defineThreshold, Runnable{
    ITriColorLEDArray leds ;
    ITriColorLED    led7;
    Queue q= new Queue();
    Queue tmpQ= new Queue();
    crc8 c8= new crc8();
    boolean isSending=false;
    boolean issenOK=false;
    boolean issenFALSE=false;
    Thread runThread;
    boolean stopFlag;

    public boolean checkFin(){
        if(q.isEmpty()&&tmpQ.isEmpty()) return true;
        return false;
    }
    public void start(){
        runThread = new Thread(this);
        runThread.start();
        stopFlag=false;
    }
    public void stop(){
        stopFlag=true;
    }
    public sendData(){
        leds = (ITriColorLEDArray) Resources.lookup(ITriColorLEDArray.class);
        led7= leds.getLED(7);
        led7.setRGB(0, 100, 0);
    }
    public void run() {
        byte[] data;
        while(!stopFlag){
            //System.out.println("Wait 1s");
            Utils.sleep(2000);//2s is time out
            if(tmpQ.isEmpty()&&!q.isEmpty()){
                tmpQ.put(q.get());
            }
            if(!tmpQ.isEmpty()&&!isSending){//if not empty
                data= (byte[]) tmpQ.get();
                tmpQ.put(data);
                System.out.print("Send:");
                for(int i=0;i<data.length;i++) System.out.print(data[i]+" ");
                System.out.println();
                send(data);


            }
        }
        System.out.println("End sendata Thread");
    }
    /**
     * Add new data to send , default data[0] is checksum -> warning
     * @param data
     */
    public void add(byte[] data){
        data[0]=(byte) crc8.compute(data, 1, data.length-1);
        q.put(data);
        System.out.println("added data");
    }
    public void OKACK(){
        if(isSending){//if isSending -> make okflag
            issenOK=true;
        }else{//else -> direct send
            //lock send
            isSending=true;
            sendOK();
            isSending=false;
        }
    }
    private void sendOK(){
        System.out.println("SENDING OK ACK");
        led7.setRGB(255, 250, 250);
        led7.setOn();
        Utils.sleep(15*3);
        led7.setOff();
        Utils.sleep(20);
        led7.setOn();
        Utils.sleep(15*3);
        led7.setOff();
        Utils.sleep(20);
        issenOK=false;
    }
    public void FALSEACK(){
        if(isSending){//if isSending -> make okflag
            issenFALSE=true;
        }else{//else -> direct send
            //lock send
            isSending=true;
            sendFALSE();
            isSending=false;
        }
    }
    private void sendFALSE(){
        System.out.println("SENDING FALSE ACK");
        led7.setRGB(255, 250, 250);
        //led7.setOff();
        //Utils.sleep(10);
        led7.setOn();
        Utils.sleep(15 *6);
        led7.setOff();
        Utils.sleep(20);
        led7.setOn();
        Utils.sleep(15*6);
        led7.setOff();
        Utils.sleep(20);
        issenFALSE=false;
    }
    public void send(String binaryChar){
        isSending=true;
        //start lights
        led7.setRGB(255, 250, 250);
        led7.setOn();
        Utils.sleep(15*6);
        led7.setOff();
        Utils.sleep(20);
        led7.setOn();
        Utils.sleep(15*3);
        led7.setOff();
        Utils.sleep(60);
        //start lights
        led7.setOn();
        Utils.sleep(15*6);
        led7.setOff();
        Utils.sleep(20);
        led7.setOn();
        Utils.sleep(15*3);
        led7.setOff();
        Utils.sleep(20);
        //send data

        for(int i=0;i<binaryChar.length();i++){

            if(binaryChar.charAt(i)=='1'){
                led7.setRGB(250, 250, 250);
                led7.setOn();
                Utils.sleep(speedSendData);
                led7.setOff();
                //if(i+1<binaryChar.length()&&binaryChar.charAt(i+1)=='1')Utils.sleep(speedSendData2*3/2);
                //else
                Utils.sleep(speedSendData2);
                //System.out.println("1");
            }else if(binaryChar.charAt(i)=='0'){
                led7.setRGB(50, 40, 0);
                led7.setOn();
                Utils.sleep(speedSendData2);
                led7.setOff();
                Utils.sleep(speedSendData);
                //System.out.println("0");
            }


        }
        //stop
        led7.setRGB(255, 250, 250);
        //led7.setOff();
        //Utils.sleep(10);
        led7.setOn();
        Utils.sleep(15 *3);
        led7.setOff();
        Utils.sleep(20);
        led7.setOn();
        Utils.sleep(15*6);
        led7.setOff();
        Utils.sleep(20);
        isSending=false;
    }
    public void send(byte[] byteArr){
        isSending=true;
        int tmp2;
        char tmp;

        //start lights
        led7.setRGB(255, 250, 250);
        led7.setOn();
        Utils.sleep(15*6);
        led7.setOff();
        Utils.sleep(20);
        led7.setOn();
        Utils.sleep(15*3);
        led7.setOff();
        Utils.sleep(30);
        //start lights
        led7.setOn();
        Utils.sleep(15*8);
        led7.setOff();
        Utils.sleep(20);
        led7.setOn();
        Utils.sleep(15*3);
        led7.setOff();
        Utils.sleep(20);
        //send data
        for(int i=0;i<byteArr.length;i++){
            if(issenOK){
                sendOK();
                issenOK=false;
            }else if(issenFALSE){
                sendFALSE();
                issenFALSE=false;
            }
            //System.out.println(byteArr[i]);
            tmp2=(byteArr[i]<0?256+byteArr[i]:byteArr[i]);
            for(int j=0;j<8;j++){
		tmp=(char)((tmp2<<j)%256);
		tmp>>=7;
		if(tmp==1){
                        led7.setRGB(250, 250, 250);
                        led7.setOn();
                        Utils.sleep(speedSendData);
                        led7.setOff();
                        //if(i+1<binaryChar.length()&&binaryChar.charAt(i+1)=='1')Utils.sleep(speedSendData2*3/2);
                        //else
                        Utils.sleep(speedSendData2);
                }else if(tmp==0){
                        led7.setRGB(50, 40, 0);
                        led7.setOn();
                        Utils.sleep(speedSendData2);
                        led7.setOff();
                        Utils.sleep(speedSendData);
                        //System.out.println("0");
                }
            }
	}

        //stop
        led7.setRGB(255, 250, 250);
        //led7.setOff();
        //Utils.sleep(10);
        led7.setOn();
        Utils.sleep(15 *3);
        led7.setOff();
        Utils.sleep(20);
        led7.setOn();
        Utils.sleep(15*6);
        led7.setOff();
        Utils.sleep(20);
        //check for send ACK
            if(issenOK){
                sendOK();
                issenOK=false;
            }else if(issenFALSE){
                sendFALSE();
                issenFALSE=false;
            }
        isSending=false;
    }
}
