/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.mining;

import minebot.MineBot;

/**
 *
 * @author avecowa
 */
public class Miner {
    public static boolean isMining = false;
    public static void goMining() {
        isMining = true;
        MineBot.getToY(12);
    }
    public static void stopMining() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
