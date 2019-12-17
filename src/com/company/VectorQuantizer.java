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

    class CompressionData {
        String blockSize;
        String dictionarySize;
        String extraRows;
        String extraColumns;
        String imageWidth;
        String imageHeight;
        String dictionary;
        String compressedImageSize;
        String compressedImage;

        public CompressionData(String blockSize, String dictionarySize, String extraRows, String extraColumns, String imageWidth, String imageHeight, String dictionary, String compressedImageSize, String compressedImage) {
            this.blockSize = blockSize;
            this.dictionarySize = dictionarySize;
            this.extraRows = extraRows;
            this.extraColumns = extraColumns;
            this.imageWidth = imageWidth;
            this.imageHeight = imageHeight;
            this.dictionary = dictionary;
            this.compressedImageSize = compressedImageSize;
            this.compressedImage = compressedImage;
        }
    }

    private int maxBlocks, blockSize, extraRows = 0, extraColumns = 0;
    private int[][] image, oldImage;
    private int[] compressedImage;
    private List<Block> imageBlocks;
    private List<Node> leafs = new ArrayList<>(); // to store all leafs to help in splitting

    VectorQuantizer(int[][] image, int blockSize, int maxBlocks) {

        this.blockSize = blockSize;
        this.maxBlocks = getNearestPower(maxBlocks);

        this.image = image;
        this.oldImage = image;
    }

    VectorQuantizer(CompressionData compressionData) throws Exception {
        int width, height;
        try {
            blockSize = Integer.parseInt(compressionData.blockSize.trim());
            extraColumns = Integer.parseInt(compressionData.extraColumns.trim());
            extraRows = Integer.parseInt(compressionData.extraRows.trim());
            maxBlocks = Integer.parseInt(compressionData.dictionarySize.trim()); // dictionary size = max number of blocks
            width = Integer.parseInt(compressionData.imageWidth.trim());
            height = Integer.parseInt(compressionData.imageHeight.trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw e;
        }
        stringDictionaryFill(compressionData.dictionary);
        buildImage(stringCompressedImageToArray(compressionData.compressedImage),width,height);



    }
    private void generateBlocks() {
        int numberOfBlocks = (image.length * image[0].length) / (blockSize * blockSize); // number of blocks = image area/block area
        List<Block> ret = new ArrayList<>();

        for (int i = 0; i < numberOfBlocks; ++i) {
            ret.add(new Block(image, i, blockSize));
        }

        this.imageBlocks = ret;
    }

    // rounds the number to nearest power of 2 to ensure that max blocks is power of 2
    private int getNearestPower(int n) {
        return (1 << (int) (Math.log(n) / Math.log(2)));
    }

    /*private void distributeBlocks() {
        List<Node> ret = new ArrayList<>(); // old leafs

        for (Node leaf : leafs) {
            ret.add(new Node(new Block(leaf.parent.data, Block.BLOCK_TYPE.DEFAULT))); // leafs already contains blocks
        }

        for (Block currentBlock : ImageBlocks) {
            double minDistance = Double.MAX_VALUE;
            Node minLeaf = ret.get(0);
            for (Node currentLeaf : ret) {
                double distance = currentBlock.getDistance(currentLeaf.parent);
                if (Double.compare(distance, minDistance) < 0) {
                    minDistance = distance;
                    minLeaf = currentLeaf;
                }
            }

            minLeaf.addBlock(currentBlock);
        }
        leafs = ret;
    }*/
    private void distributeBlocks() {

        for (Node leaf : leafs) {
            leaf.blocks.clear();
        }

        for (Block currentBlock : imageBlocks) {
            double minDistance = Double.MAX_VALUE;
            Node minLeaf = leafs.get(0);
            for (Node currentLeaf : leafs) {
                double distance = currentBlock.getDistance(currentLeaf.parent);
                if (Double.compare(distance, minDistance) < 0) {
                    minDistance = distance;
                    minLeaf = currentLeaf;
                }
            }

            minLeaf.addBlock(currentBlock);
        }
    }
    private void split() {
        //splits all leafs
        List<Node> ret = new ArrayList<>();
        for (Node currentLeaf : leafs) {

            double[] average = currentLeaf.getAverage();
            ;
            Block leftParent = new Block(average, Block.BLOCK_TYPE.ROUND);
            Block rightParent = new Block(average, Block.BLOCK_TYPE.CEIL);

            ret.add(new Node(leftParent));
            ret.add(new Node(rightParent));
        }
        leafs = ret;
    }
    private int compareAveragesArrays(double[] first, double[] second) {
        if (first == null || second == null) return Integer.MAX_VALUE;
        for (int i = 0; i < first.length; ++i) {
            if (first.length != second.length || first[i] != second[i])
                return Math.min(Double.compare(first[i], second[i]), 1);
        }
        return 0;
    }
    private boolean enhanceDistribution() {
        for (Node leaf : leafs) {
            leaf.parent.data = leaf.getAverage();
        }
        distributeBlocks();

        boolean ret = false;
        for (Node leaf : leafs) {
            if (compareAveragesArrays(leaf.getAverage(), leaf.parent.data) != 0) {
                ret = true;
            }
        }
        return ret;
    }

    private void buildCompressedImage() {
        compressedImage = new int[(image.length * image[0].length) / (blockSize * blockSize)]; // number of blocks = ImageArea / blocks area
        for (int i = 0; i < leafs.size(); ++i) {
            for (Block currentBlock : leafs.get(i).blocks) {
                compressedImage[currentBlock.index] = i;
            }
        }
    }

    private String dictionaryToString() {
        StringBuilder ret = new StringBuilder("");
        for (Node curr : leafs) {
            for (double val : curr.parent.data) {
                ret.append((int) val).append(" ");
            }
            ret = new StringBuilder(ret.toString().trim());
            ret.append("\n");
        }
        return ret.toString().trim();
    }

    private String compressedImageToString() {
        StringBuilder ret = new StringBuilder("");
        for (int i : compressedImage) ret.append(i).append(" ");
        return ret.toString().trim();
    }
    private CompressionData getCompressionData() {
        return new CompressionData(blockSize + "", maxBlocks + "", extraRows + "", extraColumns + "",
                image.length + "", image[0].length + "", dictionaryToString(), compressedImage.length + "",
                compressedImageToString());
    }
    private void buildImageFromBlocks() {
        // TODO : Retrieve image here
    }

    private void resizeImage() {
        extraRows = image.length % blockSize;
        extraColumns = image.length % blockSize;
        if (extraRows != 0) extraRows = blockSize - extraRows; // 12 % 5 = 2 so i need to add 3
        if (extraColumns != 0) extraColumns = blockSize - extraColumns;

        int[][] resizedImage = new int[image.length + extraRows][image[0].length + extraColumns];

        /*for (int i = 0; i < resizedImage.length; ++i) {
            for (int j = 0; j < resizedImage[0].length; ++j) {
                resizedImage[i][j] = ((i >= image.length || j >= image[0].length) ? 0 : image[i][j]);
            }
        } */
        for (int i = 0; i < image.length; ++i) {
            System.arraycopy(image[i], 0, resizedImage[i], 0, image[i].length);
        }
        image = resizedImage;
    }

    private void trimImage() {
        int[][] resizedImage = new int[image.length - extraRows][image[0].length - extraColumns];
        for (int i = 0; i < resizedImage.length; ++i) {
            System.arraycopy(image[i], 0, resizedImage[i], 0, resizedImage[i].length);
        }
        image = resizedImage;
    }
    private int[] stringCompressedImageToArray(String compressedImage){
        // TODO : from string to array conversion here
        return null;
    }
    private void buildImage(int[] compressedImage , int width , int height){
        // TODO : reconstruct image from the compressed image here


        oldImage = image;
        trimImage();
    }
    private void stringDictionaryFill(String dictionary) {
        // TODO : fill dictionary here
    }

    public CompressionData compress() {
        resizeImage();
        generateBlocks();

        Node root = new Node(null);
        root.setBlocks(this.imageBlocks);
        leafs.add(root);

        while (leafs.size() < maxBlocks) {
            split();
            distributeBlocks();
        }

        int MAX_LOOP = 10;
        while (MAX_LOOP-- > 0) {
            if (!enhanceDistribution()) break;
        }
        buildCompressedImage();
        return getCompressionData();
    }

    public int[][] Decompress() {
        return image;
    }
}
