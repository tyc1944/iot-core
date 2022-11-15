package com.yunmo.iot.api.service;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.yunmo.boot.web.upload.FileStorageProperties;
import com.yunmo.iot.domain.file.UploadedFilePath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class FileStorageExtendService {

    private final Path root;

    @Autowired
    public FileStorageExtendService(FileStorageProperties fileStorageProperties) {
        this.root = Path.of(fileStorageProperties.getLocation());
    }

    public UploadedFilePath storageInfo(String location) throws IOException, NoSuchAlgorithmException {
        Path relatedPath = Path.of(URLDecoder.decode(location, Charsets.UTF_8));
        Path absolutePath = root.resolve(relatedPath);
        String md5 = md5(absolutePath);
        return new UploadedFilePath()
                .setRelativePath(relatedPath)
                .setAbsolutePath(absolutePath)
                .setLocation(relatedPath.toString())
                .setMd5(md5)
                .setName(getNameWithoutExtension(relatedPath.toString()));
    }

    private String md5(Path absolutePath) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] secretBytes = md.digest(Files.readAllBytes(absolutePath));
        return new BigInteger(1, secretBytes).toString(16);
    }

    private String getNameWithoutExtension(String file) {
        Preconditions.checkNotNull(file);
        String fileName = (new File(file)).getName();
        int dotIndex = fileName.lastIndexOf(46);
        return dotIndex == -1 ? fileName : fileName.substring(0, dotIndex);
    }

}
