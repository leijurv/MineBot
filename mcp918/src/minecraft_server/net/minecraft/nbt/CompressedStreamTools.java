package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools
{
    /**
     * Load the gzipped compound from the inputstream.
     */
    public static NBTTagCompound readCompressed(InputStream is) throws IOException
    {
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(is)));
        NBTTagCompound nbttagcompound;

        try
        {
            nbttagcompound = read(datainputstream, NBTSizeTracker.INFINITE);
        }
        finally
        {
            datainputstream.close();
        }

        return nbttagcompound;
    }

    /**
     * Write the compound, gzipped, to the outputstream.
     */
    public static void writeCompressed(NBTTagCompound p_74799_0_, OutputStream outputStream) throws IOException
    {
        DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));

        try
        {
            write(p_74799_0_, dataoutputstream);
        }
        finally
        {
            dataoutputstream.close();
        }
    }

    /**
     * Reads from a CompressedStream.
     */
    public static NBTTagCompound read(DataInputStream inputStream) throws IOException
    {
        return read(inputStream, NBTSizeTracker.INFINITE);
    }

    /**
     * Reads the given DataInput, constructs, and returns an NBTTagCompound with the data from the DataInput
     */
    public static NBTTagCompound read(DataInput p_152456_0_, NBTSizeTracker p_152456_1_) throws IOException
    {
        NBTBase nbtbase = func_152455_a(p_152456_0_, 0, p_152456_1_);

        if (nbtbase instanceof NBTTagCompound)
        {
            return (NBTTagCompound)nbtbase;
        }
        else
        {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound p_74800_0_, DataOutput p_74800_1_) throws IOException
    {
        writeTag(p_74800_0_, p_74800_1_);
    }

    private static void writeTag(NBTBase p_150663_0_, DataOutput p_150663_1_) throws IOException
    {
        p_150663_1_.writeByte(p_150663_0_.getId());

        if (p_150663_0_.getId() != 0)
        {
            p_150663_1_.writeUTF("");
            p_150663_0_.write(p_150663_1_);
        }
    }

    private static NBTBase func_152455_a(DataInput p_152455_0_, int p_152455_1_, NBTSizeTracker p_152455_2_) throws IOException
    {
        byte b0 = p_152455_0_.readByte();

        if (b0 == 0)
        {
            return new NBTTagEnd();
        }
        else
        {
            p_152455_0_.readUTF();
            NBTBase nbtbase = NBTBase.createNewByType(b0);

            try
            {
                nbtbase.read(p_152455_0_, p_152455_1_, p_152455_2_);
                return nbtbase;
            }
            catch (IOException ioexception)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(ioexception, "Loading NBT data");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("NBT Tag");
                crashreportcategory.addCrashSection("Tag name", "[UNNAMED TAG]");
                crashreportcategory.addCrashSection("Tag type", Byte.valueOf(b0));
                throw new ReportedException(crashreport);
            }
        }
    }
}
