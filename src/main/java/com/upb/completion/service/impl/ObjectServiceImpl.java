package com.upb.completion.service.impl;

import com.upb.completion.model.CustomImage;
import com.upb.completion.model.PatchSimilarity;
import com.upb.completion.model.Priority;
import com.upb.completion.service.ObjectService;
import com.upb.completion.utils.CannyEdgeDetector;
import com.upb.completion.utils.ProcessImage;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

/**
 * Created by Ioana Popescu on 5/6/14.
 * <p/>
 * This class holds all methods that are used to process the images in order to analyze
 * the objects present in these.
 */
public class ObjectServiceImpl implements ObjectService {
    public static final int WHITE_255 = 255;
    public static final int BLACK_0 = 0;
    private int background;
    private int object;
    private Map<Integer, Long> areasList = new HashMap<Integer, Long>();
    private final double MINIMUM_AREA_PERCENTAGE = 0.15;
    private final int WINDOW_DIMENSION = 9;
    private int dominantValue;
    private static int nrTestImage = 0;

    @Override
    public int[][] getBlackWhiteFromGray(int[][] grayImage) throws IOException {
        int height = grayImage.length;
        int width = grayImage[0].length;

        int threshold = getThresholdValue(grayImage, width, height);

        boolean backgroundLow = isBackgroundLessThanThreshold(grayImage, width, height, threshold);
        if (backgroundLow) {
            background = BLACK;
            object = WHITE;
        } else {
            background = WHITE;
            object = BLACK;
        }

        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                grayImage[row][column] = (grayImage[row][column] < threshold) ? background : object;
            }
        }
        ProcessImage.writeImageOnDisk("bwimag.jpg", ProcessImage.getBufferedImageFromPixelData(grayImage, width, height, BufferedImage.TYPE_BYTE_GRAY),
                "D:/Tests/");
        return grayImage;
    }

    @Override
    public void getImageWithoutMainObject(CustomImage originalImage) throws IOException {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        originalImage.setGrayImage((BufferedImage) ProcessImage.getGrayScaleImage(originalImage.getOriginalImage()));
        originalImage.setGrayImageMatrix(ProcessImage.getPixelDataFromBufferedImage(originalImage.getGrayImage()));
        int[][] blackWhiteImage = getBlackWhiteFromGray(originalImage.getGrayImageMatrix());
        int[][] withoutObjectImage = getImageMainObjectMatrix(blackWhiteImage, width, height);

        getDominantObjectValue(width, height);
        eliminateMainObjectFromImage(originalImage, withoutObjectImage);
        reconstructImage(originalImage);
        originalImage.setFinalImage(ProcessImage.getBufferedImageFromPixelData(
                originalImage.getFinalImageMatrix(), width, height, BufferedImage.TYPE_INT_RGB));
    }

    /**
     * This method is eliminating everything from the image but the main object.
     *
     * @param blackWhiteImage input image in a binary format
     * @param width           the width of the image
     * @param height          the height of the image
     * @return the output image that will have a white background and a black main object
     */
    private int[][] getImageMainObjectMatrix(int[][] blackWhiteImage, int width, int height) {
        //a new value to change all pixels that correspond to a certain object
        int changeValue = 2;
        for (int row = 1; row < height - 1; row++) {
            for (int column = 1; column < width - 1; column++) {
                if (blackWhiteImage[row][column] == WHITE) {
                    Long area = Long.valueOf(0);
                    Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
                    Integer index = 0;
                    ArrayList<Integer> initialValue = new ArrayList<Integer>();
                    initialValue.add(0, row);
                    initialValue.add(1, column);
                    map.put(index++, initialValue);

                    Iterator iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        int temp_row = (Integer) ((ArrayList) entry.getValue()).get(0);
                        int temp_column = (Integer) ((ArrayList) entry.getValue()).get(1);
                        int indexRemove = (Integer) entry.getKey();
                        for (int i = temp_row - 1; i <= temp_row + 1; i++) {
                            for (int j = temp_column - 1; j <= temp_column + 1; j++) {
                                if ((i != row || j != column)
                                        && i >= 0 && i < height
                                        && j >= 0 && j < width
                                        && blackWhiteImage[i][j] == WHITE) {
                                    ArrayList<Integer> valueToBeStored = new ArrayList<Integer>();
                                    valueToBeStored.add(0, i);
                                    valueToBeStored.add(1, j);
                                    if (!map.containsValue(valueToBeStored)) {
                                        map.put(index++, valueToBeStored);
                                    }
                                }
                            }
                        }
                        blackWhiteImage[temp_row][temp_column] = changeValue;
                        area++;
                        map.remove(indexRemove);
                        if (!map.isEmpty()) {
                            iterator = map.entrySet().iterator();
                        }
                    }
                    areasList.put(changeValue, area);
                    changeValue++;
                }
            }
        }
        return blackWhiteImage;
    }

    /**
     * This method counts how many values are smaller than the threshold and how many are
     * greater that the threshold. Depending on these numbers it will return true or false - meaning
     * the background will be considered with the values less that threshold or else.
     *
     * @param grayImage input gray image
     * @param width     the width of the image
     * @param height    the height of the image
     * @param threshold the image's threshold
     * @return a boolean value representing if the background should be less that the threshold
     */
    private boolean isBackgroundLessThanThreshold(int[][] grayImage, int width, int height, int threshold) {
        int lessThanThreshold = 0;
        int greaterThanThreshold = 0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (grayImage[row][column] < threshold) {
                    lessThanThreshold++;
                } else {
                    greaterThanThreshold++;
                }
            }
        }
        if (lessThanThreshold > greaterThanThreshold) {
            return true;
        }
        return false;
    }

    /**
     * This method is extracting the mean gray value contained
     * by the gray level image. This level will be the one to be considered
     * the threshold for the binarization.
     *
     * @param grayImage input gray image
     * @param width     the width of the image
     * @param height    the height of the image
     * @return an integer representing the mean value
     */
    private int getThresholdValue(int[][] grayImage, int width, int height) {
        double mean = 0.0;
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                mean += grayImage[row][column];
            }
        }
        mean = mean / (width * height);
        return (int) mean;
    }

    /**
     * This method determines based on the <code>areasList</code> List of object areas, which object
     * is the biggest. It also keeps track if the object has an area more that 5% of the image area.
     * Else, the object will be considered too small to be important.
     *
     * @param width  the width of the image
     * @param height the height of the image
     */
    private void getDominantObjectValue(int width, int height) {
        double minimumArea = width * height * MINIMUM_AREA_PERCENTAGE;
        Long maximumArea = Long.valueOf(0);

        Iterator iterator = areasList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Long currentArea = (Long) entry.getValue();
            if (currentArea > maximumArea && currentArea > minimumArea) {
                maximumArea = currentArea;
                dominantValue = (Integer) entry.getKey();
            }
        }
    }

    /**
     * This method iterates through the processed image that contains detached
     * objects in order to remove from the original image the determined main
     * object. After the removal the pixels will become black. The new image
     * will be saved on a CustomImage object.
     *
     * @param image       the input image
     * @param objectImage the matrix containing object segmentation
     */
    private void eliminateMainObjectFromImage(CustomImage image, int[][] objectImage) throws IOException {
        int[][][] originalImage = image.getOriginalImageMatrix();
        int width = image.getWidth();
        int height = image.getHeight();
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                if (objectImage[row][column] == dominantValue) {
                    originalImage[row][column][0] = 0;
                    originalImage[row][column][1] = 0;
                    originalImage[row][column][2] = 0;
                    objectImage[row][column] = BLACK;
                } else {
                    objectImage[row][column] = WHITE;
                }
            }
        }
        image.setObjectImageMatrix(objectImage);
        ProcessImage.writeImageOnDisk("object.jpg", ProcessImage.getBufferedImageFromPixelData(objectImage, width, height, BufferedImage.TYPE_BYTE_GRAY),
                "D:/Tests/");
        image.setWithoutObjectMatrix(originalImage);
    }

    /**
     * This method is using a <link>CannyEdgeDetector</link> object in order to
     * extract the edge of the main object. This method also eliminates from the image
     * before the extraction, the objects that are not of interest.
     *
     * @param image       the input image
     * @param objectImage the matrix containing object segmentation
     * @return the binary image containing only the edges
     * @throws IOException
     */
    private void extractContourObject(CustomImage image, int[][] objectImage) throws IOException {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage binaryImage = ProcessImage.getBufferedImageFromPixelData(objectImage,
                width, height, BufferedImage.TYPE_BYTE_GRAY);
        CannyEdgeDetector cannyEdgeDetector = new CannyEdgeDetector();
        cannyEdgeDetector.setSourceImage(binaryImage);
        cannyEdgeDetector.process();
        image.setEdgeObject(ProcessImage.getPixelDataFromBufferedImage(cannyEdgeDetector.getEdgesImage()));
        ProcessImage.writeImageOnDisk(nrTestImage++ + "test.jpg",
                cannyEdgeDetector.getEdgesImage(), "D:/Tests/contour");
    }


    private List<Priority> calculatePriorities(CustomImage image, int windowDimension) {
        int[][][] rgb = image.getWithoutObjectMatrix();
        List<Priority> priorityList = getEdgeAsList(image);

        for (Priority priority : priorityList) {
            int referenceRow = priority.getRow();
            int referenceColumn = priority.getColumn();
            int noPixelsKnown = 0;
            for (int row = referenceRow - 1; row <= referenceRow + 1; row++) {
                for (int column = referenceColumn - 1; column <= referenceColumn + 1; column++) {
                    if (rgb[row][column][0] != 0 ||
                            rgb[row][column][1] != 0 ||
                            rgb[row][column][2] != 0) {
                        noPixelsKnown++;
                    }
                }
            }
            priority.setPriority(noPixelsKnown / Math.pow(windowDimension, 2));
        }
        return priorityList;
    }

    private List<Priority> getEdgeAsList(CustomImage image) {
        int[][] edge = image.getEdgeObject();
        List<Priority> priorityList = new ArrayList<Priority>();

        for (int row = 0; row < image.getHeight(); row++) {
            for (int column = 0; column < image.getWidth(); column++) {
                if (edge[row][column] == WHITE_255 || edge[row][column] == WHITE) {
                    Priority priority = new Priority();
                    priority.setRow(row);
                    priority.setColumn(column);
                    priorityList.add(priority);
                }
            }
        }

        return priorityList;
    }

    /**
     * todo
     * fereastra completa
     *
     * @param knownRegion
     * @param unknownRegion
     * @return
     */
    private double getEuclidianDistanceComplete(int[][][] knownRegion, int[][][] unknownRegion) {
        int sumOfSquares = 0;
        for (int row = 0; row < WINDOW_DIMENSION; row++) {
            for (int column = 0; column < WINDOW_DIMENSION; column++) {
                for (int color = 0; color < 3; color++) {
                    sumOfSquares += Math.pow(knownRegion[row][column][color] - unknownRegion[row][column][color], 2);
                }
            }
        }
        return Math.sqrt(sumOfSquares);
    }

    /**
     * This method is calculating the Euclidian distance between
     * two different 3D image matrices trough the edges. In the calculus,
     * it will be used only the values from the first and last columns,
     * respectively, first and last row.
     *
     * @param knownRegion   the 3D matrix of the known region
     * @param unknownRegion the 3D matrix of the unknown region
     * @return the distance between the two inputs, as a double
     */
    private double getEuclidianDistanceThroughEdge(int[][][] knownRegion, int[][][] unknownRegion) {
        int sumOfSquares = 0;
        for (int k = 0; k < WINDOW_DIMENSION; k++) {
            for (int color = 0; color < 3; color++) {
                sumOfSquares += Math.pow(knownRegion[0][k][color] - unknownRegion[0][k][color], 2)
                        + Math.pow(
                        knownRegion[WINDOW_DIMENSION - 1][k][color] - unknownRegion[WINDOW_DIMENSION - 1][k][color],
                        2)
                        + Math.pow(
                        knownRegion[k][0][color] - unknownRegion[k][0][color],
                        2)
                        + Math.pow(
                        knownRegion[k][WINDOW_DIMENSION - 1][color] - unknownRegion[k][WINDOW_DIMENSION - 1][color],
                        2);
            }
        }
        return Math.sqrt(sumOfSquares);
    }

    private PatchSimilarity getSimilarityKnownUnknown(CustomImage image, int[][][] unknownRegion, int unknownRow, int unknownColumn) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][][] matrix = image.getOriginalImageMatrix();
        int halfDim = WINDOW_DIMENSION / 2;
        int[][][] temporaryMatrix = new int[WINDOW_DIMENSION][WINDOW_DIMENSION][3];
        double minimumSimilarity = Double.MAX_VALUE;
        PatchSimilarity similarity = new PatchSimilarity();

        for (int row = halfDim; row < height - halfDim; row++) {
            for (int column = halfDim; column < width - halfDim; column++) {
                if (row != unknownRow || column != unknownColumn) {
                    int noOfBad = 0;
                    for (int i = row - halfDim; i <= row + halfDim; i++) {
                        for (int j = column - halfDim; j <= column + halfDim; j++) {
                            for (int k = 0; k < 3; k++) {
                                temporaryMatrix[i - row + halfDim][j - column + halfDim][k] = matrix[i][j][k];
                            }
                            if (temporaryMatrix[i - row + halfDim][j - column + halfDim][0] == 0
                                    && temporaryMatrix[i - row + halfDim][j - column + halfDim][1] == 0
                                    && temporaryMatrix[i - row + halfDim][j - column + halfDim][2] == 0) {
                                noOfBad++;
                            }
                        }
                    }
                    if (noOfBad < 0.25 * Math.sqrt(WINDOW_DIMENSION)) {
                        double euclidianDistanceThroughEdge = getEuclidianDistanceThroughEdge(temporaryMatrix, unknownRegion);
                        if (euclidianDistanceThroughEdge <= minimumSimilarity) {
                            minimumSimilarity = euclidianDistanceThroughEdge;
                            similarity.setRow(row);
                            similarity.setColumn(column);
                            similarity.setDistance(euclidianDistanceThroughEdge);
                        }
                    }
                }
            }
        }
        return similarity;
    }

    private void reconstructImage(CustomImage customImage) throws IOException {
        //boolean value containing true if all the image is rebuilt
        boolean completeImage = false;
        int halfDimension = WINDOW_DIMENSION / 2;

        while (!completeImage) {
            int[][][] originalImage = customImage.getOriginalImageMatrix();
            extractContourObject(customImage, customImage.getObjectImageMatrix());
            List<Priority> priorities = calculatePriorities(customImage, WINDOW_DIMENSION);
            Collections.sort(priorities);
            if (priorities.size() == 0) {
                completeImage = true;
                break;
            }
            Iterator iterator = priorities.iterator();
            while (iterator.hasNext()) {
                Priority priority = (Priority) iterator.next();
                int centralPointRow = priority.getRow();
                int centralPointColumn = priority.getColumn();
                iterator.remove();
                int[][][] unknownRegion = getWindowForCentralPoint(originalImage, halfDimension, centralPointRow, centralPointColumn);
                PatchSimilarity similarity = getSimilarityKnownUnknown(customImage, unknownRegion, centralPointRow, centralPointColumn);

                System.out.println("similarity coordinates: " + similarity.getRow() + "    " + similarity.getColumn());
                int[][] objectMap = customImage.getObjectImageMatrix();
                substituteWindows(originalImage, halfDimension, centralPointRow, centralPointColumn, similarity, objectMap);

                customImage.setObjectImageMatrix(objectMap);
            }
            ProcessImage.writeImageOnDisk("modified.jpg",
                    ProcessImage.getBufferedImageFromPixelData(
                            originalImage, customImage.getWidth(), customImage.getHeight(), BufferedImage.TYPE_INT_RGB), "D:/Tests/gray");
            customImage.setFinalImageMatrix(originalImage);
            customImage.setOriginalImageMatrix(originalImage);
        }
    }

    private void substituteWindows(int[][][] originalImage, int halfDimension, int centralPointRow, int centralPointColumn, PatchSimilarity similarity, int[][] objectMap) {
        for (int ik = similarity.getRow() - halfDimension, iu = centralPointRow - halfDimension;
             ik <= similarity.getRow() + halfDimension; ik++, iu++) {
            for (int jk = similarity.getColumn() - halfDimension, ju = centralPointColumn - halfDimension;
                 jk <= similarity.getColumn() + halfDimension; jk++, ju++) {
//                if (originalImage[iu][ju][0] == 0 && originalImage[iu][ju][1] == 0 && originalImage[iu][ju][2] == 0){
                for (int k = 0; k < 3; k++) {
                        if (iu >= 0 && ik >= 0 && ju >= 0 && jk >= 0) {
                            originalImage[iu][ju][k] = originalImage[ik][jk][k];
                        }
                    }
//                }
                if (iu >= 0 && ju >= 0) {
                    objectMap[iu][ju] = BLACK_0;
                }
            }
        }
    }

    private int[][][] getWindowForCentralPoint(int[][][] originalImage, int halfDimension, int centralPointRow, int centralPointColumn) {
        int[][][] unknownRegion = new int[WINDOW_DIMENSION][WINDOW_DIMENSION][3];
        for (int row = centralPointRow - halfDimension; row <= centralPointRow + halfDimension; row++) {
            for (int column = centralPointColumn - halfDimension; column <= centralPointColumn + halfDimension; column++) {
                for (int color = 0; color < 3; color++) {
                    if (row - centralPointRow + halfDimension >= 0
                            && column - centralPointColumn + halfDimension >= 0
                            && row >= 0 && column >= 0) {
                        unknownRegion[row - centralPointRow + halfDimension]
                                [column - centralPointColumn + halfDimension]
                                [color] = originalImage[row][column][color];
                    }
                }
            }
        }
        return unknownRegion;
    }
}
