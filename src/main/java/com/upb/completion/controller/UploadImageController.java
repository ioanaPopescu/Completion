package com.upb.completion.controller;

import com.upb.completion.model.CustomImage;
import com.upb.completion.model.FileValidator;
import com.upb.completion.model.UploadedFile;
import com.upb.completion.service.ObjectService;
import com.upb.completion.utils.ProcessImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Ioana Popescu on 5/5/14.
 */
@Controller
public class UploadImageController {
    @Autowired
    private FileValidator fileValidator;

    @Autowired
    private ObjectService objectService;

    @RequestMapping("/fileUploadForm")
    public ModelAndView getUploadForm(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result) {
        return new ModelAndView("uploadForm");
    }

    @RequestMapping("/fileUpload")
    public ModelAndView fileUploaded(@ModelAttribute("uploadedFile") UploadedFile uploadedFile, BindingResult result) {
        MultipartFile file = uploadedFile.getFile();
        fileValidator.validate(file, result);
        String fileName = file.getOriginalFilename();

        if (result.hasErrors()) {
            return new ModelAndView("uploadForm");
        }

        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            CustomImage customImage = new CustomImage(bufferedImage);
            customImage.setName(fileName);
            customImage.setOriginalImageMatrix(ProcessImage.getPixelRGBDataFromBufferedImage(bufferedImage));

            //int[][] resultMatrixGray = ProcessImage.getPixelDataFromBufferedImage((BufferedImage) ProcessImage.getGrayScaleImage(bufferedImage));

            objectService.getImageWithoutMainObject(customImage);
            /*BufferedImage outputImage = customImage.getFinalImage();

            ProcessImage.writeImageOnDisk(fileName, outputImage, "D:/Tests/gray");*/

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView("showFile", "message", fileName);
    }
}
