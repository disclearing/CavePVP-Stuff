package org.spigotmc;

import net.minecraft.server.PacketDataSerializer;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.Unpooled;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.handler.codec.ByteToMessageDecoder;
import net.valorhcf.ReusableByteArray;

import java.util.List;
import java.util.zip.Inflater;

public class SpigotDecompressor extends ByteToMessageDecoder
{

    private final Inflater inflater = new Inflater();
    private static final ReusableByteArray reusableCompressedData = new ReusableByteArray(8192);

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> objects) throws Exception
    {
        if ( byteBuf.readableBytes() == 0 )
        {
            return;
        }

        PacketDataSerializer serializer = new PacketDataSerializer( byteBuf );
        int size = serializer.a();
        if ( size == 0 )
        {
            objects.add( serializer.readBytes( serializer.readableBytes() ) );
        } else
        {
            int compressedSize = serializer.readableBytes();
            byte[] compressedData = reusableCompressedData.get(compressedSize);
            serializer.readBytes( compressedData, 0, compressedSize );
            inflater.setInput( compressedData, 0, compressedSize );

            byte[] data = new byte[ size ];
            inflater.inflate( data );
            objects.add( Unpooled.wrappedBuffer( data ) );
            inflater.reset();
        }
    }
}
