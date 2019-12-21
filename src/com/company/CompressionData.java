package com.company;

import java.io.Serializable;
import java.util.Arrays;

public class CompressionData implements Serializable {
    final int blockSize;
    final int dictionarySize;
    final int extraHeight;
    final int extraWidth;
    final int imageWidth;
    final int imageHeight;

    int[] compressedImage;
    int[][] dictionary;

    public CompressionData(int blockSize, int dictionarySize, int extraWidth, int extraHeight, int imageWidth, int imageHeight, int[][] dictionary, int[] compressedImage) {
        this.blockSize = blockSize;
        this.dictionarySize = dictionarySize;
        this.extraWidth = extraWidth;
        this.extraHeight = extraHeight;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;

        this.dictionary = dictionary;
        this.compressedImage = compressedImage;
    }
    void printToConsole(){
        System.out.println(String.format("BlockSize: %d * %d", blockSize, blockSize));
        System.out.println(String.format("No. Blocks: %d", dictionary.length));
        System.out.println(String.format("Extra Width: %d, Extra Height: %d", extraWidth, extraHeight));
        System.out.println(String.format("Image Size: %d * %d", imageWidth, imageHeight));
        System.out.println("\nCompression Dictionary: ");
        for (int[] arr : dictionary) {
            System.out.println(Arrays.toString(arr));
        }
        System.out.println("\nImage :");
        System.out.println(Arrays.toString(compressedImage));
    }
}
