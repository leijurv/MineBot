package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderDebug;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider
{
    public static final float[] moonPhaseFactors = new float[] {1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};

    /** world object being used */
    protected World worldObj;
    private WorldType terrainType;
    private String generatorSettings;

    /** World chunk manager being used to generate chunks */
    protected WorldChunkManager worldChunkMgr;

    /**
     * States whether the Hell world provider is used(true) or if the normal world provider is used(false)
     */
    protected boolean isHellWorld;

    /**
     * A boolean that tells if a world does not have a sky. Used in calculating weather and skylight
     */
    protected boolean hasNoSky;

    /** Light to brightness conversion table */
    protected final float[] lightBrightnessTable = new float[16];

    /** The id for the dimension (ex. -1: Nether, 0: Overworld, 1: The End) */
    protected int dimensionId;

    /** Array for sunrise/sunset colors (RGBA) */
    private final float[] colorsSunriseSunset = new float[4];

    /**
     * associate an existing world with a World provider, and setup its lightbrightness table
     */
    public final void registerWorld(World worldIn)
    {
        this.worldObj = worldIn;
        this.terrainType = worldIn.getWorldInfo().getTerrainType();
        this.generatorSettings = worldIn.getWorldInfo().getGeneratorOptions();
        this.registerWorldChunkManager();
        this.generateLightBrightnessTable();
    }

    /**
     * Creates the light to brightness table
     */
    protected void generateLightBrightnessTable()
    {
        float f = 0.0F;

        for (int i = 0; i <= 15; ++i)
        {
            float f1 = 1.0F - (float)i / 15.0F;
            this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
        }
    }

    /**
     * creates a new world chunk manager for WorldProvider
     */
    protected void registerWorldChunkManager()
    {
        WorldType worldtype = this.worldObj.getWorldInfo().getTerrainType();

        if (worldtype == WorldType.FLAT)
        {
            FlatGeneratorInfo flatgeneratorinfo = FlatGeneratorInfo.createFlatGeneratorFromString(this.worldObj.getWorldInfo().getGeneratorOptions());
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.getBiomeFromBiomeList(flatgeneratorinfo.getBiome(), BiomeGenBase.field_180279_ad), 0.5F);
        }
        else if (worldtype == WorldType.DEBUG_WORLD)
        {
            this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.plains, 0.0F);
        }
        else
        {
            this.worldChunkMgr = new WorldChunkManager(this.worldObj);
        }
    }

    /**
     * Returns a new chunk provider which generates chunks for this world
     */
    public IChunkProvider createChunkGenerator()
    {
        return (IChunkProvider)(this.terrainType == WorldType.FLAT ? new ChunkProviderFlat(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings) : (this.terrainType == WorldType.DEBUG_WORLD ? new ChunkProviderDebug(this.worldObj) : (this.terrainType == WorldType.CUSTOMIZED ? new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings) : new ChunkProviderGenerate(this.worldObj, this.worldObj.getSeed(), this.worldObj.getWorldInfo().isMapFeaturesEnabled(), this.generatorSettings))));
    }

    /**
     * Will check if the x, z position specified is alright to be set as the map spawn point
     */
    public boolean canCoordinateBeSpawn(int x, int z)
    {
        return this.worldObj.getGroundAboveSeaLevel(new BlockPos(x, 0, z)) == Blocks.grass;
    }

    /**
     * Calculates the angle of sun and moon in the sky relative to a specified time (usually worldTime)
     */
    public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
    {
        int i = (int)(p_76563_1_ % 24000L);
        float f = ((float)i + p_76563_3_) / 24000.0F - 0.25F;

        if (f < 0.0F)
        {
            ++f;
        }

        if (f > 1.0F)
        {
            --f;
        }

        f = 1.0F - (float)((Math.cos((double)f * Math.PI) + 1.0D) / 2.0D);
        f = f + (f - f) / 3.0F;
        return f;
    }

    public int getMoonPhase(long p_76559_1_)
    {
        return (int)(p_76559_1_ / 24000L % 8L + 8L) % 8;
    }

    /**
     * Returns 'true' if in the "main surface world", but 'false' if in the Nether or End dimensions.
     */
    public boolean isSurfaceWorld()
    {
        return true;
    }

    /**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere()
    {
        return true;
    }

    public static WorldProvider getProviderForDimension(int dimension)
    {
        return (WorldProvider)(dimension == -1 ? new WorldProviderHell() : (dimension == 0 ? new WorldProviderSurface() : (dimension == 1 ? new WorldProviderEnd() : null)));
    }

    public BlockPos getSpawnCoordinate()
    {
        return null;
    }

    public int getAverageGroundLevel()
    {
        return this.terrainType == WorldType.FLAT ? 4 : this.worldObj.func_181545_F() + 1;
    }

    /**
     * Returns the dimension's name, e.g. "The End", "Nether", or "Overworld".
     */
    public abstract String getDimensionName();

    public abstract String getInternalNameSuffix();

    public WorldChunkManager getWorldChunkManager()
    {
        return this.worldChunkMgr;
    }

    public boolean doesWaterVaporize()
    {
        return this.isHellWorld;
    }

    public boolean getHasNoSky()
    {
        return this.hasNoSky;
    }

    public float[] getLightBrightnessTable()
    {
        return this.lightBrightnessTable;
    }

    /**
     * Gets the dimension of the provider
     */
    public int getDimensionId()
    {
        return this.dimensionId;
    }

    public WorldBorder getWorldBorder()
    {
        return new WorldBorder();
    }
}
