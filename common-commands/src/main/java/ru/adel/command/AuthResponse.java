package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class AuthResponse extends Command {

    private boolean isAuthenticated;

    private int attemptsLeft;

    public AuthResponse(boolean isAuthenticated, int attemptsLeft) {
        super(CommandType.AUTHENTICATE_RESPONSE);
        this.isAuthenticated = isAuthenticated;
        this.attemptsLeft = attemptsLeft;
    }
}
