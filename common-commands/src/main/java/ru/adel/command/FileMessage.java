package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class FileMessage extends Command {

    private String filename;

    private String directory;

    private int size;

    private byte[] data;

    public FileMessage(String filename, String directory, int size, byte[] data) {
        super(CommandType.FILE_MESSAGE);
        this.filename = filename;
        this.directory = directory;
        this.data = data;
        this.size = size;
    }
}
