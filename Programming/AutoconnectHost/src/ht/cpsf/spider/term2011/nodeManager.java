/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ht.cpsf.spider.term2011;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author cpsf
 */
public class nodeManager {
    ArrayList<nodeOb> listNode;
    Random rd;
    int weight,height;
    public nodeManager(int weight,int height){
        listNode=new ArrayList<nodeOb>();
        rd= new Random(new Date().getTime());
        this.weight=weight;
        this.height=height;
    }
    /**
     * Add node to list, return 0 if success, 1 if already have
     * x,y is % in screen
     * @param ID
     * @param info
     * @param x
     * @param y
     * @return
     */
    public int addNode(String ID,String info,int x,int y){
        int i;
        System.out.println("Add node "+ ID +" at "+x+" "+y);
        for(i=0;i<listNode.size();i++) if(listNode.get(i).ID.equals(ID)){
            System.out.println("Already have "+ID);
            return 1;
        }
        nodeOb newN= new nodeOb(ID, info, x, y,weight,height);
        listNode.add(newN);
        return 0;
    }
    /**
     * Add node to list, return 0 if success, 1 if already have
     * x,y is % in screen, is random value
     * @param ID
     * @param info
     * @param x
     * @param y
     * @return
     */
    public int addNode(String ID,String info){
        int x,y;
        
        //calc max node in 1 line
        int maxN1L=(int) (100 / (weight * 1.2));
        x=(listNode.size()%maxN1L)*(int)(1.2*weight);
        y=(listNode.size()/maxN1L)*(int)(1.2*height);
        //
        
        return addNode(ID, info,x,y);
    }
    /**
     * Update data to node
     * @param ID
     * @param info
     * @return
     */
    public int updateNode(String ID,byte[] data){
        int i;
        for(i=0;i<listNode.size();i++) if(listNode.get(i).ID.equals(ID)){
            listNode.get(i).update(data);
            return 0;
        }
        System.out.println("Cant find "+ID);
        return 1;
    }
    ArrayList<nodeOb> getList(){
        return listNode;
    }
}
