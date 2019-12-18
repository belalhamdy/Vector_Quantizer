package com.company;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    final static int[][] LectureImage = new int[][]{
            {1, 2, 7, 9, 4, 11},
            {3, 4, 6, 6, 12, 12},
            {4, 9, 15, 14, 9, 9},
            {10, 10, 20, 18, 8, 8},
            {4, 3, 17, 16, 1, 4},
            {4, 5, 18, 18, 5, 6}
    };

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        new GUI();
        int[][] test = new VectorQuantizer(LectureExample().compress()).decompress();
        for (int[] ints : test) {
            System.out.println(Arrays.toString(ints));
        }
    }

    private static VectorQuantizer LectureExample() {

        return new VectorQuantizer(LectureImage, 2, 4);
    }
}
