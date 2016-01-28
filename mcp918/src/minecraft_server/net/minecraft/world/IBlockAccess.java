package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public interface IBlockAccess
{
    TileEntity getTileEntity(BlockPos pos);

    IBlockState getBlockState(BlockPos pos);

    /**
     * Checks to see if an air block exists at the provided location. Note that this only checks to see if the blocks
     * material is set to air, meaning it is possible for non-vanilla blocks to still pass this check.
     */
    boolean isAirBlock(BlockPos pos);

    int getStrongPower(BlockPos pos, EnumFacing direction);
}
