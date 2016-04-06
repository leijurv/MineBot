/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minebotclient;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author leijurv
 */
public class MineBotClient {
    static BufferedImage image = null;
    static final ArrayList<Long> receiveTimes = new ArrayList<>();
    static final ArrayList<Integer> numBytesRead = new ArrayList<>();
    static Integer heightOffset = null;
    public static void main(String[] args) throws IOException {
        Socket sk = new Socket("localhost", 5021);
        DataInputStream in = new DataInputStream(sk.getInputStream());
        JFrame frame = new JFrame("MineBot client");
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        frame.repaint();
                        Thread.sleep(10);
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(MineBotClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
        new Thread() {
            public void run() {
                try {
                    while (true) {
                        int width = in.readInt();
                        int height = in.readInt();
                        int[] blah = (int[]) new ObjectInputStream(sk.getInputStream()).readObject();
                        int size = blah.length;
                        System.out.println("Read " + width + " " + height + " " + size);
                        BufferedImage bufferedimage;
                        bufferedimage = new BufferedImage(width, height, 1);
                        bufferedimage.setRGB(0, 0, width, height, blah, 0, width);
                        image = bufferedimage;
                        long time = System.currentTimeMillis();
                        synchronized (receiveTimes) {
                            receiveTimes.add(time);
                            numBytesRead.add(size);
                        }
                        if (heightOffset == null || (frame.getContentPane().getSize().height != height && (new Random().nextInt(5) == 0))) {
                            if (receiveTimes.size() > 5) {
                                frame.setSize(width, height);
                                Dimension actualSize = frame.getContentPane().getSize();
                                int extraW = width - actualSize.width;
                                int extraH = height - actualSize.height;
                                heightOffset = extraH;
                                frame.setSize(width + extraW, height + extraH);
                            } else {
                                frame.setSize(width, height);
                            }
                        } else {
                            frame.setSize(width, height + heightOffset);
                        }
                        frame.repaint();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(MineBotClient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MineBotClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
        frame.setContentPane(new JComponent() {
            public void paintComponent(Graphics g) {
                if (image != null) {
                    g.drawImage(image, 0, 0, null);
                    try {
                        g.drawString("Bytes available: " + in.available(), 0, 15);
                    } catch (IOException ex) {
                        Logger.getLogger(MineBotClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (receiveTimes.size() < 10) {
                        return;
                    }
                    synchronized (receiveTimes) {
                        while (receiveTimes.size() > 10) {
                            receiveTimes.remove(0);
                            numBytesRead.remove(0);
                        }
                        long MSForTenFrames = receiveTimes.get(receiveTimes.size() - 1) - receiveTimes.get(0);
                        int numBytesInTenFrames = numBytesRead.stream().reduce(0, Integer::sum);
                        double numBytesPerSecond = ((double) numBytesInTenFrames) / (MSForTenFrames / 1000D);
                        double megabytesPerSecond = numBytesPerSecond / 1000000;
                        double MSForOneFrame = MSForTenFrames / 10D;
                        double framesPerMS = 1D / MSForOneFrame;
                        double framesPerSecond = framesPerMS * 1000D;
                        String blah = "" + framesPerSecond;
                        if (blah.length() > 5) {
                            blah = blah.substring(0, 5);
                        }
                        String lol = "" + megabytesPerSecond;
                        if (lol.length() > 5) {
                            lol = lol.substring(0, 5);
                        }
                        g.drawString("Receive rate (fps): " + blah, 0, 30);
                        g.drawString("Receive rate (megabytes per sec): " + lol, 0, 45);
                    }
                }
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(10000, 10000);
        frame.setVisible(true);
    }
}
