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
    //
    public boolean checkCode(long addr, byte[] data){
        return checkCode(getCode(addr), data);
    }
    public boolean checkCode(byte[] key,byte[] data){
        for(int i=0;i<key.length;i++){
            if(key[i]!=data[i]) return false;
        }
        return true;
    }
    //
    public byte[] EnCode(long addr,byte[] data){
        return EnCode(getCode(addr), data);
    }
    public byte[] EnCode(byte[] key, byte[] data){
        byte[] tmp= new byte[key.length+data.length];
        for(int i=0;i<key.length;i++)tmp[i]=key[i];
        for(int i=0;i<data.length;i++)tmp[i+key.length]=data[i];
        return tmp;
    }
    //
    public byte[] DeCode(long addr,byte[] data){
        return DeCode(getCode(addr),data);
    }
    public byte[] DeCode(byte[] key, byte[] data){
        byte[] tmp= new byte[data.length-key.length];
        for(int i=0;i<tmp.length;i++) tmp[i]=data[i+key.length];
        return tmp;
    }
}
