package com.company;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.*;

public class Block {
    double[] data;
    int index;
    enum BLOCK_TYPE{CEIL,ROUND , DEFAULT}

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        for (double d : data){
            ret.append(d).append(" ");
        }
        return ret.toString();
    }

    Block(int[][] image, int blockIndex, int blockSize){
        this.data = new double[blockSize*blockSize];
        this.index = blockIndex;

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
        this.data = new double[data.length];

        if (type == BLOCK_TYPE.CEIL){
            for (int i = 0 ; i<data.length ; ++i){
                this.data[i] = (int) (data[i]+1);
            }
        }

        else if (type == BLOCK_TYPE.ROUND){
            for (int i = 0 ; i<data.length ; ++i){
                this.data[i] = (int) (data[i]);
            }
        }
        else arraycopy(data, 0, this.data, 0, data.length);
    }

    double getDistance (Block other){
        return getDistance(other.data);
    }
    double getDistance (double[] other){
        double sum = 0;
        for (int i = 0 ; i<data.length ; ++i){
            double difference = Math.abs(data[i] - other[i]);
            if (com.company.Main.ErrorFunctionType == Main.ErrorFunctionTypeENUM.SQUARED) difference*=difference;
            sum+= difference;
        }
        return sum;
    }

    static double[] getAverage (List<Block> blocks){
        if (blocks == null || blocks.isEmpty()) return null;
        double[] ret = new double[blocks.get(0).data.length];
        Arrays.fill(ret,0);

        for (int i = 0 ; i<ret.length ; ++i){
            for (Block block: blocks) {
                ret[i]+=block.data[i];
            }
            ret[i]/=blocks.size();
        }
        return ret;
    }
}
