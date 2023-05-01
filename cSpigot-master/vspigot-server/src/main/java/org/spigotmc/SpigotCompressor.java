package org.spigotmc;

import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.handler.codec.MessageToByteEncoder;
import net.valorhcf.ReusableByteArray;

import java.util.zip.Deflater;

public class SpigotCompressor extends MessageToByteEncoder
{

    private final byte[] buffer = new byte[8192];
    private final Deflater deflater = new Deflater();
    private static final ReusableByteArray reusableData = new ReusableByteArray(0x70000); // rough size estimate from a quick play test

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception
    {
        ByteBuf in = (ByteBuf) msg;
        int origSize = in.readableBytes();

        if ( origSize < 256 )
        {
            writeVarInt( 0, out );
            out.writeBytes( in );
        } else
        {
            byte[] data = reusableData.get(origSize);
            in.readBytes( data, 0, origSize );

            writeVarInt( origSize, out );

            deflater.setInput( data, 0, origSize );
            deflater.finish();
            while (!deflater.finished()) {
                int count = deflater.deflate( buffer );
                out.writeBytes( buffer, 0, count );
            }
            deflater.reset();
        }
    }

    public static void writeVarInt(int val, ByteBuf out) {
        while ((val & -128) != 0) {
            out.writeByte(val & 127 | 128);
            val >>>= 7;
        }

        out.writeByte(val);
    }

}
