package net.minecraft.world.storage;

import net.minecraft.util.IProgressUpdate;

public interface ISaveFormat
{
    /**
     * Returns back a loader for the specified save directory
     */
    ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata);

    void flushCache();

    /**
     * @args: Takes one argument - the name of the directory of the world to delete. @desc: Delete the world by deleting
     * the associated directory recursively.
     */
    boolean deleteWorldDirectory(String p_75802_1_);

    /**
     * gets if the map is old chunk saving (true) or McRegion (false)
     */
    boolean isOldMapFormat(String saveName);

    /**
     * converts the map to mcRegion
     */
    boolean convertMapFormat(String filename, IProgressUpdate progressCallback);
}
