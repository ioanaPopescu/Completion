package com.upb.completion.model;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Ioana Popescu on 5/5/14.
 */
public class FileValidator implements Validator{

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object o, Errors errors) {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFile((MultipartFile)o);

        if (uploadedFile == null || uploadedFile.getFile().getSize() == 0) {
            errors.rejectValue("file", "uploadForm.selectFile", "Please select a file!");
        }
    }
}
