package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class Utilities {
    public int[][] readImageToArray(String path) throws IOException {
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
    public boolean saveArrayToImage(int[][] imageArray,String to,String name) throws IOException {
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
    }
}
