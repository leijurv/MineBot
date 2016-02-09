/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

/**
 *
 * @author leijurv
 */
public class TreePuncher extends BlockPuncher{
    public static void tick() {
        BlockPuncher.tick("log");
    }
}
