package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode
{
    public static final PropertyBool LOCKED = PropertyBool.create("locked");
    public static final PropertyInteger DELAY = PropertyInteger.create("delay", 1, 4);

    protected BlockRedstoneRepeater(boolean powered)
    {
        super(powered);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DELAY, Integer.valueOf(1)).withProperty(LOCKED, Boolean.valueOf(false)));
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName()
    {
        return StatCollector.translateToLocal("item.diode.name");
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return state.withProperty(LOCKED, Boolean.valueOf(this.isLocked(worldIn, pos, state)));
    }

    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!playerIn.capabilities.allowEdit)
        {
            return false;
        }
        else
        {
            worldIn.setBlockState(pos, state.cycleProperty(DELAY), 3);
            return true;
        }
    }

    protected int getDelay(IBlockState state)
    {
        return ((Integer)state.getValue(DELAY)).intValue() * 2;
    }

    protected IBlockState getPoweredState(IBlockState unpoweredState)
    {
        Integer integer = (Integer)unpoweredState.getValue(DELAY);
        Boolean obool = (Boolean)unpoweredState.getValue(LOCKED);
        EnumFacing enumfacing = (EnumFacing)unpoweredState.getValue(FACING);
        return Blocks.powered_repeater.getDefaultState().withProperty(FACING, enumfacing).withProperty(DELAY, integer).withProperty(LOCKED, obool);
    }

    protected IBlockState getUnpoweredState(IBlockState poweredState)
    {
        Integer integer = (Integer)poweredState.getValue(DELAY);
        Boolean obool = (Boolean)poweredState.getValue(LOCKED);
        EnumFacing enumfacing = (EnumFacing)poweredState.getValue(FACING);
        return Blocks.unpowered_repeater.getDefaultState().withProperty(FACING, enumfacing).withProperty(DELAY, integer).withProperty(LOCKED, obool);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.repeater;
    }

    public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state)
    {
        return this.getPowerOnSides(worldIn, pos, state) > 0;
    }

    protected boolean canPowerSide(Block blockIn)
    {
        return isRedstoneRepeaterBlockID(blockIn);
    }

    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        this.notifyNeighbors(worldIn, pos, state);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(LOCKED, Boolean.valueOf(false)).withProperty(DELAY, Integer.valueOf(1 + (meta >> 2)));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getHorizontalIndex();
        i = i | ((Integer)state.getValue(DELAY)).intValue() - 1 << 2;
        return i;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING, DELAY, LOCKED});
    }
}
