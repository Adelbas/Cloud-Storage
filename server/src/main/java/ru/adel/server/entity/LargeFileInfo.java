package ru.adel.server.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LargeFileInfo {

    private String filename;

    private String directory;

    private long size;
}
