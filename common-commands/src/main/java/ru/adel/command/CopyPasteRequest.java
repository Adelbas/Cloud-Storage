package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class CopyPasteRequest extends Command {

    private String copyFromPath;

    private String pasteToPath;

    private String filename;

    public CopyPasteRequest(String copyFromPath, String pasteToPath, String filename) {
        super(CommandType.COPY_PASTE_REQUEST);
        this.copyFromPath = copyFromPath;
        this.pasteToPath = pasteToPath;
        this.filename = filename;
    }
}
