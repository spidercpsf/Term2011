/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term.sensornodeled;

/**
 *
 * @author cpsf
 */
public class EnDeCode {
    static byte[] key;//length=8
    static void setKey(byte[] key){
        EnDeCode.key=key;
    }
    public static boolean check(byte[] data){
        return true;
    }
    public static byte[] decode(byte[] data){
        return data;
    }
    public static byte[] encode(byte[] data){
        return data;
    }
    public static byte[] encode(byte[] key,long ID,byte[] data){
        return data;
    }
    public static byte[] decode(byte[] key,long ID,byte[] data){
        return data;
    }
}
