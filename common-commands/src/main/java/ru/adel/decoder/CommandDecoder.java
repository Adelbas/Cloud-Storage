package ru.adel.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import ru.adel.Command;
import ru.adel.CommandType;
import ru.adel.decoder.impl.AuthRequestDecoder;
import ru.adel.decoder.impl.AuthResponseDecoder;
import ru.adel.decoder.impl.LogoutRequestDecoder;
import ru.adel.decoder.impl.UnknownCmdDecoder;
import ru.adel.decoder.impl.FilesGetRequestDecoder;
import ru.adel.decoder.impl.FileDownloadRequestDecoder;
import ru.adel.decoder.impl.FilesGetResponseDecoder;
import ru.adel.decoder.impl.FileMessageDecoder;
import ru.adel.decoder.impl.CreateFolderRequestDecoder;
import ru.adel.decoder.impl.CopyPasteRequestDecoder;
import ru.adel.decoder.impl.DeleteFileRequestDecoder;

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
        commandDecoders.put(CommandType.FILES_GET_REQUEST, new FilesGetRequestDecoder());
        commandDecoders.put(CommandType.FILES_GET_RESPONSE, new FilesGetResponseDecoder());
        commandDecoders.put(CommandType.FILE_MESSAGE, new FileMessageDecoder());
        commandDecoders.put(CommandType.FILE_DOWNLOAD_REQUEST, new FileDownloadRequestDecoder());
        commandDecoders.put(CommandType.CREATE_FOLDER_REQUEST, new CreateFolderRequestDecoder());
        commandDecoders.put(CommandType.COPY_PASTE_REQUEST, new CopyPasteRequestDecoder());
        commandDecoders.put(CommandType.DELETE_FILE_REQUEST, new DeleteFileRequestDecoder());
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in == null) {
            return;
        }

        CommandType commandType = CommandType.values()[in.readInt()];
        commandDecoders.get(commandType).decode(ctx, in, out);
    }
}
