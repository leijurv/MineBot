package net.minecraft.server.dedicated;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerHangWatchdog implements Runnable {
   private static final Logger field_180251_a = LogManager.getLogger();
   private final DedicatedServer field_180249_b;
   private final long field_180250_c;

   public ServerHangWatchdog(DedicatedServer p_i46310_1_) {
      this.field_180249_b = p_i46310_1_;
      this.field_180250_c = p_i46310_1_.func_175593_aQ();
   }

   public void run() {
      while(this.field_180249_b.func_71278_l()) {
         long i = this.field_180249_b.func_175587_aJ();
         long j = MinecraftServer.func_130071_aq();
         long k = j - i;
         if(k > this.field_180250_c) {
            field_180251_a.fatal("A single server tick took " + String.format("%.2f", new Object[]{Float.valueOf((float)k / 1000.0F)}) + " seconds (should be max " + String.format("%.2f", new Object[]{Float.valueOf(0.05F)}) + ")");
            field_180251_a.fatal("Considering it to be crashed, server will forcibly shutdown.");
            ThreadMXBean threadmxbean = ManagementFactory.getThreadMXBean();
            ThreadInfo[] athreadinfo = threadmxbean.dumpAllThreads(true, true);
            StringBuilder stringbuilder = new StringBuilder();
            Error error = new Error();

            for(ThreadInfo threadinfo : athreadinfo) {
               if(threadinfo.getThreadId() == this.field_180249_b.func_175583_aK().getId()) {
                  error.setStackTrace(threadinfo.getStackTrace());
               }

               stringbuilder.append((Object)threadinfo);
               stringbuilder.append("\n");
            }

            CrashReport crashreport = new CrashReport("Watching Server", error);
            this.field_180249_b.func_71230_b(crashreport);
            CrashReportCategory crashreportcategory = crashreport.func_85058_a("Thread Dump");
            crashreportcategory.func_71507_a("Threads", stringbuilder);
            File file1 = new File(new File(this.field_180249_b.func_71238_n(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
            if(crashreport.func_147149_a(file1)) {
               field_180251_a.error("This crash report has been saved to: " + file1.getAbsolutePath());
            } else {
               field_180251_a.error("We were unable to save this crash report to disk.");
            }

            this.func_180248_a();
         }

         try {
            Thread.sleep(i + this.field_180250_c - j);
         } catch (InterruptedException var15) {
            ;
         }
      }

   }

   private void func_180248_a() {
      try {
         Timer timer = new Timer();
         timer.schedule(new TimerTask() {
            public void run() {
               Runtime.getRuntime().halt(1);
            }
         }, 10000L);
         System.exit(1);
      } catch (Throwable var2) {
         Runtime.getRuntime().halt(1);
      }

   }
}
