package com.company;

import java.util.ArrayList;
import java.util.List;

public class VectorQuantizer {
        class Node{
            double[] average;
            List<Block> blocks;
            Block parent;

            Node(Block parent,double[] average , List<Block> blocks){
                this.average = average;
                this.blocks = blocks;
                this.parent = parent;
            }
        }

        int maxBlocks,blockSize, extraRows = 0 , extraColumns = 0;
        int[][] image;
        List<Block> ImageBlocks;
        Node root;
        List<Node> leafs = new ArrayList<>(); // to store all leafs to help in splitting

        VectorQuantizer(int[][] image, int blockSize , int maxBlocks){
            this.image = resizeImage(image,blockSize);
            this.blockSize = blockSize;
            this.maxBlocks = maxBlocks;

            this.ImageBlocks = generateBlocks();

            root = new Node(null,Block.getAverage(this.ImageBlocks),this.ImageBlocks);
            leafs.add(root);
        }

        private void split(){
            //splits all leafs and adds them to leafs list and removes the old leafs also with splitting it adds the proper block to it's parent

            for (Node currentLeaf : leafs){
                leafs.remove(currentLeaf);

                Block leftParent = new Block(currentLeaf.average, Block.BLOCK_TYPE.DEFAULT);
                Block rightParent = new Block(currentLeaf.average, Block.BLOCK_TYPE.CEIL);

                List<Block> leftBlocks = new ArrayList<>();
                List<Block> rightBlocks = new ArrayList<>();

                // Adds blocks to proper place
                for(Block currentBlock : currentLeaf.blocks){
                    long leftDistance = currentBlock.getDistance(leftParent);
                    long rightDistance = currentBlock.getDistance(rightParent);

                    if (leftDistance > rightDistance){
                        rightBlocks.add(currentBlock);
                    }
                    else if (leftDistance < rightDistance){
                        leftBlocks.add(currentBlock);
                    }
                    else{
                        // TODO : Handle equidistance case
                    }
                }

                Node leftLeaf = new Node(leftParent,Block.getAverage(leftBlocks),leftBlocks);
                Node rightLeaf = new Node(rightParent,Block.getAverage(rightBlocks),rightBlocks);

                leafs.add(leftLeaf);
                leafs.add(rightLeaf);

            }

        }

        private List<Block> generateBlocks(){
            int numberOfBlocks = (image.length * image[0].length) / (blockSize * blockSize); // number of blocks = image area/block area
            List<Block> ret = new ArrayList<>();

            for(int i = 0 ; i < numberOfBlocks; ++i){
                ret.add(new Block(image,i,blockSize));
            }

            return ret;
        }

        private int[][] resizeImage (int[][] image , int blockSize){
            //TODO : function to resize image and adding extra rows and extra columns to make the image able to be compressed (EDIT IN EXTRA ROWS/COLUMNS)
            return null;
        }
}
