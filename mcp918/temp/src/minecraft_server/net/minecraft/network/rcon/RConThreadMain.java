package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConThreadBase;
import net.minecraft.network.rcon.RConThreadClient;

public class RConThreadMain extends RConThreadBase {
   private int field_72647_g;
   private int field_72651_h;
   private String field_72652_i;
   private ServerSocket field_72649_j;
   private String field_72650_k;
   private Map<SocketAddress, RConThreadClient> field_72648_l;

   public RConThreadMain(IServer p_i1538_1_) {
      super(p_i1538_1_, "RCON Listener");
      this.field_72647_g = p_i1538_1_.func_71327_a("rcon.port", 0);
      this.field_72650_k = p_i1538_1_.func_71330_a("rcon.password", "");
      this.field_72652_i = p_i1538_1_.func_71277_t();
      this.field_72651_h = p_i1538_1_.func_71234_u();
      if(0 == this.field_72647_g) {
         this.field_72647_g = this.field_72651_h + 10;
         this.func_72609_b("Setting default rcon port to " + this.field_72647_g);
         p_i1538_1_.func_71328_a("rcon.port", Integer.valueOf(this.field_72647_g));
         if(0 == this.field_72650_k.length()) {
            p_i1538_1_.func_71328_a("rcon.password", "");
         }

         p_i1538_1_.func_71326_a();
      }

      if(0 == this.field_72652_i.length()) {
         this.field_72652_i = "0.0.0.0";
      }

      this.func_72646_f();
      this.field_72649_j = null;
   }

   private void func_72646_f() {
      this.field_72648_l = Maps.<SocketAddress, RConThreadClient>newHashMap();
   }

   private void func_72645_g() {
      Iterator<Entry<SocketAddress, RConThreadClient>> iterator = this.field_72648_l.entrySet().iterator();

      while(iterator.hasNext()) {
         Entry<SocketAddress, RConThreadClient> entry = (Entry)iterator.next();
         if(!((RConThreadClient)entry.getValue()).func_72613_c()) {
            iterator.remove();
         }
      }

   }

   public void run() {
      this.func_72609_b("RCON running on " + this.field_72652_i + ":" + this.field_72647_g);

      try {
         while(this.field_72619_a) {
            try {
               Socket socket = this.field_72649_j.accept();
               socket.setSoTimeout(500);
               RConThreadClient rconthreadclient = new RConThreadClient(this.field_72617_b, socket);
               rconthreadclient.func_72602_a();
               this.field_72648_l.put(socket.getRemoteSocketAddress(), rconthreadclient);
               this.func_72645_g();
            } catch (SocketTimeoutException var7) {
               this.func_72645_g();
            } catch (IOException ioexception) {
               if(this.field_72619_a) {
                  this.func_72609_b("IO: " + ioexception.getMessage());
               }
            }
         }
      } finally {
         this.func_72608_b(this.field_72649_j);
      }

   }

   public void func_72602_a() {
      if(0 == this.field_72650_k.length()) {
         this.func_72606_c("No rcon password set in \'" + this.field_72617_b.func_71329_c() + "\', rcon disabled!");
      } else if(0 < this.field_72647_g && '\uffff' >= this.field_72647_g) {
         if(!this.field_72619_a) {
            try {
               this.field_72649_j = new ServerSocket(this.field_72647_g, 0, InetAddress.getByName(this.field_72652_i));
               this.field_72649_j.setSoTimeout(500);
               super.func_72602_a();
            } catch (IOException ioexception) {
               this.func_72606_c("Unable to initialise rcon on " + this.field_72652_i + ":" + this.field_72647_g + " : " + ioexception.getMessage());
            }

         }
      } else {
         this.func_72606_c("Invalid rcon port " + this.field_72647_g + " found in \'" + this.field_72617_b.func_71329_c() + "\', rcon disabled!");
      }
   }
}
