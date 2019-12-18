package com.company;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class Utilities {
    public static int[][] readImageToArray(String path) throws IOException {
        return readImageToArray(path, -1);
    }

    public static int[][] readImageToArray(String path, int band) throws IOException {
        File file = new File(path);
        BufferedImage img = ImageIO.read(file);

        int width = img.getWidth();
        int height = img.getHeight();

        int[][] ret = new int[height][width];

        Raster raster = img.getData();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ret[i][j] = 0;
                if (band == -1) {
                    for (int s = 0; s < Math.max(raster.getNumBands(), 3); ++s)
                        ret[i][j] += raster.getSample(j, i, s);
                    ret[i][j] /= raster.getNumBands();
                } else
                    ret[i][j] = raster.getSample(j, i, band);
            }
        }

        return ret;
    }

    public static void saveArrayToImage(int[][] imageArray, String to, String name) throws IOException {
        int[] result = Arrays.stream(imageArray).flatMapToInt(Arrays::stream).toArray();
        BufferedImage outputImage = new BufferedImage(imageArray[0].length, imageArray.length, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = outputImage.getRaster();

        for (int b = 0; b < raster.getNumBands(); ++b)
            raster.setSamples(0, 0, imageArray[0].length, imageArray.length, b, result);

        ImageIO.write(outputImage, "png", new File(to + "/" + name + ".png"));

    }

    public static boolean saveCompressionData(CompressionData compressionData, String to, String name) throws Exception {
        String path = to + "/" + name + ".txt";
        if (Files.exists(Paths.get(path))) throw new Exception("File already exists cannot save to it");
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
        return true;
    }

    public static CompressionData readCompressionData(String path) throws IOException {
        String blockSize, dictionarySize, extraWidth, extraHeight, imageWidth, imageHeight, dictionary, compressedImageSize, compressedImage;
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
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
    }

    public static VectorQuantizer readCompressedImage(CompressionData compressionData) {
        return new VectorQuantizer(compressionData);
    }
}
