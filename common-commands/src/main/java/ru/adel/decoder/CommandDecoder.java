package ru.adel.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.decoder.impl.*;
import ru.adel.encoder.Encoder;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Class represent decoding of ByteByf to Command object using custom decoders that are storing in EnumMap
 * that uses CommandType as key and Decoder interface implementation as value.
 *
 * @see CommandType
 * @see Decoder
 */
public class CommandDecoder extends ReplayingDecoder<Command> {

    private final Map<CommandType, Decoder> commandDecoders = new EnumMap<>(CommandType.class);

    {
        commandDecoders.put(CommandType.AUTHENTICATE_REQUEST, new AuthRequestDecoder());
        commandDecoders.put(CommandType.AUTHENTICATE_RESPONSE, new AuthResponseDecoder());
        commandDecoders.put(CommandType.LOGOUT_REQUEST, new LogoutRequestDecoder());
        commandDecoders.put(CommandType.UNKNOWN_COMMAND_RESPONSE, new UnknownCmdDecoder());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        if (in == null) {
            return;
        }

        CommandType commandType = CommandType.values()[in.readInt()];
        commandDecoders.get(commandType).decode(ctx, in, out);
    }
}
