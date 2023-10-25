package ru.adel.client.command;

import ru.adel.Command;

/**
 * Interface provides execution of received command.
 */
public interface CommandExecutor {
    void execute(Command command);
}
