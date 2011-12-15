package org.sunspotworld;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author cpsf
 */
public interface signalListener {
    void newBit(char b);//b=0-> 0, 1-> 1, 2-> start, 3 -> end
}
