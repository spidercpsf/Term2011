package org.sunspotworld;

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
    double max0=0,min0=3000,max1=0,min1=3000;
    int maxC0=0,minC0=100,maxC1=0,minC1=100;
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

    void addSignal(double value){
        //System.out.println("*"+value);
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
                        return;
                    }else if(preLen>=lenSS1&&preLen<lenSS2&&count1<lenSS1){//start
                        listener.newBit('S');
                        System.out.println("START ACK:"+preLen+" "+count1);
                        preBit=0;
                        preLen=0;
                        count1=0;
                        id=0;
                        max=0;
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
                        
                        if(max<threshold01_0){
                            
                            if(count1<thresholdLen01-3){
                                System.out.print("&");
                                //if('1'!= test.charAt(id)) System.out.println("False1:");
                                listener.newBit('1');
                            }else{
                                //if('0'!= test.charAt(id)) System.out.println("False2:");
                                listener.newBit('0');
                            }
                            preBit=0;
                        }else if(max>threshold01_1){
                            
                            //if('1'!= test.charAt(id)) System.out.println("False3:");
                            if(count1>thresholdLen01+2){
                                System.out.print("&");
                                //if('1'!= test.charAt(id)) System.out.println("False1:");
                                listener.newBit('0');
                                preBit=0;
                            }else{
                                listener.newBit('1');
                                preBit=1;
                            }
                        }else{// not sure -> using leng of signal
                            System.out.print("!");
                            if(count1>=thresholdLen01){
                                //if('0'!= test.charAt(id)) System.out.println("False4:");
                                listener.newBit('0');
                                preBit=0;
                            }else{
                                //if('1'!= test.charAt(id)) System.out.println("False5:");
                                 listener.newBit('1');
                                 preBit=1;
                            }
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
                //check start stop
            }else{
                count0++;
                if(count0>=11){
                    //System.out.print("%"+count0+"%");
                    listener.newBit('0');
                    count0-=11;
                }
            }
            max=0;
        }else{
            if(status==0) status=1;
            count1++;
            if(max< value) max=value;
        }
    }
    
}