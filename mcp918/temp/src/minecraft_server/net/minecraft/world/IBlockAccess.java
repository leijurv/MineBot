package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public interface IBlockAccess {
   TileEntity func_175625_s(BlockPos var1);

   IBlockState func_180495_p(BlockPos var1);

   boolean func_175623_d(BlockPos var1);

   int func_175627_a(BlockPos var1, EnumFacing var2);
}
