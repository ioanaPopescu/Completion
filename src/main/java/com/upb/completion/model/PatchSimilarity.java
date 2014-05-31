package com.upb.completion.model;

/**
 * Created by Ioana Popescu on 5/11/14.
 */
public class PatchSimilarity {
    private int row;
    private int column;
    private double distance;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
