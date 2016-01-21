package minebot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import net.minecraft.client.main.Main;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author avecowa
 */
public class Autorun {
    public static void main(String[] args) throws IOException, InterruptedException{
        String s = Autorun.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(5)+"../../autorun/runmc.command";
        if(s.contains("jar")){
                start(args);
                return;
        }
        Process p;
        String[] arr = new String[1];
        arr[0] = s;
        System.out.println(p = Runtime.getRuntime().exec("/usr/local/bin/ant jar"));
        InputStream i = p.getInputStream();
        InputStream e = p.getErrorStream();
        OutputStream o = p.getOutputStream();
        while(p.isAlive() || e.available()>0 || i.available()>0){
            while(i.available()>0){
                System.out.print((char)(i.read()));
            }
            while(e.available()>0){
                System.out.print((char)(e.read()));
            }
            
        }
        System.out.println(p = Runtime.getRuntime().exec("java -Djava.library.path=jars/versions/1.8.8/1.8.8-natives/ -jar dist/MineBot.jar"));
        System.out.println("/bin/bash"+" "+arr[0]);
        i = p.getInputStream();
        e = p.getErrorStream();
        o = p.getOutputStream();
        while(p.isAlive() || e.available()>0 || i.available()>0){
            while(i.available()>0){
                System.out.print((char)(i.read()));
            }
            while(e.available()>0){
                System.out.print((char)(e.read()));
            }
            
        }
    }
    private static void start(String[] args){
        System.out.println(Arrays.asList(args));
        System.out.println(System.getProperty("java.library.path"));
        //System.setProperty("java.library.path", System.getProperty("user.home") + "/Dropbox/MineBot/mcp918/jars/versions/1.8.8/1.8.8-natives/");
        //System.out.println(System.getProperty("java.library.path"));
        Main.main(concat(new String[]{"--version", "mcp", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args));
    }
    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
