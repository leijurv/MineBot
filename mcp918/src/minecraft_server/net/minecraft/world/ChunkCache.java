package net.minecraft.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess
{
    protected int chunkX;
    protected int chunkZ;
    protected Chunk[][] chunkArray;

    /** set by !chunk.getAreLevelsEmpty */
    protected boolean hasExtendedLevels;

    /** Reference to the World object. */
    protected World worldObj;

    public ChunkCache(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn)
    {
        this.worldObj = worldIn;
        this.chunkX = posFromIn.getX() - subIn >> 4;
        this.chunkZ = posFromIn.getZ() - subIn >> 4;
        int i = posToIn.getX() + subIn >> 4;
        int j = posToIn.getZ() + subIn >> 4;
        this.chunkArray = new Chunk[i - this.chunkX + 1][j - this.chunkZ + 1];
        this.hasExtendedLevels = true;

        for (int k = this.chunkX; k <= i; ++k)
        {
            for (int l = this.chunkZ; l <= j; ++l)
            {
                this.chunkArray[k - this.chunkX][l - this.chunkZ] = worldIn.getChunkFromChunkCoords(k, l);
            }
        }

        for (int i1 = posFromIn.getX() >> 4; i1 <= posToIn.getX() >> 4; ++i1)
        {
            for (int j1 = posFromIn.getZ() >> 4; j1 <= posToIn.getZ() >> 4; ++j1)
            {
                Chunk chunk = this.chunkArray[i1 - this.chunkX][j1 - this.chunkZ];

                if (chunk != null && !chunk.getAreLevelsEmpty(posFromIn.getY(), posToIn.getY()))
                {
                    this.hasExtendedLevels = false;
                }
            }
        }
    }

    public TileEntity getTileEntity(BlockPos pos)
    {
        int i = (pos.getX() >> 4) - this.chunkX;
        int j = (pos.getZ() >> 4) - this.chunkZ;
        return this.chunkArray[i][j].getTileEntity(pos, Chunk.EnumCreateEntityType.IMMEDIATE);
    }

    public IBlockState getBlockState(BlockPos pos)
    {
        if (pos.getY() >= 0 && pos.getY() < 256)
        {
            int i = (pos.getX() >> 4) - this.chunkX;
            int j = (pos.getZ() >> 4) - this.chunkZ;

            if (i >= 0 && i < this.chunkArray.length && j >= 0 && j < this.chunkArray[i].length)
            {
                Chunk chunk = this.chunkArray[i][j];

                if (chunk != null)
                {
                    return chunk.getBlockState(pos);
                }
            }
        }

        return Blocks.air.getDefaultState();
    }

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    public boolean isAirBlock(BlockPos pos)
    {
        return this.getBlockState(pos).getBlock().getMaterial() == Material.air;
    }

    public int getStrongPower(BlockPos pos, EnumFacing direction)
    {
        IBlockState iblockstate = this.getBlockState(pos);
        return iblockstate.getBlock().getStrongPower(this, pos, iblockstate, direction);
    }
}
