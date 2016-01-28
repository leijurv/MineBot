package net.minecraft.server.gui;

import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftServerGui extends JComponent
{
    private static final Font serverGuiFont = new Font("Monospaced", 0, 12);
    private static final Logger LOGGER = LogManager.getLogger();
    private DedicatedServer server;

    /**
     * Creates the server GUI and sets it visible for the user.
     */
    public static void createServerGui(final DedicatedServer serverIn)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception var3)
        {
            ;
        }

        MinecraftServerGui minecraftservergui = new MinecraftServerGui(serverIn);
        JFrame jframe = new JFrame("Minecraft server");
        jframe.add(minecraftservergui);
        jframe.pack();
        jframe.setLocationRelativeTo((Component)null);
        jframe.setVisible(true);
        jframe.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent p_windowClosing_1_)
            {
                serverIn.initiateShutdown();

                while (!serverIn.isServerStopped())
                {
                    try
                    {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException interruptedexception)
                    {
                        interruptedexception.printStackTrace();
                    }
                }

                System.exit(0);
            }
        });
    }

    public MinecraftServerGui(DedicatedServer serverIn)
    {
        this.server = serverIn;
        this.setPreferredSize(new Dimension(854, 480));
        this.setLayout(new BorderLayout());

        try
        {
            this.add(this.getLogComponent(), "Center");
            this.add(this.getStatsComponent(), "West");
        }
        catch (Exception exception)
        {
            LOGGER.error((String)"Couldn\'t build server GUI", (Throwable)exception);
        }
    }

    /**
     * Generates new StatsComponent and returns it.
     */
    private JComponent getStatsComponent() throws Exception
    {
        JPanel jpanel = new JPanel(new BorderLayout());
        jpanel.add(new StatsComponent(this.server), "North");
        jpanel.add(this.getPlayerListComponent(), "Center");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
        return jpanel;
    }

    /**
     * Generates new PlayerListComponent and returns it.
     */
    private JComponent getPlayerListComponent() throws Exception
    {
        JList jlist = new PlayerListComponent(this.server);
        JScrollPane jscrollpane = new JScrollPane(jlist, 22, 30);
        jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
        return jscrollpane;
    }

    private JComponent getLogComponent() throws Exception
    {
        JPanel jpanel = new JPanel(new BorderLayout());
        final JTextArea jtextarea = new JTextArea();
        final JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
        jtextarea.setEditable(false);
        jtextarea.setFont(serverGuiFont);
        final JTextField jtextfield = new JTextField();
        jtextfield.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent p_actionPerformed_1_)
            {
                String s = jtextfield.getText().trim();

                if (s.length() > 0)
                {
                    MinecraftServerGui.this.server.addPendingCommand(s, MinecraftServer.getServer());
                }

                jtextfield.setText("");
            }
        });
        jtextarea.addFocusListener(new FocusAdapter()
        {
            public void focusGained(FocusEvent p_focusGained_1_)
            {
            }
        });
        jpanel.add(jscrollpane, "Center");
        jpanel.add(jtextfield, "South");
        jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
        Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                String s;

                while ((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null)
                {
                    MinecraftServerGui.this.func_164247_a(jtextarea, jscrollpane, s);
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
        return jpanel;
    }

    public void func_164247_a(final JTextArea p_164247_1_, final JScrollPane p_164247_2_, final String p_164247_3_)
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    MinecraftServerGui.this.func_164247_a(p_164247_1_, p_164247_2_, p_164247_3_);
                }
            });
        }
        else
        {
            Document document = p_164247_1_.getDocument();
            JScrollBar jscrollbar = p_164247_2_.getVerticalScrollBar();
            boolean flag = false;

            if (p_164247_2_.getViewport().getView() == p_164247_1_)
            {
                flag = (double)jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double)(serverGuiFont.getSize() * 4) > (double)jscrollbar.getMaximum();
            }

            try
            {
                document.insertString(document.getLength(), p_164247_3_, (AttributeSet)null);
            }
            catch (BadLocationException var8)
            {
                ;
            }

            if (flag)
            {
                jscrollbar.setValue(Integer.MAX_VALUE);
            }
        }
    }
}
