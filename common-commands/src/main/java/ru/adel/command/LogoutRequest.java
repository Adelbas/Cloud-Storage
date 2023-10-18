package ru.adel.command;

import lombok.Builder;
import lombok.ToString;
import ru.adel.Command;
import ru.adel.CommandType;

@ToString
@Builder
public class LogoutRequest extends Command {
    public LogoutRequest() {
        super(CommandType.LOGOUT_REQUEST);
    }
}
