package com.company;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;

public class Utilities {
    public static int[][] readImageToArray(BufferedImage img) {
        return readImageToArray(img, -1);
    }

    public static int[][] readImageToArray(BufferedImage img, int band) {
        int width = img.getWidth();
        int height = img.getHeight();

        int[][] ret = new int[height][width];

        Raster raster = img.getData();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ret[i][j] = 0;
                if (band == -1) {
                    for (int s = 0; s < Math.min(raster.getNumBands(), 3); ++s)
                        ret[i][j] += raster.getSample(j, i, s);
                    ret[i][j] /= raster.getNumBands();
                } else
                    ret[i][j] = raster.getSample(j, i, band);
            }
        }

        return ret;
    }

    public static BufferedImage saveArrayToImage(int[][] imageArray) {
        int[] result = Arrays.stream(imageArray).flatMapToInt(Arrays::stream).toArray();
        BufferedImage outputImage = new BufferedImage(imageArray[0].length, imageArray.length, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = outputImage.getRaster();

        for (int b = 0; b < raster.getNumBands(); ++b)
            raster.setSamples(0, 0, imageArray[0].length, imageArray.length, b, result);

        return outputImage;
    }

    public static void saveCompressionData(CompressionData compressionData, File path) throws Exception {
        FileOutputStream fos = new FileOutputStream(path);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(compressionData);
        oos.flush();
        oos.close();

        /*
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println(compressionData.blockSize);
        writer.println(compressionData.dictionarySize);
        writer.println(compressionData.extraWidth);
        writer.println(compressionData.extraHeight);
        writer.println(compressionData.imageWidth);
        writer.println(compressionData.imageHeight);
        writer.println(compressionData.dictionary);
        writer.println(compressionData.compressedImageSize);
        writer.println(compressionData.compressedImage);
        writer.close();
        */
    }

    public static CompressionData readCompressionData(File path) throws Exception {
        ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)));
        CompressionData cd = (CompressionData) input.readObject();
        input.close();
        return cd;

        /*
        String blockSize, dictionarySize, extraWidth, extraHeight, imageWidth, imageHeight, dictionary, compressedImageSize, compressedImage;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            blockSize = br.readLine();
            dictionarySize = br.readLine();
            extraWidth = br.readLine();
            extraHeight = br.readLine();
            imageWidth = br.readLine();
            imageHeight = br.readLine();
            dictionary = br.readLine();
            compressedImageSize = br.readLine();
            compressedImage = br.readLine();
        }
        return new CompressionData(blockSize, dictionarySize, extraWidth, extraHeight, imageWidth, imageHeight, dictionary, compressedImageSize, compressedImage);
        */
    }

    public static VectorQuantizer readCompressedImage(CompressionData compressionData) {
        return new VectorQuantizer(compressionData);
    }
}
