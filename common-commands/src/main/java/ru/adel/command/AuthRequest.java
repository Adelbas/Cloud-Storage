package ru.adel.command;

import lombok.*;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class AuthRequest extends Command {

    private String username;

    private String password;

    public AuthRequest(String username, String password) {
        super(CommandType.AUTHENTICATE_REQUEST);
        this.username = username;
        this.password = password;
    }
}

