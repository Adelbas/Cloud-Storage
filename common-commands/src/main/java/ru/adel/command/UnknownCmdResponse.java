package ru.adel.command;

import lombok.Builder;
import lombok.ToString;
import ru.adel.Command;
import ru.adel.CommandType;

@ToString
@Builder
public class UnknownCmdResponse extends Command {
    public UnknownCmdResponse() {
        super(CommandType.UNKNOWN_COMMAND_RESPONSE);
    }
}
