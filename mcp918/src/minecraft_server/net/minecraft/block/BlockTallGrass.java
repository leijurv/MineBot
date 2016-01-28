package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockTallGrass extends BlockBush implements IGrowable
{
    public static final PropertyEnum<BlockTallGrass.EnumType> TYPE = PropertyEnum.<BlockTallGrass.EnumType>create("type", BlockTallGrass.EnumType.class);

    protected BlockTallGrass()
    {
        super(Material.vine);
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockTallGrass.EnumType.DEAD_BUSH));
        float f = 0.4F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
    }

    public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state)
    {
        return this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
    }

    /**
     * Whether this Block can be replaced directly by other blocks (true for e.g. tall grass)
     */
    public boolean isReplaceable(World worldIn, BlockPos pos)
    {
        return true;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return rand.nextInt(8) == 0 ? Items.wheat_seeds : null;
    }

    /**
     * Get the quantity dropped based on the given fortune level
     */
    public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return 1 + random.nextInt(fortune * 2 + 1);
    }

    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te)
    {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears)
        {
            player.triggerAchievement(StatList.mineBlockStatArray[Block.getIdFromBlock(this)]);
            spawnAsEntity(worldIn, pos, new ItemStack(Blocks.tallgrass, 1, ((BlockTallGrass.EnumType)state.getValue(TYPE)).getMeta()));
        }
        else
        {
            super.harvestBlock(worldIn, player, pos, state, te);
        }
    }

    public int getDamageValue(World worldIn, BlockPos pos)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock().getMetaFromState(iblockstate);
    }

    /**
     * Whether this IGrowable can grow
     */
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        return state.getValue(TYPE) != BlockTallGrass.EnumType.DEAD_BUSH;
    }

    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        return true;
    }

    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        BlockDoublePlant.EnumPlantType blockdoubleplant$enumplanttype = BlockDoublePlant.EnumPlantType.GRASS;

        if (state.getValue(TYPE) == BlockTallGrass.EnumType.FERN)
        {
            blockdoubleplant$enumplanttype = BlockDoublePlant.EnumPlantType.FERN;
        }

        if (Blocks.double_plant.canPlaceBlockAt(worldIn, pos))
        {
            Blocks.double_plant.placeAt(worldIn, pos, blockdoubleplant$enumplanttype, 2);
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(TYPE, BlockTallGrass.EnumType.byMetadata(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((BlockTallGrass.EnumType)state.getValue(TYPE)).getMeta();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {TYPE});
    }

    public static enum EnumType implements IStringSerializable
    {
        DEAD_BUSH(0, "dead_bush"),
        GRASS(1, "tall_grass"),
        FERN(2, "fern");

        private static final BlockTallGrass.EnumType[] META_LOOKUP = new BlockTallGrass.EnumType[values().length];
        private final int meta;
        private final String name;

        private EnumType(int meta, String name)
        {
            this.meta = meta;
            this.name = name;
        }

        public int getMeta()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockTallGrass.EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        static {
            for (BlockTallGrass.EnumType blocktallgrass$enumtype : values())
            {
                META_LOOKUP[blocktallgrass$enumtype.getMeta()] = blocktallgrass$enumtype;
            }
        }
    }
}
