package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class StartLargeFileUpload extends Command {

    private String filename;

    private String directory;

    private long size;

    public StartLargeFileUpload(String filename, String directory, long size) {
        super(CommandType.START_LARGE_FILE_UPLOAD);
        this.filename = filename;
        this.directory = directory;
        this.size = size;
    }
}
