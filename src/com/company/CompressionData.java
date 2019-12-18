package com.company;

public class CompressionData {
    String blockSize;
    String dictionarySize;
    String extraRows;
    String extraColumns;
    String imageWidth;
    String imageHeight;
    String dictionary;
    String compressedImageSize;
    String compressedImage;

    public CompressionData(String blockSize, String dictionarySize, String extraRows, String extraColumns, String imageWidth, String imageHeight, String dictionary, String compressedImageSize, String compressedImage) {
        this.blockSize = blockSize;
        this.dictionarySize = dictionarySize;
        this.extraRows = extraRows;
        this.extraColumns = extraColumns;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.dictionary = dictionary;
        this.compressedImageSize = compressedImageSize;
        this.compressedImage = compressedImage;
    }
}
