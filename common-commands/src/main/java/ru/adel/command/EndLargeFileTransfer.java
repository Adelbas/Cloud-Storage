package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class EndLargeFileTransfer extends Command {

    private boolean isSucceed;

    public EndLargeFileTransfer(boolean isSucceed) {
        super(CommandType.END_LARGE_FILE_TRANSFER);
        this.isSucceed = isSucceed;
    }
}
