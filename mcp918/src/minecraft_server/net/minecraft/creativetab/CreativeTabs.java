package net.minecraft.creativetab;

import net.minecraft.enchantment.EnumEnchantmentType;

public abstract class CreativeTabs
{
    public static final CreativeTabs[] creativeTabArray = new CreativeTabs[12];
    public static final CreativeTabs tabBlock = new CreativeTabs(0, "buildingBlocks")
    {
    };
    public static final CreativeTabs tabDecorations = new CreativeTabs(1, "decorations")
    {
    };
    public static final CreativeTabs tabRedstone = new CreativeTabs(2, "redstone")
    {
    };
    public static final CreativeTabs tabTransport = new CreativeTabs(3, "transportation")
    {
    };
    public static final CreativeTabs tabMisc = (new CreativeTabs(4, "misc")
    {
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.ALL});
    public static final CreativeTabs tabAllSearch = (new CreativeTabs(5, "search")
    {
    }).setBackgroundImageName("item_search.png");
    public static final CreativeTabs tabFood = new CreativeTabs(6, "food")
    {
    };
    public static final CreativeTabs tabTools = (new CreativeTabs(7, "tools")
    {
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE});
    public static final CreativeTabs tabCombat = (new CreativeTabs(8, "combat")
    {
    }).setRelevantEnchantmentTypes(new EnumEnchantmentType[] {EnumEnchantmentType.ARMOR, EnumEnchantmentType.ARMOR_FEET, EnumEnchantmentType.ARMOR_HEAD, EnumEnchantmentType.ARMOR_LEGS, EnumEnchantmentType.ARMOR_TORSO, EnumEnchantmentType.BOW, EnumEnchantmentType.WEAPON});
    public static final CreativeTabs tabBrewing = new CreativeTabs(9, "brewing")
    {
    };
    public static final CreativeTabs tabMaterials = new CreativeTabs(10, "materials")
    {
    };
    public static final CreativeTabs tabInventory = (new CreativeTabs(11, "inventory")
    {
    }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
    private final int tabIndex;
    private final String tabLabel;

    /** Texture to use. */
    private String theTexture = "items.png";
    private boolean hasScrollbar = true;

    /** Whether to draw the title in the foreground of the creative GUI */
    private boolean drawTitle = true;
    private EnumEnchantmentType[] enchantmentTypes;

    public CreativeTabs(int index, String label)
    {
        this.tabIndex = index;
        this.tabLabel = label;
        creativeTabArray[index] = this;
    }

    public CreativeTabs setBackgroundImageName(String texture)
    {
        this.theTexture = texture;
        return this;
    }

    public CreativeTabs setNoTitle()
    {
        this.drawTitle = false;
        return this;
    }

    public CreativeTabs setNoScrollbar()
    {
        this.hasScrollbar = false;
        return this;
    }

    /**
     * Sets the enchantment types for populating this tab with enchanting books
     */
    public CreativeTabs setRelevantEnchantmentTypes(EnumEnchantmentType... types)
    {
        this.enchantmentTypes = types;
        return this;
    }
}
