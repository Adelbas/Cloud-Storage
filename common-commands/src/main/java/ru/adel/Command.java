package ru.adel;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Abstract class that represents command.
 * To add new command inherit this class and create for it decoder and encoder.
 */
@Data
@AllArgsConstructor
public abstract class Command {

    private final CommandType commandType;

}
