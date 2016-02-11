/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.block.Block;
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
    
    /**
     * Tuple links the BlockPos and Block to one another.
     * @param blockPos
     * @return Tuple of BlockPos and Block
     */
    public Tuple<BlockPos, Block> getTupleFromBlockPos(BlockPos blockPos) {
        if(schematicBlocks.containsKey(blockPos)) {
            return new Tuple<BlockPos, Block>(blockPos, schematicBlocks.get(blockPos));
        }
        return null;
    }
    
    /**
     * Gets given block type in schematic from a BlockPos
     * @param blockPos
     * @return 
     */
    public Block getBlockFromBlockPos(BlockPos blockPos) {
        return schematicBlocks.get(blockPos);
    }
    
    /**
     * Gives the length along the X axis
     * @return Schematic width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gives the height along the y axis
     * @return Schematic height
     */
    public int getHeight() {
        return height;
    }
    
    
    /**
     * Gives the length along the z axis
     * @return Schematic length
     */
    public int getLength() {
        return length;
    }
    
    public ArrayList<Entry<BlockPos, Block>> getEntries() {
        return new ArrayList(schematicBlocks.entrySet());
    }
    
}
