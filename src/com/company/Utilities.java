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
        File file = new File(path);
        BufferedImage img = ImageIO.read(file);

        int width = img.getWidth();
        int height = img.getHeight();

        int[][] ret = new int[width][height];

        Raster raster = img.getData();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ret[i][j] = raster.getSample(i, j, 0);
            }
        }

        return ret;
    }
    
    // takes directory
    /*public static boolean saveArrayToImage(int[][] imageArray,String to,String name) throws IOException {
        BufferedImage image = new BufferedImage(imageArray.length, imageArray[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < imageArray.length; i++) {
            for (int j = 0; j < imageArray[i].length; j++) {
                image.setRGB(i, j, imageArray[i][j]);
            }
        }

        String path = to + "/" + name +  ".png";
        File ImageFile = new File(path);
        ImageIO.write(image, "png", ImageFile);

        return true;
    }*/
    public static boolean saveArrayToImage(int[][] imageArray,String to,String name) throws IOException {
        int[] result = Arrays.stream( transposeImage(imageArray))
                .flatMapToInt(Arrays::stream)
                .toArray();
        BufferedImage outputImage = new BufferedImage(imageArray.length,  imageArray[0].length, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = outputImage.getRaster();
        raster.setSamples(0, 0, imageArray.length,  imageArray[0].length, 0, result);
        ImageIO.write(outputImage, "png", new File( to + "/" + name +  ".png"));

        return true;
    }
    static int[][] transposeImage (int[][] imageArray) {
        if (imageArray == null || imageArray.length == 0)//empty or unset array, nothing do to here
            return imageArray;

        int width = imageArray.length;
        int height = imageArray[0].length;

        int[][] array_new = new int[height][width];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                array_new[j][i] = imageArray[i][j];
            }
        }
        return array_new;
    }
    public static boolean saveCompressionData(CompressionData compressionData, String to , String name) throws Exception {
        String path = to + "/" + name + ".txt";
        if (Files.exists(Paths.get(path))) throw new Exception("File already exists cannot save to it");
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println(compressionData.blockSize);
        writer.println(compressionData.dictionarySize);
        writer.println(compressionData.extraRows);
        writer.println(compressionData.extraColumns);
        writer.println(compressionData.imageWidth);
        writer.println(compressionData.imageHeight);
        writer.println(compressionData.dictionary);
        writer.println(compressionData.compressedImageSize);
        writer.println(compressionData.compressedImage);
        writer.close();
        return true;
    }
    public static CompressionData readCompressionData (String path) throws IOException {
        String blockSize, dictionarySize,  extraRows, extraColumns, imageWidth,  imageHeight, dictionary, compressedImageSize, compressedImage;
        try(BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
                blockSize = br.readLine();
                dictionarySize = br.readLine();
                extraRows = br.readLine();
                extraColumns = br.readLine();
                imageWidth = br.readLine();
                imageHeight = br.readLine();
                dictionary = br.readLine();
                compressedImageSize = br.readLine();
                compressedImage = br.readLine();
        }
        return new CompressionData(blockSize, dictionarySize,  extraRows, extraColumns, imageWidth,  imageHeight, dictionary, compressedImageSize, compressedImage);
    }
    public static VectorQuantizer readCompressedImage(CompressionData compressionData) throws Exception {
            return new VectorQuantizer(compressionData);
    }
}
