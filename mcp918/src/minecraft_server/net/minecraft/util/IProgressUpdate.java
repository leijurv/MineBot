package net.minecraft.util;

public interface IProgressUpdate
{
    /**
     * Shows the 'Saving level' string.
     */
    void displaySavingString(String message);

    /**
     * Displays a string on the loading screen supposed to indicate what is being done currently.
     */
    void displayLoadingString(String message);

    /**
     * Updates the progress bar on the loading screen to the specified amount. Args: loadProgress
     */
    void setLoadingProgress(int progress);
}
