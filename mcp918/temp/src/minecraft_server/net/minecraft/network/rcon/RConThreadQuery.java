package net.minecraft.network.rcon;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.network.rcon.IServer;
import net.minecraft.network.rcon.RConOutputStream;
import net.minecraft.network.rcon.RConThreadBase;
import net.minecraft.network.rcon.RConUtils;
import net.minecraft.server.MinecraftServer;

public class RConThreadQuery extends RConThreadBase {
   private long field_72629_g;
   private int field_72636_h;
   private int field_72637_i;
   private int field_72634_j;
   private String field_72635_k;
   private String field_72632_l;
   private DatagramSocket field_72633_m;
   private byte[] field_72630_n = new byte[1460];
   private DatagramPacket field_72631_o;
   private Map<SocketAddress, String> field_72644_p;
   private String field_72643_q;
   private String field_72642_r;
   private Map<SocketAddress, RConThreadQuery.Auth> field_72641_s;
   private long field_72640_t;
   private RConOutputStream field_72639_u;
   private long field_72638_v;

   public RConThreadQuery(IServer p_i1536_1_) {
      super(p_i1536_1_, "Query Listener");
      this.field_72636_h = p_i1536_1_.func_71327_a("query.port", 0);
      this.field_72642_r = p_i1536_1_.func_71277_t();
      this.field_72637_i = p_i1536_1_.func_71234_u();
      this.field_72635_k = p_i1536_1_.func_71274_v();
      this.field_72634_j = p_i1536_1_.func_71275_y();
      this.field_72632_l = p_i1536_1_.func_71270_I();
      this.field_72638_v = 0L;
      this.field_72643_q = "0.0.0.0";
      if(0 != this.field_72642_r.length() && !this.field_72643_q.equals(this.field_72642_r)) {
         this.field_72643_q = this.field_72642_r;
      } else {
         this.field_72642_r = "0.0.0.0";

         try {
            InetAddress inetaddress = InetAddress.getLocalHost();
            this.field_72643_q = inetaddress.getHostAddress();
         } catch (UnknownHostException unknownhostexception) {
            this.func_72606_c("Unable to determine local host IP, please set server-ip in \'" + p_i1536_1_.func_71329_c() + "\' : " + unknownhostexception.getMessage());
         }
      }

      if(0 == this.field_72636_h) {
         this.field_72636_h = this.field_72637_i;
         this.func_72609_b("Setting default query port to " + this.field_72636_h);
         p_i1536_1_.func_71328_a("query.port", Integer.valueOf(this.field_72636_h));
         p_i1536_1_.func_71328_a("debug", Boolean.valueOf(false));
         p_i1536_1_.func_71326_a();
      }

      this.field_72644_p = Maps.<SocketAddress, String>newHashMap();
      this.field_72639_u = new RConOutputStream(1460);
      this.field_72641_s = Maps.<SocketAddress, RConThreadQuery.Auth>newHashMap();
      this.field_72640_t = (new Date()).getTime();
   }

   private void func_72620_a(byte[] p_72620_1_, DatagramPacket p_72620_2_) throws IOException {
      this.field_72633_m.send(new DatagramPacket(p_72620_1_, p_72620_1_.length, p_72620_2_.getSocketAddress()));
   }

   private boolean func_72621_a(DatagramPacket p_72621_1_) throws IOException {
      byte[] abyte = p_72621_1_.getData();
      int i = p_72621_1_.getLength();
      SocketAddress socketaddress = p_72621_1_.getSocketAddress();
      this.func_72607_a("Packet len " + i + " [" + socketaddress + "]");
      if(3 <= i && -2 == abyte[0] && -3 == abyte[1]) {
         this.func_72607_a("Packet \'" + RConUtils.func_72663_a(abyte[2]) + "\' [" + socketaddress + "]");
         switch(abyte[2]) {
         case 0:
            if(!this.func_72627_c(p_72621_1_).booleanValue()) {
               this.func_72607_a("Invalid challenge [" + socketaddress + "]");
               return false;
            } else if(15 == i) {
               this.func_72620_a(this.func_72624_b(p_72621_1_), p_72621_1_);
               this.func_72607_a("Rules [" + socketaddress + "]");
            } else {
               RConOutputStream rconoutputstream = new RConOutputStream(1460);
               rconoutputstream.func_72667_a(0);
               rconoutputstream.func_72670_a(this.func_72625_a(p_72621_1_.getSocketAddress()));
               rconoutputstream.func_72671_a(this.field_72635_k);
               rconoutputstream.func_72671_a("SMP");
               rconoutputstream.func_72671_a(this.field_72632_l);
               rconoutputstream.func_72671_a(Integer.toString(this.func_72603_d()));
               rconoutputstream.func_72671_a(Integer.toString(this.field_72634_j));
               rconoutputstream.func_72668_a((short)this.field_72637_i);
               rconoutputstream.func_72671_a(this.field_72643_q);
               this.func_72620_a(rconoutputstream.func_72672_a(), p_72621_1_);
               this.func_72607_a("Status [" + socketaddress + "]");
            }
         default:
            return true;
         case 9:
            this.func_72622_d(p_72621_1_);
            this.func_72607_a("Challenge [" + socketaddress + "]");
            return true;
         }
      } else {
         this.func_72607_a("Invalid packet [" + socketaddress + "]");
         return false;
      }
   }

