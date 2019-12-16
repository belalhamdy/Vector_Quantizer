package com.company;

import javax.swing.*;

public class Main {
    enum ErrorFunctionTypeENUM{
        NORMAL,SQUARED;
    }
//    public static ErrorFunctionTypeENUM ErrorFunctionType = ErrorFunctionTypeENUM.SQUARED;
    public static ErrorFunctionTypeENUM ErrorFunctionType = ErrorFunctionTypeENUM.NORMAL;

    public static void main(String[] args) {
        LectureExample().compress();
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new GUI();
    }
    static VectorQuantizer LectureExample(){
        int[][] image = new int[][]{
                { 1 , 2 , 7 , 9 , 4 , 11},
                { 3 , 4 , 6 , 6 , 12, 12},
                { 4 , 9 , 15, 14, 9 , 9 },
                { 10, 10, 20, 18, 8 , 8 },
                { 4 , 3 , 17, 16, 1 , 4 },
                { 4 , 5 , 18, 18, 5 , 6 }
        };

        for (int[] ints : image) {
            for (int j = 0; j < image[0].length; ++j) {
                System.out.print(ints[j] + " ");
            }
            System.out.println();
        }
        return new VectorQuantizer(image,2,4);
    }
}
