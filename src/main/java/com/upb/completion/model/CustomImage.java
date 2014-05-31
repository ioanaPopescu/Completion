package com.upb.completion.model;

import java.awt.image.BufferedImage;

/**
 * Created by Ioana Popescu on 5/8/14.
 */
public class CustomImage {
    private BufferedImage originalImage;
    private BufferedImage grayImage;
    private BufferedImage withoutObjectImage;
    private BufferedImage finalImage;
    private String name;
    private int width;
    private int height;
    private int[][] edgeObject;
    private int[][] grayImageMatrix;
    private int[][] objectImageMatrix;
    private int[][][] originalImageMatrix;
    private int[][][] withoutObjectMatrix;
    private int[][][] finalImageMatrix;

    public CustomImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
        this.width = originalImage.getWidth();
        this.height = originalImage.getHeight();
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BufferedImage getGrayImage() {
        return grayImage;
    }

    public void setGrayImage(BufferedImage grayImage) {
        this.grayImage = grayImage;
    }

    public BufferedImage getWithoutObjectImage() {
        return withoutObjectImage;
    }

    public void setWithoutObjectImage(BufferedImage withoutObjectImage) {
        this.withoutObjectImage = withoutObjectImage;
    }

    public int[][] getEdgeObject() {
        return edgeObject;
    }

    public void setEdgeObject(int[][] edgeObject) {
        this.edgeObject = edgeObject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[][] getGrayImageMatrix() {
        return grayImageMatrix;
    }

    public void setGrayImageMatrix(int[][] grayImageMatrix) {
        this.grayImageMatrix = grayImageMatrix;
    }

    public int[][][] getOriginalImageMatrix() {
        return originalImageMatrix;
    }

    public void setOriginalImageMatrix(int[][][] originalImageMatrix) {
        this.originalImageMatrix = originalImageMatrix;
    }

    public int[][][] getWithoutObjectMatrix() {
        return withoutObjectMatrix;
    }

    public void setWithoutObjectMatrix(int[][][] withoutObjectMatrix) {
        this.withoutObjectMatrix = withoutObjectMatrix;
    }

    public BufferedImage getFinalImage() {
        return finalImage;
    }

    public void setFinalImage(BufferedImage finalImage) {
        this.finalImage = finalImage;
    }

    public int[][][] getFinalImageMatrix() {
        return finalImageMatrix;
    }

    public void setFinalImageMatrix(int[][][] finalImageMatrix) {
        this.finalImageMatrix = finalImageMatrix;
    }

    public int[][] getObjectImageMatrix() {
        return objectImageMatrix;
    }

    public void setObjectImageMatrix(int[][] objectImageMAtrix) {
        this.objectImageMatrix = objectImageMAtrix;
    }
}
