package ru.adel;

/**
 * Enum with command types for client-server communication
 */
public enum CommandType {
    UNKNOWN_COMMAND_RESPONSE,
    AUTHENTICATE_REQUEST,
    AUTHENTICATE_RESPONSE,
    LOGOUT_REQUEST,
    FILES_GET_REQUEST,
    FILES_GET_RESPONSE,
    FILE_MESSAGE,
    FILE_DOWNLOAD_REQUEST,
    CREATE_FOLDER_REQUEST,
    COPY_PASTE_REQUEST,
    DELETE_FILE_REQUEST
}
