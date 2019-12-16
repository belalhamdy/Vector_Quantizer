package com.company;

import java.util.ArrayList;
import java.util.List;

public class VectorQuantizer {
    class Node {
        List<Block> blocks;
        Block parent;

        Node(Block parent) {
            this.parent = parent;
        }


        public double[] getAverage() {
            return Block.getAverage(this.blocks);
        }

        public void addBlock(Block block) {
            if (this.blocks == null) this.blocks = new ArrayList<>();
            this.blocks.add(block);
        }

        public void setBlocks(List<Block> blocks) {
            this.blocks = blocks;
        }
    }

    private int maxBlocks, blockSize, extraRows = 0, extraColumns = 0;
    private int[][] image;
    private List<Block> ImageBlocks;
    private Node root;
    private List<Node> leafs = new ArrayList<>(); // to store all leafs to help in splitting

    VectorQuantizer(int[][] image, int blockSize, int maxBlocks) {

        this.blockSize = blockSize;
        this.maxBlocks = getNearestPower(maxBlocks);

        this.image = resizeImage(image, blockSize);
    }

    public int getBlockSize() {
        return blockSize;
    }

    public List<Node> getBlocksDictionary() {
        return leafs;
    }

    public int[][] compress() {
        this.ImageBlocks = generateBlocks();

        root = new Node(null);
        root.setBlocks(this.ImageBlocks);
        leafs.add(root);

        while (leafs.size() < maxBlocks) {
             split();
            distributeBlocks();
        }

        int MAX_LOOP = 10;
        while (MAX_LOOP-- > 0){
            if (!enhanceDistribution()) break;
        }

        // here you have the final blocks
        return null;
    }

    // rounds the number to nearest power of 2 to ensure that max blocks is power of 2
    private int getNearestPower(int n) {
        return (1 << (int) (Math.log(n) / Math.log(2)));
    }

    private void distributeBlocks() {
        List<Node> ret = new ArrayList<>();

        for (Node leaf : leafs) {
            ret.add(new Node(new Block(leaf.parent.data, Block.BLOCK_TYPE.DEFAULT)));
        }

        for (Block currentBlock : ImageBlocks) {
            double minDistance = Double.MAX_VALUE;
            Node minLeaf = ret.get(0);
                for (Node currentLeaf : ret){
                    double distance = currentBlock.getDistance(currentLeaf.parent);
                    if (Double.compare(distance,minDistance) < 0){
                        minDistance = distance;
                        minLeaf = currentLeaf;
                    }
                }

                minLeaf.addBlock(currentBlock);
        }
        leafs = ret;
    }

    private void split() {
        //splits all leafs
        List<Node> ret = new ArrayList<>();
        for (Node currentLeaf : leafs) {

            double[] average = currentLeaf.getAverage();;
            Block leftParent = new Block(average, Block.BLOCK_TYPE.ROUND);
            Block rightParent = new Block(average, Block.BLOCK_TYPE.CEIL);

            ret.add(new Node(leftParent));
            ret.add(new Node(rightParent));
        }
        leafs = ret;
    }

    private boolean enhanceDistribution(){
        for (Node leaf : leafs){
            leaf.parent.data = leaf.getAverage();
        }
        distributeBlocks();

        boolean ret = true;
        for (Node leaf : leafs) {
            if (compareAverages(leaf.getAverage(), leaf.parent.data) != 0) {
                ret = false;
            }
        }
        return ret;
    }

    private int compareAverages(double[] first , double[] second){
        if (first == null || second == null) return Integer.MAX_VALUE;
        for (int i = 0 ; i<first.length; ++i){
            if (first.length != second.length || first[i] != second[i]) return Math.min(Double.compare(first[i] , second[i]),1);
        }
        return 0;
    }

    private List<Block> generateBlocks() {
        int numberOfBlocks = (image.length * image[0].length) / (blockSize * blockSize); // number of blocks = image area/block area
        List<Block> ret = new ArrayList<>();

        for (int i = 0; i < numberOfBlocks; ++i) {
            ret.add(new Block(image, i, blockSize));
        }

        return ret;
    }

    private int[][] resizeImage(int[][] image, int blockSize) {
        //TODO : function to resize image and adding extra rows and extra columns to make the image able to be compressed (EDIT IN EXTRA ROWS/COLUMNS)
        return image;
    }
}
