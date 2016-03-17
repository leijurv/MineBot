/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebot.util;

/**
 *
 * @author avecowa
 */
public class Out {
    public static enum Mode {
        None, Minimal, Standard, Debug, Ludicrous
    }
    public static Mode mode = Mode.Standard;
    public static void log(Object o) {
        String message = trace() + o.toString();
        System.out.println(message);
        if (mode == Mode.Ludicrous) {
            chatRaw(message);
        }
    }
    public static void gui(Object o, Mode req) {
        String message = o.toString();
        String trace = trace();
        System.out.println(trace + message);
        if (req.compareTo(mode) <= 0) {
            if (Mode.Debug.compareTo(mode) <= 0) {
                message = trace() + message;
            }
            chatRaw(message);
        }
    }
    private static void chatRaw(String s) {
        net.minecraft.client.gui.GuiScreen.sendChatMessage(s);
    }
    private static String trace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement trace = stack[3];
        boolean a = false;
        for (int i = 3; i < stack.length; i++) {
            StackTraceElement e = stack[i];
            if (!e.getClassName().equals(Out.class.getName())) {
                trace = e;
                break;
            }
        }
        return trace.getClassName() + ":" + trace.getLineNumber() + "\t";
    }
}
