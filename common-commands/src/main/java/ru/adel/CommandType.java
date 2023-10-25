package ru.adel;

/**
 * Enum with command types for client-server communication
 */
public enum CommandType {
    UNKNOWN_COMMAND_RESPONSE,
    AUTHENTICATE_REQUEST,
    AUTHENTICATE_RESPONSE,
    LOGOUT_REQUEST,
    FILE_UPLOAD_REQUEST
}
