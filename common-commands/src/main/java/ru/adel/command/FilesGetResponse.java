package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

import java.io.File;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class FilesGetResponse extends Command {

    private List<File> files;

    public FilesGetResponse(List<File> files) {
        super(CommandType.FILES_GET_RESPONSE);
        this.files = files;
    }
}
