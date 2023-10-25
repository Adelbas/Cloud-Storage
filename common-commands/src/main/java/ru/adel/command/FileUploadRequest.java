package ru.adel.command;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.adel.Command;
import ru.adel.CommandType;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class FileUploadRequest extends Command {

//    private String filename;
//
//    private long size;
//
//    private byte[] data;
//
//    public FileUploadRequest(String filename, long size, byte[] data) {
//        super(CommandType.FILE_UPLOAD_REQUEST);
//        this.filename = filename;
//        this.size = size;
//        this.data = data;
//    }

    private int size;

    private byte[] data;

    public FileUploadRequest(int size, byte[] data) {
        super(CommandType.FILE_UPLOAD_REQUEST);
        this.data = data;
        this.size = size;
    }
}
