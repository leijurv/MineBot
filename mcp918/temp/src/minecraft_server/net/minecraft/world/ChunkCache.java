package net.minecraft.world;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkCache implements IBlockAccess {
   protected int field_72818_a;
   protected int field_72816_b;
   protected Chunk[][] field_72817_c;
   protected boolean field_72814_d;
   protected World field_72815_e;

   public ChunkCache(World p_i45746_1_, BlockPos p_i45746_2_, BlockPos p_i45746_3_, int p_i45746_4_) {
      this.field_72815_e = p_i45746_1_;
      this.field_72818_a = p_i45746_2_.func_177958_n() - p_i45746_4_ >> 4;
      this.field_72816_b = p_i45746_2_.func_177952_p() - p_i45746_4_ >> 4;
      int i = p_i45746_3_.func_177958_n() + p_i45746_4_ >> 4;
      int j = p_i45746_3_.func_177952_p() + p_i45746_4_ >> 4;
      this.field_72817_c = new Chunk[i - this.field_72818_a + 1][j - this.field_72816_b + 1];
      this.field_72814_d = true;

      for(int k = this.field_72818_a; k <= i; ++k) {
         for(int l = this.field_72816_b; l <= j; ++l) {
            this.field_72817_c[k - this.field_72818_a][l - this.field_72816_b] = p_i45746_1_.func_72964_e(k, l);
         }
      }

      for(int i1 = p_i45746_2_.func_177958_n() >> 4; i1 <= p_i45746_3_.func_177958_n() >> 4; ++i1) {
         for(int j1 = p_i45746_2_.func_177952_p() >> 4; j1 <= p_i45746_3_.func_177952_p() >> 4; ++j1) {
            Chunk chunk = this.field_72817_c[i1 - this.field_72818_a][j1 - this.field_72816_b];
            if(chunk != null && !chunk.func_76606_c(p_i45746_2_.func_177956_o(), p_i45746_3_.func_177956_o())) {
               this.field_72814_d = false;
            }
         }
      }

   }

   public TileEntity func_175625_s(BlockPos p_175625_1_) {
      int i = (p_175625_1_.func_177958_n() >> 4) - this.field_72818_a;
      int j = (p_175625_1_.func_177952_p() >> 4) - this.field_72816_b;
      return this.field_72817_c[i][j].func_177424_a(p_175625_1_, Chunk.EnumCreateEntityType.IMMEDIATE);
   }

   public IBlockState func_180495_p(BlockPos p_180495_1_) {
      if(p_180495_1_.func_177956_o() >= 0 && p_180495_1_.func_177956_o() < 256) {
         int i = (p_180495_1_.func_177958_n() >> 4) - this.field_72818_a;
         int j = (p_180495_1_.func_177952_p() >> 4) - this.field_72816_b;
         if(i >= 0 && i < this.field_72817_c.length && j >= 0 && j < this.field_72817_c[i].length) {
            Chunk chunk = this.field_72817_c[i][j];
            if(chunk != null) {
               return chunk.func_177435_g(p_180495_1_);
            }
         }
      }

      return Blocks.field_150350_a.func_176223_P();
   }

   public boolean func_175623_d(BlockPos p_175623_1_) {
      return this.func_180495_p(p_175623_1_).func_177230_c().func_149688_o() == Material.field_151579_a;
   }

   public int func_175627_a(BlockPos p_175627_1_, EnumFacing p_175627_2_) {
      IBlockState iblockstate = this.func_180495_p(p_175627_1_);
      return iblockstate.func_177230_c().func_176211_b(this, p_175627_1_, iblockstate, p_175627_2_);
   }
}
