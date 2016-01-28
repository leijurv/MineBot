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
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools {
   public static NBTTagCompound func_74796_a(InputStream p_74796_0_) throws IOException {
      DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(p_74796_0_)));

      NBTTagCompound nbttagcompound;
      try {
         nbttagcompound = func_152456_a(datainputstream, NBTSizeTracker.field_152451_a);
      } finally {
         datainputstream.close();
      }

      return nbttagcompound;
   }

   public static void func_74799_a(NBTTagCompound p_74799_0_, OutputStream p_74799_1_) throws IOException {
      DataOutputStream dataoutputstream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(p_74799_1_)));

      try {
         func_74800_a(p_74799_0_, dataoutputstream);
      } finally {
         dataoutputstream.close();
      }

   }

   public static NBTTagCompound func_74794_a(DataInputStream p_74794_0_) throws IOException {
      return func_152456_a(p_74794_0_, NBTSizeTracker.field_152451_a);
   }

   public static NBTTagCompound func_152456_a(DataInput p_152456_0_, NBTSizeTracker p_152456_1_) throws IOException {
      NBTBase nbtbase = func_152455_a(p_152456_0_, 0, p_152456_1_);
      if(nbtbase instanceof NBTTagCompound) {
         return (NBTTagCompound)nbtbase;
      } else {
         throw new IOException("Root tag must be a named compound tag");
      }
   }

   public static void func_74800_a(NBTTagCompound p_74800_0_, DataOutput p_74800_1_) throws IOException {
      func_150663_a(p_74800_0_, p_74800_1_);
   }

   private static void func_150663_a(NBTBase p_150663_0_, DataOutput p_150663_1_) throws IOException {
      p_150663_1_.writeByte(p_150663_0_.func_74732_a());
      if(p_150663_0_.func_74732_a() != 0) {
         p_150663_1_.writeUTF("");
         p_150663_0_.func_74734_a(p_150663_1_);
      }
   }

   private static NBTBase func_152455_a(DataInput p_152455_0_, int p_152455_1_, NBTSizeTracker p_152455_2_) throws IOException {
      byte b0 = p_152455_0_.readByte();
      if(b0 == 0) {
         return new NBTTagEnd();
      } else {
         p_152455_0_.readUTF();
         NBTBase nbtbase = NBTBase.func_150284_a(b0);

         try {
            nbtbase.func_152446_a(p_152455_0_, p_152455_1_, p_152455_2_);
            return nbtbase;
         } catch (IOException ioexception) {
            CrashReport crashreport = CrashReport.func_85055_a(ioexception, "Loading NBT data");
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("NBT Tag");
            crashreportcategory.func_71507_a("Tag name", "[UNNAMED TAG]");
            crashreportcategory.func_71507_a("Tag type", Byte.valueOf(b0));
            throw new ReportedException(crashreport);
         }
      }
   }
}
