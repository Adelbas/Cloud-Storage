package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class CreateFolderRequest extends Command {

    private String folderName;

    private String folderPath;

    public CreateFolderRequest(String folderPath, String folderName) {
        super(CommandType.CREATE_FOLDER_REQUEST);
        this.folderName = folderName;
        this.folderPath = folderPath;
    }
}
