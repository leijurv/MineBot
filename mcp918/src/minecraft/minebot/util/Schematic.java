/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Tuple;

/**
 *
 * @author galdara
 */
public class Schematic {
    
    private HashMap<BlockPos, Block> schematicBlocks;
    private int width;
    private int height;
    private int length;
    
    public Schematic(HashMap<BlockPos, Block> blocks, int width, int height, int length) {
        schematicBlocks = blocks;
        
        this.width = width;
        this.height = height;
        this.length = length;
    }
    
    public Tuple<BlockPos, Block> getTupleFromBlockPos(BlockPos blockPos) {
        if(blockPos != null) {
            if(schematicBlocks.containsKey(blockPos)) {
                return new Tuple<BlockPos, Block>(blockPos, schematicBlocks.get(blockPos));
            }
        }
        return null;
    }
    
    public Block getBlockFromBlockPos(BlockPos blockPos) {
        Tuple<BlockPos, Block> tuple = getTupleFromBlockPos(blockPos);
        if(tuple != null) {
            return tuple.getSecond();
        }
        return null;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getLength() {
        return length;
    }
    
}
