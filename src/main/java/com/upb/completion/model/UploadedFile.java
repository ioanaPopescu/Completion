package com.upb.completion.model;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Ioana Popescu on 5/5/14.
 */
public class UploadedFile {
    private MultipartFile file;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
