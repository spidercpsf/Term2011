/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author cpsf
 */
public class NodeManager_tmp {

    NodeManager_tmp(int i, int i0) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    class nodeInfo{
        String addr;
        byte[] randomCode;
        byte[] data;
        int rssi;
        Date lastChange;
        public nodeInfo(String addr,byte[] randomCode){
            this.addr=addr;
            this.randomCode=randomCode;
        }
    }
    ArrayList<nodeInfo> listNode;
    public NodeManager_tmp(){
        listNode= new ArrayList<NodeManager_tmp.nodeInfo>();
    }
    public void addNode(String addr,byte[] randomCode){
        for(int i=0;i<listNode.size();i++){
            if(listNode.get(i).addr.endsWith(addr)){
                listNode.get(i).randomCode=randomCode;
                return;
            }
        }
        nodeInfo nNode=new nodeInfo(addr,randomCode);
        listNode.add(nNode);
        System.out.print("New node:"+addr+" ");
        for(int i=0;i<randomCode.length;i++ )System.out.print(Integer.toHexString(randomCode[0]));
        System.out.println();
    }
    public boolean checkNodeAddr(String addr){
        for(int i=0;i<listNode.size();i++){
            if(listNode.get(i).addr.endsWith(addr)) return true;
        }
        return false;
    }
    public void updateNode(String addr,byte[] data,int rssi){
        for(int i=0;i<listNode.size();i++){
            if(listNode.get(i).addr.endsWith(addr)){
                listNode.get(i).data=data;
                listNode.get(i).rssi=rssi;
                System.out.println("Update "+addr+" "+rssi);
                listNode.get(i).lastChange= new Date();
                //call to update status FRAME
                //SunSpotHostApplication.sF.update();
                //
                return;
            }
        }
    }
    //public boolean editNodeData
}
