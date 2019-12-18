package com.company;

public class CompressionData {
    final int maxBlocks;
    final int blockSize;
    final int dictionarySize;
    final int extraHeight;
    final int extraWidth;
    final int imageWidth;
    final int imageHeight;
    final int compressedImageSize;

    String compressedImage;
    String dictionary;

    public CompressionData(int blockSize, int dictionarySize, int extraWidth, int extraHeight, int imageWidth, int imageHeight, String dictionary, int compressedImageSize, String compressedImage) {
        this.blockSize = blockSize;
        this.dictionarySize = dictionarySize;
        this.extraWidth = extraWidth;
        this.extraHeight = extraHeight;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.maxBlocks = dictionarySize; // dictionary size = max number of blocks
        this.compressedImageSize = compressedImageSize;

        this.dictionary = dictionary;
        this.compressedImage = compressedImage;
    }
    public CompressionData(String blockSize, String dictionarySize, String extraWidth, String extraHeight, String imageWidth, String imageHeight, String dictionary, String compressedImageSize, String compressedImage) {
        this.blockSize = Integer.parseInt(blockSize.trim());
        this.dictionarySize = Integer.parseInt(dictionarySize.trim());
        this.extraWidth = Integer.parseInt(extraWidth.trim());
        this.extraHeight = Integer.parseInt(extraHeight.trim());
        this.imageWidth = Integer.parseInt(imageWidth.trim());
        this.imageHeight = Integer.parseInt(imageHeight.trim());
        this.maxBlocks = Integer.parseInt(dictionarySize.trim()); // dictionary size = max number of blocks

        this.compressedImageSize = Integer.parseInt(compressedImageSize);

        this.dictionary = dictionary;
        this.compressedImage = compressedImage;
    }
}
