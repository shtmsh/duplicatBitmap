package com.hprof.bitmap;

import com.squareup.haha.perflib.Instance;

public class AnaylizerResult {
    private String bufferHash;
    private String classInstance;
    private int width;
    private int height;
    private int bufferSize;
    private Instance instance;

    @Override
    public String toString() {
        return "AnaylizerResult{" +
                "bufferHash='" + bufferHash + '\'' +
                ", classInstance='" + classInstance + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", bufferSize=" + bufferSize +
                ", instance=" + instance +
                '}';
    }

    public String getBufferHash() {
        return bufferHash;
    }

    public void setBufferHash(String bufferHash) {
        this.bufferHash = bufferHash;
    }

    public String getClassInstance() {
        return classInstance;
    }

    public void setClassInstance(String classInstance) {
        this.classInstance = classInstance;
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

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }
}
