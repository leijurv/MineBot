/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

/**
 *
 * @author galdara
 */
public class SchematicLoader {
        
    public static Schematic loadFromFile(File nbtFile) throws FileNotFoundException, IOException {
        FileInputStream fileInputStream = new FileInputStream(nbtFile);
        NBTTagCompound compound = CompressedStreamTools.readCompressed(fileInputStream);
        System.out.print(compound);
        int height, width, length;
        height = compound.getInteger("Height");
        width = compound.getInteger("Width");
        length = compound.getInteger("Length");
        byte[][][] blocks  = new byte[width][height][length], data = new byte[width][height][length];
        byte[] rawBlocks = compound.getByteArray("Blocks");
        byte[] rawData = compound.getByteArray("Data");
        HashMap<BlockPos, Block> blocksMap = new HashMap<BlockPos, Block>();
        ArrayList<Block> killMe = new ArrayList<Block>();
        for(int y = 0; y < height; y++) {
            for(int z = 0; z < length; z++) {
                for(int x = 0; x < width; x++) {
                    int index = y * width * length + z * width + x;
                    blocks[x][y][z] = rawBlocks[index];
                    if(!Block.getBlockById(rawBlocks[index]).equals(Block.getBlockById(0)))
                        blocksMap.put(new BlockPos(x, y, z), Block.getBlockById(rawBlocks[index]));
                }
            }
        }
        System.out.println(killMe);
        return new Schematic(blocksMap, width, height, length);
        
    }
   
}
