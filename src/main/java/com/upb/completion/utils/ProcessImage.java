package com.upb.completion.utils;

import com.upb.completion.model.CustomImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.io.*;

/**
 * Created by Ioana Popescu on 5/5/14.
 */
public class ProcessImage {

    public static final int PIXEL_DEPTH = 3;
    public static final int RED = 0;
    public static final int GREEN = 1;
    public static final int BLUE = 2;

    /**
     * This method is used to parse a jpg image to a RGB pixel matrix. The method is calling the
     * private method transformArrayToRGBMatrix in order to transform the resulting array of data
     * into a 3D matrix.
     *
     * @param bufferedImage input Image
     * @return the RGB matrix
     * @throws IOException exception thrown if there were errors in the parsing of the image
     */
    public static int[][][] getPixelRGBDataFromBufferedImage(BufferedImage bufferedImage) throws IOException {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = getPixelsFromBufferedImage(bufferedImage);
        return transformArrayToRGBMatrix(pixels, width, height);
    }

    /**
     * This method is used to parse a jpg image to a RGB pixel matrix. The method is calling the
     * private method transformArrayToMatrix in order to transform the resulting array of data
     * into a 2D matrix.
     *
     * @param bufferedImage input Image
     * @return the 2D matrix
     * @throws IOException exception thrown if there were errors in the parsing of the image
     */
    public static int[][] getPixelDataFromBufferedImage(BufferedImage bufferedImage) throws IOException {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = getPixelsFromBufferedImage(bufferedImage);
        return transformArrayToMatrix(pixels, width, height);
    }

    /**
     * This method is used to recreate a BufferedImage using an int matrix that contains
     * all pixel data. The method is calling the private method transformMatrixToArray in
     * order to recreate the output array to be transformed into a BufferedImage.
     *
     * @param rgbPixels pixel data array in RGB format
     * @param width     the width of the input image
     * @param height    the height of the input image
     * @return the BufferedImage recreated from the existing pixels
     */
    public static BufferedImage getBufferedImageFromPixelData(int[][][] rgbPixels, int width, int height, int writingType) {
        int[] pixels = transformMatrixToArray(rgbPixels, width, height);
        return getBufferedImageFromPixelData(pixels, width, height, writingType);
    }

    /**
     * This method is used to recreate a BufferedImage using an int matrix that contains
     * gray pixel data. The method is calling the private method transformMatrixToArray in
     * order to recreate the output array to be transformed into a BufferedImage.
     *
     * @param grayPixels gray pixel data array
     * @param width      the width of the input image
     * @param height     the height of the input image
     * @return the BufferedImage recreated from the existing pixels
     */
    public static BufferedImage getBufferedImageFromPixelData(int[][] grayPixels, int width, int height, int writingType) {
        int[] pixels = transformMatrixToArray(grayPixels, width, height);
        return getBufferedImageFromPixelData(pixels, width, height, writingType);
    }

    /**
     * This method is returning a BufferedImage created using the int array of data
     * received as a parameter.
     *
     * @param pixels      pixel data to be written in the output image
     * @param width       the width of the input image
     * @param height      the height of the input image
     * @param writingType the type of image to be returned
     * @return the BufferedImage output
     */
    private static BufferedImage getBufferedImageFromPixelData(int[] pixels, int width, int height, int writingType) {
        BufferedImage bufferedImage = new BufferedImage(width, height, writingType);
        bufferedImage.setRGB(0, 0, width, height, pixels, 0, width);
        return bufferedImage;
    }

    /**
     * This method is transforming a BufferedImage into a gray level Image.
     *
     * @param bufferedImage the original RGB image
     * @return the output gray level Image
     */
    public static Image getGrayScaleImage(BufferedImage bufferedImage) {
        BufferedImage grayImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics graphics = grayImage.getGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        return grayImage;
    }


    public static void writeImageOnDisk(String fileName, BufferedImage outputImage) throws IOException {
        InputStream inputStream;OutputStream outputStream;ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(outputImage, "jpg", baos);
        inputStream = new ByteArrayInputStream(baos.toByteArray());

        File newFile = new File("D:/Tests/gray" + fileName);
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        outputStream = new FileOutputStream(newFile);
        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        outputStream.close();
    }

    /**
     * This method is used to transform an Image's array of
     * pixels into a 3D matrix.
     *
     * @param pixels input array of pixels
     * @param width  the width of the input image
     * @param height the height of the input image
     * @return the resulting matrix
     */
    private static int[][][] transformArrayToRGBMatrix(int[] pixels, int width, int height) {
        int[][][] matrix = new int[height][width][PIXEL_DEPTH];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                matrix[row][column][RED] = (pixels[row * width + column] >> 16) & 0xFF;
                matrix[row][column][GREEN] = (pixels[row * width + column] >> 8) & 0xFF;
                matrix[row][column][BLUE] = (pixels[row * width + column] >> 0) & 0xFF;
            }
        }
        return matrix;
    }

    /**
     * This method is used to transform the gray pixel data into a 2D matrix.
     *
     * @param pixels the pixel values of the gray image
     * @param width  the width of the input image
     * @param height the height of the input image
     * @return the matrix corresponding to the gray image
     */
    private static int[][] transformArrayToMatrix(int[] pixels, int width, int height) {
        int[][] matrix = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                matrix[row][column] = pixels[row * width + column] & 0xFF;
            }
        }
        return matrix;
    }

    /**
     * This method is recreating a natural Image vector using its RGB values for each pixel
     *
     * @param rgbImage RGB values received as a 3D matrix
     * @param width    the width of the input image
     * @param height   the height of the input image
     * @return the resulting output stream as an integer array
     */
    private static int[] transformMatrixToArray(int[][][] rgbImage, int width, int height) {
        int[] array = new int[width * height];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                array[row * width + column] = (rgbImage[row][column][0] << 16) +
                        (rgbImage[row][column][1] << 8) +
                        rgbImage[row][column][2];
            }
        }
        return array;
    }

    /**
     * This method is recreating a gray level image.
     *
     * @param grayImage gray levels received as a 2D matrix
     * @param width     the width of the input image
     * @param height    the height of the input image
     * @return the resulting output stream as an integer array
     */
    private static int[] transformMatrixToArray(int[][] grayImage, int width, int height) {
        int[] array = new int[width * height];
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                array[row * width + column] = grayImage[row][column];
            }
        }
        return array;
    }

    /**
     * This method is using a <code>PixelGrabber</code> object in order to grab all
     * pixels data from a BufferedImage.
     *
     * @param bufferedImage the input image
     * @return an int array containing the pixels values
     * @throws IOException
     */
    private static int[] getPixelsFromBufferedImage(BufferedImage bufferedImage) throws IOException {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int[] pixels = new int[width * height];
        PixelGrabber pixelGrabber = new PixelGrabber(bufferedImage, 0, 0, width, height, pixels, 0, width);
        try {
            pixelGrabber.grabPixels();
            if ((pixelGrabber.getStatus() & ImageObserver.ABORT) != 0) {
                throw new IOException("Unable to serialize java.awt.image: PixelGrabber aborted.");
            }
        } catch (InterruptedException ie) {
            throw new IOException("Unable to serialize java.awt.image: PixelGrabber aborted.");
        }
        return pixels;
    }
}
