package com.company;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.*;

public class Block {
    enum ErrorFunctionType {
        NORMAL, SQUARED
    }

    public static ErrorFunctionType errorFunctionType = ErrorFunctionType.NORMAL;

    double[] data;
    int index;

    enum BlockType {CEIL, FLOOR}

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

    Block(double[] data, BlockType type) {
        this.data = new double[data.length];

        if (type == BlockType.CEIL) {
            for (int i = 0; i < data.length; ++i) {
                //not ceil because if data[i] was an integer, both ceil and floor would be the same.
                this.data[i] = Math.floor(data[i] + 1);
            }
        } else if (type == BlockType.FLOOR) {
            for (int i = 0; i < data.length; ++i) {

                this.data[i] = Math.floor(data[i]);
            }
        } else arraycopy(data, 0, this.data, 0, data.length);
    }

    double getDistance(Block other) {
        double sum = 0;
        for (int i = 0; i < data.length; ++i) {
            double difference = Math.abs(data[i] - other.data[i]);
            if (errorFunctionType == ErrorFunctionType.SQUARED) difference *= difference;
            sum += difference;
        }
        return sum;
    }

    static double[] getAverage(List<Block> blocks) {
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
