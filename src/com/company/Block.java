package com.company;

import java.util.Arrays;

public class Block {
    int[] data;

    enum BLOCK_TYPE{CEIL , DEFAULT}

    Block(int[][] image, int blockIndex, int blockSize){
        this.data = new int[blockSize*blockSize];

        int width = image[0].length;
        int height = image.length;

        int blocksPerRow = width/blockSize;
        int blocksPerCol = height/blockSize;


        int startRow = (blockIndex / blocksPerRow) * blockSize ;
        int startCol = (blockIndex % blocksPerCol) * blockSize;

        int idx = 0;
        for (int i = startRow ; i < startRow + blockSize ; ++i){
            for (int j = startCol ; j < startCol + blockSize ; ++j ){
                this.data[idx++] = image[i][j];
            }
        }
    }

    Block(double[] data, BLOCK_TYPE type){
        this.data = new int[data.length];

        if (type == BLOCK_TYPE.CEIL){
            for (int i = 0 ; i<data.length ; ++i){
                this.data[i] = (int) (data[i]+1);
            }
        }

        else{
            for (int i = 0 ; i<data.length ; ++i){
                this.data[i] = (int) (data[i]);
            }
        }
    }

    long getDistance (Block other){
        long sum = 0;
        for (int i = 0 ; i<data.length ; ++i){
            long difference = (data[i] - other.data[i]);
            sum+= difference*difference;
        }
        return sum;
    }

    double getDistance (double[] other){
        double sum = 0;
        for (int i = 0 ; i<data.length ; ++i){
            double difference = (data[i] - other[i]);
            sum+= difference*difference;
        }
        return sum;
    }

    static double[] getAverage (Block[] blocks){
        double[] ret = new double[blocks[0].data.length];
        Arrays.fill(ret,0);

        for (int i = 0 ; i<ret.length ; ++i){
            for (Block block: blocks) {
                ret[i]+=block.data[i];
            }
            ret[i]/=blocks.length;
        }
        return ret;
    }
}