   private byte[] func_72624_b(DatagramPacket p_72624_1_) throws IOException {
      long i = MinecraftServer.func_130071_aq();
      if(i < this.field_72638_v + 5000L) {
         byte[] abyte = this.field_72639_u.func_72672_a();
         byte[] abyte1 = this.func_72625_a(p_72624_1_.getSocketAddress());
         abyte[1] = abyte1[0];
         abyte[2] = abyte1[1];
         abyte[3] = abyte1[2];
         abyte[4] = abyte1[3];
         return abyte;
      } else {
         this.field_72638_v = i;
         this.field_72639_u.func_72669_b();
         this.field_72639_u.func_72667_a(0);
         this.field_72639_u.func_72670_a(this.func_72625_a(p_72624_1_.getSocketAddress()));
         this.field_72639_u.func_72671_a("splitnum");
         this.field_72639_u.func_72667_a(128);
         this.field_72639_u.func_72667_a(0);
         this.field_72639_u.func_72671_a("hostname");
         this.field_72639_u.func_72671_a(this.field_72635_k);
         this.field_72639_u.func_72671_a("gametype");
         this.field_72639_u.func_72671_a("SMP");
         this.field_72639_u.func_72671_a("game_id");
         this.field_72639_u.func_72671_a("MINECRAFT");
         this.field_72639_u.func_72671_a("version");
         this.field_72639_u.func_72671_a(this.field_72617_b.func_71249_w());
         this.field_72639_u.func_72671_a("plugins");
         this.field_72639_u.func_72671_a(this.field_72617_b.func_71258_A());
         this.field_72639_u.func_72671_a("map");
         this.field_72639_u.func_72671_a(this.field_72632_l);
         this.field_72639_u.func_72671_a("numplayers");
         this.field_72639_u.func_72671_a("" + this.func_72603_d());
         this.field_72639_u.func_72671_a("maxplayers");
         this.field_72639_u.func_72671_a("" + this.field_72634_j);
         this.field_72639_u.func_72671_a("hostport");
         this.field_72639_u.func_72671_a("" + this.field_72637_i);
         this.field_72639_u.func_72671_a("hostip");
         this.field_72639_u.func_72671_a(this.field_72643_q);
         this.field_72639_u.func_72667_a(0);
         this.field_72639_u.func_72667_a(1);
         this.field_72639_u.func_72671_a("player_");
         this.field_72639_u.func_72667_a(0);
         String[] astring = this.field_72617_b.func_71213_z();

         for(String s : astring) {
            this.field_72639_u.func_72671_a(s);
         }

         this.field_72639_u.func_72667_a(0);
         return this.field_72639_u.func_72672_a();
      }
   }

   private byte[] func_72625_a(SocketAddress p_72625_1_) {
      return ((RConThreadQuery.Auth)this.field_72641_s.get(p_72625_1_)).func_72591_c();
   }

   private Boolean func_72627_c(DatagramPacket p_72627_1_) {
      SocketAddress socketaddress = p_72627_1_.getSocketAddress();
      if(!this.field_72641_s.containsKey(socketaddress)) {
         return Boolean.valueOf(false);
      } else {
         byte[] abyte = p_72627_1_.getData();
         return ((RConThreadQuery.Auth)this.field_72641_s.get(socketaddress)).func_72592_a() != RConUtils.func_72664_c(abyte, 7, p_72627_1_.getLength())?Boolean.valueOf(false):Boolean.valueOf(true);
      }
   }

   private void func_72622_d(DatagramPacket p_72622_1_) throws IOException {
      RConThreadQuery.Auth rconthreadquery$auth = new RConThreadQuery.Auth(p_72622_1_);
      this.field_72641_s.put(p_72622_1_.getSocketAddress(), rconthreadquery$auth);
      this.func_72620_a(rconthreadquery$auth.func_72594_b(), p_72622_1_);
   }

