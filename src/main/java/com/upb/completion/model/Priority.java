package com.upb.completion.model;

/**
 * Created by Ioana Popescu on 5/9/14.
 */
public class Priority {
    private int row;
    private int column;
    private double priority;

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

    public double getPriority() {
        return priority;
    }

    public void setPriority(double priority) {
        this.priority = priority;
    }
}
