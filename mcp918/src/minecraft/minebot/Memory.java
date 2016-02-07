/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

/**
 *
 * @author leijurv
 */
public class Memory {
    public static HashMap<Block, ArrayList<BlockPos>> blockMemory = new HashMap();
    public static HashMap<String, BlockPos> playerLocationMemory = new HashMap();
    public static HashMap<String, BlockPos> goalMemory = new HashMap();
}
