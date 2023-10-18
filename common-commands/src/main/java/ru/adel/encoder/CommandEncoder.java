package ru.adel.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.encoder.impl.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * Class represent encoding Command object to ByteBuf using custom encoders that are storing in EnumMap
 * that uses CommandType as key and Encoder interface implementation as value.
 *
 * @see CommandType
 * @see Encoder
 */
public class CommandEncoder extends MessageToByteEncoder<Command> {

    private final Map<CommandType, Encoder> commandEncoders = new EnumMap<>(CommandType.class);

    {
        commandEncoders.put(CommandType.AUTHENTICATE_REQUEST, new AuthRequestEncoder());
        commandEncoders.put(CommandType.AUTHENTICATE_RESPONSE, new AuthResponseEncoder());
        commandEncoders.put(CommandType.LOGOUT_REQUEST, new LogoutRequestEncoder());
        commandEncoders.put(CommandType.UNKNOWN_COMMAND_RESPONSE, new UnknownCmdEncoder());
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) {
        if (msg == null) {
            return;
        }

        commandEncoders.get(msg.getCommandType()).encode(ctx, msg, out);
    }
}
