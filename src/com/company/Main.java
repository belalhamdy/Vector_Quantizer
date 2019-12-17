package com.company;

import javax.swing.*;
import java.io.IOException;

public class Main {
    final static int[][] LectureImage = new int[][]{
            { 1 , 2 , 7 , 9 , 4 , 11},
            { 3 , 4 , 6 , 6 , 12, 12},
            { 4 , 9 , 15, 14, 9 , 9 },
            { 10, 10, 20, 18, 8 , 8 },
            { 4 , 3 , 17, 16, 1 , 4 },
            { 4 , 5 , 18, 18, 5 , 6 }
    };
    enum ErrorFunctionTypeENUM{
        NORMAL,SQUARED;
    }
//    public static ErrorFunctionTypeENUM ErrorFunctionType = ErrorFunctionTypeENUM.SQUARED;
    public static ErrorFunctionTypeENUM ErrorFunctionType = ErrorFunctionTypeENUM.NORMAL;

    public static void main(String[] args) {
        int[][] im = new int[0][];
        try {
            Utilities.saveArrayToImage(LectureImage,"S://","test");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
             im = Utilities.readImageToArray("S://test.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int[] ints : im) {
            for (int j = 0; j < im[0].length; ++j) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();

        for (int[] ints : LectureImage) {
            for (int j = 0; j < LectureImage[0].length; ++j) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }
        LectureExample().compress();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new GUI();
    }
    static VectorQuantizer LectureExample(){

        return new VectorQuantizer(LectureImage,2,4);
    }
}
