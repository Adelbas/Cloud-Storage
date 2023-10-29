package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class DeleteFileRequest extends Command {

    private String path;

    public DeleteFileRequest(String path) {
        super(CommandType.DELETE_FILE_REQUEST);
        this.path = path;
    }
}