   private void func_72628_f() {
      if(this.field_72619_a) {
         long i = MinecraftServer.func_130071_aq();
         if(i >= this.field_72629_g + 30000L) {
            this.field_72629_g = i;
            Iterator<Entry<SocketAddress, RConThreadQuery.Auth>> iterator = this.field_72641_s.entrySet().iterator();

            while(iterator.hasNext()) {
               Entry<SocketAddress, RConThreadQuery.Auth> entry = (Entry)iterator.next();
               if(((RConThreadQuery.Auth)entry.getValue()).func_72593_a(i).booleanValue()) {
                  iterator.remove();
               }
            }

         }
      }
   }

   public void run() {
      this.func_72609_b("Query running on " + this.field_72642_r + ":" + this.field_72636_h);
      this.field_72629_g = MinecraftServer.func_130071_aq();
      this.field_72631_o = new DatagramPacket(this.field_72630_n, this.field_72630_n.length);

      try {
         while(this.field_72619_a) {
            try {
               this.field_72633_m.receive(this.field_72631_o);
               this.func_72628_f();
               this.func_72621_a(this.field_72631_o);
            } catch (SocketTimeoutException var7) {
               this.func_72628_f();
            } catch (PortUnreachableException var8) {
               ;
            } catch (IOException ioexception) {
               this.func_72623_a(ioexception);
            }
         }
      } finally {
         this.func_72611_e();
      }

   }

   public void func_72602_a() {
      if(!this.field_72619_a) {
         if(0 < this.field_72636_h && '\uffff' >= this.field_72636_h) {
            if(this.func_72626_g()) {
               super.func_72602_a();
            }

         } else {
            this.func_72606_c("Invalid query port " + this.field_72636_h + " found in \'" + this.field_72617_b.func_71329_c() + "\' (queries disabled)");
         }
      }
   }

   private void func_72623_a(Exception p_72623_1_) {
      if(this.field_72619_a) {
         this.func_72606_c("Unexpected exception, buggy JRE? (" + p_72623_1_.toString() + ")");
         if(!this.func_72626_g()) {
            this.func_72610_d("Failed to recover from buggy JRE, shutting down!");
            this.field_72619_a = false;
         }

      }
   }

   private boolean func_72626_g() {
      try {
         this.field_72633_m = new DatagramSocket(this.field_72636_h, InetAddress.getByName(this.field_72642_r));
         this.func_72601_a(this.field_72633_m);
         this.field_72633_m.setSoTimeout(500);
         return true;
      } catch (SocketException socketexception) {
         this.func_72606_c("Unable to initialise query system on " + this.field_72642_r + ":" + this.field_72636_h + " (Socket): " + socketexception.getMessage());
      } catch (UnknownHostException unknownhostexception) {
         this.func_72606_c("Unable to initialise query system on " + this.field_72642_r + ":" + this.field_72636_h + " (Unknown Host): " + unknownhostexception.getMessage());
      } catch (Exception exception) {
         this.func_72606_c("Unable to initialise query system on " + this.field_72642_r + ":" + this.field_72636_h + " (E): " + exception.getMessage());
      }

      return false;
   }

   class Auth {
      private long field_72598_b = (new Date()).getTime();
      private int field_72599_c;
      private byte[] field_72596_d;
      private byte[] field_72597_e;
      private String field_72595_f;

      public Auth(DatagramPacket p_i1535_2_) {
         byte[] abyte = p_i1535_2_.getData();
         this.field_72596_d = new byte[4];
         this.field_72596_d[0] = abyte[3];
         this.field_72596_d[1] = abyte[4];
         this.field_72596_d[2] = abyte[5];
         this.field_72596_d[3] = abyte[6];
         this.field_72595_f = new String(this.field_72596_d);
         this.field_72599_c = (new Random()).nextInt(16777216);
         this.field_72597_e = String.format("\t%s%d\u0000", new Object[]{this.field_72595_f, Integer.valueOf(this.field_72599_c)}).getBytes();
      }

      public Boolean func_72593_a(long p_72593_1_) {
         return Boolean.valueOf(this.field_72598_b < p_72593_1_);
      }

      public int func_72592_a() {
         return this.field_72599_c;
      }

      public byte[] func_72594_b() {
         return this.field_72597_e;
      }

      public byte[] func_72591_c() {
         return this.field_72596_d;
      }
   }
}
