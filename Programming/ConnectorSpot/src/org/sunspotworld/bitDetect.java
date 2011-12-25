package org.sunspotworld;

import java.io.IOException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cpsf
 */
public class bitDetect {
    int status;//status = 0-> wait new
                //status =1 -> couting value and calc max signal
    double thresholdStatus;
    double threshold01_0,threshold01_1;
    double thresholdLen01;
    signalListener listener;
    //
    double max=0;
    int preBit=0;
    int preLen=0;
    int count0=0;
    int count1=0;
    int lenSS0=0;
    int lenSS1=0;
    int lenSS2=0;
    //
    int id=0;
    double sum;
    double max0=0,min0=3000,max1=0,min1=3000;
    int maxC0=0,minC0=100,maxC1=0,minC1=100;
    double xLight=1;
    double xLight1=1;
    double minL,maxL;
    double minL1,maxL1;
    /**
     * lenSS0<=x1<lenSS1<=x2<lenSS2
     * @param thresholdStatus
     * @param threashold01_0
     * @param threashold01_1
     * @param lenSS0
     * @param lenSS1
     * @param lenSS2
     * @param listener
     */
    public bitDetect(double thresholdStatus,double threashold01_0,double threashold01_1,int lenSS0,int lenSS1,int lenSS2,int thresholdLen01,signalListener listener){
        status=1;
        this.thresholdStatus= thresholdStatus;
        this.threshold01_0= threashold01_0;
        this.threshold01_1= threashold01_1;
        this.listener= listener;
        this.lenSS0=lenSS0;
        this.lenSS1=lenSS1;
        this.lenSS2=lenSS2;
        this.thresholdLen01=thresholdLen01;

    }

    void addSignal(double value) throws IOException{
        //if(isStart) System.out.println(value);
        
        if(value<thresholdStatus){
            if(status==1){
                id+=1;
                if(preBit==1&&preLen>=lenSS0&&max>threshold01_1&&count1>=lenSS0){
                    if(count1>=lenSS1&&preLen<lenSS1&&count1<lenSS2){//stop
                        System.out.println("STOP ACK:"+preLen+" "+count1+" idCount="+id);
                        listener.newBit('T');
                        preBit=0;
                        preLen=0;
                        count1=0;
                        id=0;
                        max=0;
                        //send hello packet to HOST
                            SunSpotApplication.HC.initSend(0, 1, SunSpotApplication.hostCode);
                            SunSpotApplication.HC.dos.writeByte((byte)2);//send log
                            SunSpotApplication.HC.dos.writeDouble(maxL);//send log
                            SunSpotApplication.HC.dos.writeDouble(minL);//send log
                            SunSpotApplication.HC.dos.writeDouble(maxL1);//send log
                            SunSpotApplication.HC.dos.writeDouble(minL1);//send log
                            SunSpotApplication.HC.send2();
                        //
                        return;
                    }else if(preLen>=lenSS1&&preLen<lenSS2&&count1<lenSS1){//start
                        listener.newBit('S');
                        System.out.println("START ACK:"+preLen+" "+count1);
                        xLight=(sum/count1)/136;
                        if(xLight<0.85)xLight=0.85;
                        if(xLight>1.2)xLight=1.2;

                        System.out.println("Avg="+(sum/count1)+" ->*="+xLight);
                        //send hello packet to HOST
                            SunSpotApplication.HC.initSend(0, 1, SunSpotApplication.hostCode);
                            SunSpotApplication.HC.dos.writeByte((byte)0);//send log
                            SunSpotApplication.HC.dos.writeDouble(sum/count1);//send log
                            SunSpotApplication.HC.dos.writeDouble(xLight);//send log
                            SunSpotApplication.HC.send2();
                        //
                        preBit=0;
                        preLen=0;
                        count1=0;
                        id=0;
                        max=0;
                        //
                        minL=2000;
                        maxL=0;
                        minL1=2000;
                        maxL1=0;
                        //
                        sum=0;
                        
                        System.out.println("Env:"+status+" "+thresholdStatus+" "+threshold01_0+" "+threshold01_1+" "+thresholdLen01+" "+max+" "+preBit+" "+count0+" "+count1+" "+lenSS0+" "+lenSS1+" "+lenSS2+" "+id);
                        return;
                    }else if(preLen<lenSS1&&count1<lenSS1){//OK ACK
                        listener.newBit('O');
                        System.out.println("OK ACK:"+preLen+" "+count1);
                        preBit=0;
                        preLen=0;
                        count1=0;
                        max=0;
                        return;
                    }else if(preLen>=lenSS1&&count1>=lenSS1&&preLen<lenSS2&&count1<lenSS2){//FALSE ACK
                        listener.newBit('F');
                        System.out.println("FALSE ACK:"+preLen+" "+count1);
                        preBit=0;
                        preLen=0;
                        count1=0;
                        max=0;
                        return;
                    }
                }     
                if(count1>0&&count1<lenSS0){
                        xLight=sum/(count1*count1);
                        if(minL>xLight)minL=xLight;
                        if(maxL<xLight)maxL=xLight;
                        xLight1=max/(count1);
                        if(minL1>xLight1)minL1=xLight1;
                        if(maxL1<xLight1)maxL1=xLight1;
                        /*if(max<threshold01_0){
                            listener.newBit('0');
                            preBit=0;
                        }else if(max>threshold01_1){
                                listener.newBit('1');
                                preBit=1;
                        }else{// not sure -> using leng of signal
                            System.out.print("!");
                            if(count1>=thresholdLen01){
                                listener.newBit('0');
                                preBit=0;
                            }else{
                                 listener.newBit('1');
                                 preBit=1;
                            }
                        }*/
                        if(max*1.0/count1 >  11){
                            listener.newBit('1');
                                preBit=1;
                        }else{
                            listener.newBit('0');
                                preBit=0;
                        }

                }else if(count1>0){
                    if(max>threshold01_0){
                        preBit=1;
                        
                    }

                }
                preLen=count1;
                count1=0;
                status=0;
                count0=0;
                sum=0;
                //check start stop
            }else{
                count0++;
                if(count0>=100){
                    System.out.println("#");
                    //listener.newBit('0');
                    count0-=100;
                }
            }
            max=0;
        }else{
            if(status==0) status=1;
            count1++;
            sum+=value;
            if(max< value) max=value;
        }
    }
    
}