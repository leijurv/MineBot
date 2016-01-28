package net.minecraft.world.storage;

import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.storage.ISaveHandler;

public interface ISaveFormat {
   ISaveHandler func_75804_a(String var1, boolean var2);

   void func_75800_d();

   boolean func_75802_e(String var1);

   boolean func_75801_b(String var1);

   boolean func_75805_a(String var1, IProgressUpdate var2);
}
