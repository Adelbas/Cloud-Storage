package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class FilesGetRequest extends Command {

    private String directory;

    public FilesGetRequest(String directory) {
        super(CommandType.FILES_GET_REQUEST);
        this.directory = directory;
    }
}
