package com.yunmo.iot.domain.file;

import lombok.Data;
import lombok.experimental.Accessors;

import java.nio.file.Path;

@Data
@Accessors(chain = true)
public class UploadedFilePath {

    private Path relativePath;

    private Path absolutePath;

    private String location;

    private String md5;

    private String name;

}
