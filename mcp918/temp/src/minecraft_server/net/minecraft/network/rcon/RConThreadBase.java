package net.minecraft.network.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.network.rcon.IServer;

public abstract class RConThreadBase implements Runnable {
   private static final AtomicInteger field_164004_h = new AtomicInteger(0);
   protected boolean field_72619_a;
   protected IServer field_72617_b;
   protected final String field_164003_c;
   protected Thread field_72618_c;
   protected int field_72615_d = 5;
   protected List<DatagramSocket> field_72616_e = Lists.<DatagramSocket>newArrayList();
   protected List<ServerSocket> field_72614_f = Lists.<ServerSocket>newArrayList();

   protected RConThreadBase(IServer p_i45300_1_, String p_i45300_2_) {
      this.field_72617_b = p_i45300_1_;
      this.field_164003_c = p_i45300_2_;
      if(this.field_72617_b.func_71239_B()) {
         this.func_72606_c("Debugging is enabled, performance maybe reduced!");
      }

   }

   public synchronized void func_72602_a() {
      this.field_72618_c = new Thread(this, this.field_164003_c + " #" + field_164004_h.incrementAndGet());
      this.field_72618_c.start();
      this.field_72619_a = true;
   }

   public boolean func_72613_c() {
      return this.field_72619_a;
   }

   protected void func_72607_a(String p_72607_1_) {
      this.field_72617_b.func_71198_k(p_72607_1_);
   }

   protected void func_72609_b(String p_72609_1_) {
      this.field_72617_b.func_71244_g(p_72609_1_);
   }

   protected void func_72606_c(String p_72606_1_) {
      this.field_72617_b.func_71236_h(p_72606_1_);
   }

   protected void func_72610_d(String p_72610_1_) {
      this.field_72617_b.func_71201_j(p_72610_1_);
   }

   protected int func_72603_d() {
      return this.field_72617_b.func_71233_x();
   }

   protected void func_72601_a(DatagramSocket p_72601_1_) {
      this.func_72607_a("registerSocket: " + p_72601_1_);
      this.field_72616_e.add(p_72601_1_);
   }

   protected boolean func_72604_a(DatagramSocket p_72604_1_, boolean p_72604_2_) {
      this.func_72607_a("closeSocket: " + p_72604_1_);
      if(null == p_72604_1_) {
         return false;
      } else {
         boolean flag = false;
         if(!p_72604_1_.isClosed()) {
            p_72604_1_.close();
            flag = true;
         }

         if(p_72604_2_) {
            this.field_72616_e.remove(p_72604_1_);
         }

         return flag;
      }
   }

   protected boolean func_72608_b(ServerSocket p_72608_1_) {
      return this.func_72605_a(p_72608_1_, true);
   }

   protected boolean func_72605_a(ServerSocket p_72605_1_, boolean p_72605_2_) {
      this.func_72607_a("closeSocket: " + p_72605_1_);
      if(null == p_72605_1_) {
         return false;
      } else {
         boolean flag = false;

         try {
            if(!p_72605_1_.isClosed()) {
               p_72605_1_.close();
               flag = true;
            }
         } catch (IOException ioexception) {
            this.func_72606_c("IO: " + ioexception.getMessage());
         }

         if(p_72605_2_) {
            this.field_72614_f.remove(p_72605_1_);
         }

         return flag;
      }
   }

   protected void func_72611_e() {
      this.func_72612_a(false);
   }

   protected void func_72612_a(boolean p_72612_1_) {
      int i = 0;

      for(DatagramSocket datagramsocket : this.field_72616_e) {
         if(this.func_72604_a(datagramsocket, false)) {
            ++i;
         }
      }

      this.field_72616_e.clear();

      for(ServerSocket serversocket : this.field_72614_f) {
         if(this.func_72605_a(serversocket, false)) {
            ++i;
         }
      }

      this.field_72614_f.clear();
      if(p_72612_1_ && 0 < i) {
         this.func_72606_c("Force closed " + i + " sockets");
      }

   }
}
