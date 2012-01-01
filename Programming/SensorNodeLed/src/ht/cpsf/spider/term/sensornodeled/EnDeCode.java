/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term.sensornodeled;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author cpsf
 */
public class EnDeCode {
    byte key[];
    crc8 cr=new crc8();
    public byte[] getCode(long addr){
        if(addr==SunSpotApplication.hostAddr) return SunSpotApplication.hostCode;
        return SunSpotApplication.randomCode;
    }
    public void setCode(byte[] key){
        this.key=key;
    }
    public EnDeCode(byte[] key){
        this.key=key;
    }
    public byte[] EnCode(long addr,byte[] data){

        return EnCode(getCode(addr), data);
    }
    public byte[] EnCode(byte[] key, byte[] data){
        TEA t= new TEA(key);
        byte[] tmp= new byte[1+data.length];
        tmp[0]= (byte) cr.compute(data);
        for(int i=0;i<data.length;i++)tmp[i+1]=data[i];
        return t.encrypt(tmp);
    }
    //
    public byte[] DeCode(long addr,byte[] data){
        return DeCode(getCode(addr),data);
    }
    public byte[] DeCode(byte[] key, byte[] data){
        System.out.print("Decode using:");for(int i=0;i<8;i++) System.out.print(key[i]+ " ");System.out.println();
        TEA t= new TEA(key);
        byte[] tmp= t.decrypt(data);
        if((byte)cr.compute(tmp, 1, tmp.length-1)==tmp[0]){
            byte[] rt= new byte[tmp.length-1];
            for(int i=0;i<rt.length;i++) rt[i]=tmp[i+1];
            return rt;
        }else return null;
        //System.out.print("  Decode:");for(int i=0;i<tmp.length;i++) System.out.print(tmp[i]+ " ");System.out.println();
        //return tmp;
    }
}
