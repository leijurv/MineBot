package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLiquid extends Block
{
    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);

    protected BlockLiquid(Material materialIn)
    {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setTickRandomly(true);
    }

    public boolean isPassable(IBlockAccess worldIn, BlockPos pos)
    {
        return this.blockMaterial != Material.lava;
    }

    /**
     * Returns the percentage of the liquid block that is air, based on the given flow decay of the liquid
     */
    public static float getLiquidHeightPercent(int meta)
    {
        if (meta >= 8)
        {
            meta = 0;
        }

        return (float)(meta + 1) / 9.0F;
    }

    protected int getLevel(IBlockAccess worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().getMaterial() == this.blockMaterial ? ((Integer)worldIn.getBlockState(pos).getValue(LEVEL)).intValue() : -1;
    }

    protected int getEffectiveFlowDecay(IBlockAccess worldIn, BlockPos pos)
    {
        int i = this.getLevel(worldIn, pos);
        return i >= 8 ? 0 : i;
    }

    public boolean isFullCube()
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid)
    {
        return hitIfLiquid && ((Integer)state.getValue(LEVEL)).intValue() == 0;
    }

    /**
     * Whether this Block is solid on the given Side
     */
    public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side)
    {
        Material material = worldIn.getBlockState(pos).getBlock().getMaterial();
        return material == this.blockMaterial ? false : (side == EnumFacing.UP ? true : (material == Material.ice ? false : super.isBlockSolid(worldIn, pos, side)));
    }

    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state)
    {
        return null;
    }

    /**
     * The type of render function called. 3 for standard block models, 2 for TESR's, 1 for liquids, -1 is no render
     */
    public int getRenderType()
    {
        return 1;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return null;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    protected Vec3 getFlowVector(IBlockAccess worldIn, BlockPos pos)
    {
        Vec3 vec3 = new Vec3(0.0D, 0.0D, 0.0D);
        int i = this.getEffectiveFlowDecay(worldIn, pos);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
        {
            BlockPos blockpos = pos.offset(enumfacing);
            int j = this.getEffectiveFlowDecay(worldIn, blockpos);

            if (j < 0)
            {
                if (!worldIn.getBlockState(blockpos).getBlock().getMaterial().blocksMovement())
                {
                    j = this.getEffectiveFlowDecay(worldIn, blockpos.down());

                    if (j >= 0)
                    {
                        int k = j - (i - 8);
                        vec3 = vec3.addVector((double)((blockpos.getX() - pos.getX()) * k), (double)((blockpos.getY() - pos.getY()) * k), (double)((blockpos.getZ() - pos.getZ()) * k));
                    }
                }
            }
            else if (j >= 0)
            {
                int l = j - i;
                vec3 = vec3.addVector((double)((blockpos.getX() - pos.getX()) * l), (double)((blockpos.getY() - pos.getY()) * l), (double)((blockpos.getZ() - pos.getZ()) * l));
            }
        }

        if (((Integer)worldIn.getBlockState(pos).getValue(LEVEL)).intValue() >= 8)
        {
            for (EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos blockpos1 = pos.offset(enumfacing1);

                if (this.isBlockSolid(worldIn, blockpos1, enumfacing1) || this.isBlockSolid(worldIn, blockpos1.up(), enumfacing1))
                {
                    vec3 = vec3.normalize().addVector(0.0D, -6.0D, 0.0D);
                    break;
                }
            }
        }

        return vec3.normalize();
    }

    public Vec3 modifyAcceleration(World worldIn, BlockPos pos, Entity entityIn, Vec3 motion)
    {
        return motion.add(this.getFlowVector(worldIn, pos));
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(World worldIn)
    {
        return this.blockMaterial == Material.water ? 5 : (this.blockMaterial == Material.lava ? (worldIn.provider.getHasNoSky() ? 10 : 30) : 0);
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.checkForMixing(worldIn, pos, state);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        this.checkForMixing(worldIn, pos, state);
    }

    public boolean checkForMixing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (this.blockMaterial == Material.lava)
        {
            boolean flag = false;

            for (EnumFacing enumfacing : EnumFacing.values())
            {
                if (enumfacing != EnumFacing.DOWN && worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getMaterial() == Material.water)
                {
                    flag = true;
                    break;
                }
            }

            if (flag)
            {
                Integer integer = (Integer)state.getValue(LEVEL);

                if (integer.intValue() == 0)
                {
                    worldIn.setBlockState(pos, Blocks.obsidian.getDefaultState());
                    this.triggerMixEffects(worldIn, pos);
                    return true;
                }

                if (integer.intValue() <= 4)
                {
                    worldIn.setBlockState(pos, Blocks.cobblestone.getDefaultState());
                    this.triggerMixEffects(worldIn, pos);
                    return true;
                }
            }
        }

        return false;
    }

    protected void triggerMixEffects(World worldIn, BlockPos pos)
    {
        double d0 = (double)pos.getX();
        double d1 = (double)pos.getY();
        double d2 = (double)pos.getZ();
        worldIn.playSoundEffect(d0 + 0.5D, d1 + 0.5D, d2 + 0.5D, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i)
        {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LEVEL, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((Integer)state.getValue(LEVEL)).intValue();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {LEVEL});
    }

    public static BlockDynamicLiquid getFlowingBlock(Material materialIn)
    {
        if (materialIn == Material.water)
        {
            return Blocks.flowing_water;
        }
        else if (materialIn == Material.lava)
        {
            return Blocks.flowing_lava;
        }
        else
        {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    public static BlockStaticLiquid getStaticBlock(Material materialIn)
    {
        if (materialIn == Material.water)
        {
            return Blocks.water;
        }
        else if (materialIn == Material.lava)
        {
            return Blocks.lava;
        }
        else
        {
            throw new IllegalArgumentException("Invalid material");
        }
    }
}
