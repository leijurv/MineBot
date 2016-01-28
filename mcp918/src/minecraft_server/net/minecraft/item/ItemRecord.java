package net.minecraft.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemRecord extends Item
{
    private static final Map<String, ItemRecord> RECORDS = Maps.<String, ItemRecord>newHashMap();

    /** The name of the record. */
    public final String recordName;

    protected ItemRecord(String name)
    {
        this.recordName = name;
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabMisc);
        RECORDS.put("records." + name, this);
    }

    /**
     * Called when a Block is right-clicked with this Item
     */
    public boolean onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if (iblockstate.getBlock() == Blocks.jukebox && !((Boolean)iblockstate.getValue(BlockJukebox.HAS_RECORD)).booleanValue())
        {
            if (worldIn.isRemote)
            {
                return true;
            }
            else
            {
                ((BlockJukebox)Blocks.jukebox).insertRecord(worldIn, pos, iblockstate, stack);
                worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1005, pos, Item.getIdFromItem(this));
                --stack.stackSize;
                playerIn.triggerAchievement(StatList.field_181740_X);
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack stack)
    {
        return EnumRarity.RARE;
    }
}
