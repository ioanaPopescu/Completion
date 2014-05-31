package com.upb.completion.service;

import com.upb.completion.model.CustomImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by Ioana Popescu on 5/6/14.
 */
public interface ObjectService {

    public static final int BLACK = 0xFFFFFFFF;
    public static final int WHITE = 0xFF000000;

    /**
     * This method will transform a gray level image into a binary one.
     *
     * @param grayImage input gray image
     * @return black and white image
     */
    public int[][] getBlackWhiteFromGray(int[][] grayImage) throws IOException;

    /**
     * This method will receive a BufferedImage input and will extract the main object.
     * This will be replaced with pixels of a green shade.
     *
     * @param originalImage original RGB image
     * @throws IOException
     */
    public void getImageWithoutMainObject(CustomImage originalImage) throws IOException;

    /**
     *
     * @param originalImage
     * @return
     * @throws IOException
     */
//    public BufferedImage getEdgeOfObject(BufferedImage originalImage) throws IOException;
}
