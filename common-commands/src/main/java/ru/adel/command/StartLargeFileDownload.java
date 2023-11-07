package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class StartLargeFileDownload extends Command {

    private String filePath;

    public StartLargeFileDownload(String filePath) {
        super(CommandType.START_LARGE_FILE_DOWNLOAD);
        this.filePath = filePath;
    }
}
