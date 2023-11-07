package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class FileDownloadRequest extends Command {

    private String filePath;

    public FileDownloadRequest(String filePath) {
        super(CommandType.FILE_DOWNLOAD_REQUEST);
        this.filePath = filePath;
    }
}
