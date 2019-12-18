package com.company;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.*;

public class Block {

    enum ErrorFunctionType {
        NORMAL, SQUARED
    }

    private static ErrorFunctionType errorFunctionType = ErrorFunctionType.NORMAL;

    double[] data;
    int index;

    enum BlockType {CEIL, FLOOR, DEFAULT}

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (double d : data) {
            ret.append(d).append(" ");
        }
        return ret.toString();
    }

    Block(int[][] image, int blockIndex, int blockSize) {
        this.data = new double[blockSize * blockSize];
        this.index = blockIndex;

        int width = image[0].length;

        int blocksPerRow = width / blockSize;

        int si = (blockIndex / blocksPerRow) * blockSize;
        int sj = (blockIndex % blocksPerRow) * blockSize;

        int idx = 0;
        for (int i = si; i < si + blockSize; ++i) {
            for (int j = sj; j < sj + blockSize; ++j) {
                this.data[idx++] = image[i][j];
            }
        }
    }

    public Block(double[] averages) {
        this(averages, BlockType.DEFAULT);
    }

    public Block(double[] data, BlockType type) {
        this.data = new double[data.length];

        for (int i = 0; i < data.length; ++i) {
            switch (type) {
                case CEIL:
                    //not ceil because if data[i] was an integer, both ceil and floor would be the same.
                    this.data[i] = Math.floor(data[i] + 1);
                    break;
                case FLOOR:
                    this.data[i] = Math.floor(data[i]);
                    break;
                default:
                    this.data[i] = Math.round(data[i]);
                    break;
            }
        }
    }

    double getDistanceTo(Block other) {
        double sum = 0;
        for (int i = 0; i < data.length; ++i) {
            double difference = Math.abs(data[i] - other.data[i]);
            if (errorFunctionType == ErrorFunctionType.SQUARED) difference *= difference;
            sum += difference;
        }
        return sum;
    }

    static double[] getAverages(List<Block> blocks) {
        if (blocks.isEmpty()) return null;
        double[] ret = new double[blocks.get(0).data.length];
        Arrays.fill(ret, 0);

        for (int i = 0; i < ret.length; ++i) {
            for (Block block : blocks) {
                ret[i] += block.data[i];
            }
            ret[i] /= blocks.size();
        }
        return ret;
    }
}
