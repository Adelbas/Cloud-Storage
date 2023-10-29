package ru.adel.encoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.encoder.impl.AuthRequestEncoder;
import ru.adel.encoder.impl.AuthResponseEncoder;
import ru.adel.encoder.impl.LogoutRequestEncoder;
import ru.adel.encoder.impl.UnknownCmdEncoder;
import ru.adel.encoder.impl.FileMessageEncoder;
import ru.adel.encoder.impl.FilesGetRequestEncoder;
import ru.adel.encoder.impl.FilesGetResponseEncoder;
import ru.adel.encoder.impl.FileDownloadRequestEncoder;
import ru.adel.encoder.impl.CopyPasteRequestEncoder;
import ru.adel.encoder.impl.CreateFolderRequestEncoder;
import ru.adel.encoder.impl.DeleteFileRequestEncoder;

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
        commandEncoders.put(CommandType.FILE_MESSAGE, new FileMessageEncoder());
        commandEncoders.put(CommandType.FILES_GET_REQUEST, new FilesGetRequestEncoder());
        commandEncoders.put(CommandType.FILES_GET_RESPONSE, new FilesGetResponseEncoder());
        commandEncoders.put(CommandType.FILE_DOWNLOAD_REQUEST, new FileDownloadRequestEncoder());
        commandEncoders.put(CommandType.CREATE_FOLDER_REQUEST, new CreateFolderRequestEncoder());
        commandEncoders.put(CommandType.COPY_PASTE_REQUEST, new CopyPasteRequestEncoder());
        commandEncoders.put(CommandType.DELETE_FILE_REQUEST, new DeleteFileRequestEncoder());
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Command msg, ByteBuf out) throws Exception {
        if (msg == null) {
            return;
        }

        commandEncoders.get(msg.getCommandType()).encode(ctx, msg, out);
    }
}
