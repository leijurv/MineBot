package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S02PacketChat implements Packet<INetHandlerPlayClient>
{
    private IChatComponent chatComponent;
    private byte type;

    public S02PacketChat()
    {
    }

    public S02PacketChat(IChatComponent component)
    {
        this(component, (byte)1);
    }

    public S02PacketChat(IChatComponent message, byte typeIn)
    {
        this.chatComponent = message;
        this.type = typeIn;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.chatComponent = buf.readChatComponent();
        this.type = buf.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeChatComponent(this.chatComponent);
        buf.writeByte(this.type);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(INetHandlerPlayClient handler)
    {
        handler.handleChat(this);
    }

    public boolean isChat()
    {
        return this.type == 1 || this.type == 2;
    }
}
