package net.minecraft.util;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   public static final ListeningExecutorService field_180193_a = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool((new ThreadFactoryBuilder()).setDaemon(true).setNameFormat("Downloader %d").build()));
   private static final AtomicInteger field_151228_a = new AtomicInteger(0);
   private static final Logger field_151227_b = LogManager.getLogger();

   public static String func_76179_a(Map<String, Object> p_76179_0_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(Entry<String, Object> entry : p_76179_0_.entrySet()) {
         if(stringbuilder.length() > 0) {
            stringbuilder.append('&');
         }

         try {
            stringbuilder.append(URLEncoder.encode((String)entry.getKey(), "UTF-8"));
         } catch (UnsupportedEncodingException unsupportedencodingexception1) {
            unsupportedencodingexception1.printStackTrace();
         }

         if(entry.getValue() != null) {
            stringbuilder.append('=');

            try {
               stringbuilder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            } catch (UnsupportedEncodingException unsupportedencodingexception) {
               unsupportedencodingexception.printStackTrace();
            }
         }
      }

      return stringbuilder.toString();
   }

   public static String func_151226_a(URL p_151226_0_, Map<String, Object> p_151226_1_, boolean p_151226_2_) {
      return func_151225_a(p_151226_0_, func_76179_a(p_151226_1_), p_151226_2_);
   }

   private static String func_151225_a(URL p_151225_0_, String p_151225_1_, boolean p_151225_2_) {
      try {
         Proxy proxy = MinecraftServer.func_71276_C() == null?null:MinecraftServer.func_71276_C().func_110454_ao();
         if(proxy == null) {
            proxy = Proxy.NO_PROXY;
         }

         HttpURLConnection httpurlconnection = (HttpURLConnection)p_151225_0_.openConnection(proxy);
         httpurlconnection.setRequestMethod("POST");
         httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         httpurlconnection.setRequestProperty("Content-Length", "" + p_151225_1_.getBytes().length);
         httpurlconnection.setRequestProperty("Content-Language", "en-US");
         httpurlconnection.setUseCaches(false);
         httpurlconnection.setDoInput(true);
         httpurlconnection.setDoOutput(true);
         DataOutputStream dataoutputstream = new DataOutputStream(httpurlconnection.getOutputStream());
         dataoutputstream.writeBytes(p_151225_1_);
         dataoutputstream.flush();
         dataoutputstream.close();
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
         StringBuffer stringbuffer = new StringBuffer();

         String s;
         while((s = bufferedreader.readLine()) != null) {
            stringbuffer.append(s);
            stringbuffer.append('\r');
         }

         bufferedreader.close();
         return stringbuffer.toString();
      } catch (Exception exception) {
         if(!p_151225_2_) {
            field_151227_b.error((String)("Could not post to " + p_151225_0_), (Throwable)exception);
         }

         return "";
      }
   }
}
